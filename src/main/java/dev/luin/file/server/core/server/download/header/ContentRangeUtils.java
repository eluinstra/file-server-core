/**
 * Copyright 2020 E.Luinstra
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package dev.luin.file.server.core.server.download.header;

import java.time.Instant;

import dev.luin.file.server.core.file.Length;
import io.vavr.collection.CharSeq;
import io.vavr.collection.List;
import io.vavr.collection.Seq;
import io.vavr.control.Option;
import io.vavr.control.Try;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.val;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ContentRangeUtils
{
	public static Seq<Range> filterValidRanges(final Length length, @NonNull final Seq<Range> ranges)
	{
		return ranges.filter(r -> length.containsFirstPosition(r));
	}

	public static boolean validateIfRangeValue(final String ifRangeValue, final Instant lastModified)
	{
		if (ifRangeValue == null)
			return true;
		else if (ifRangeValue.startsWith("\""))
		{
			val hashCode = new Integer(ETag.getHashCode(lastModified)).toString();
			val etag = ifRangeValue.substring(1, ifRangeValue.length() - 1);
			return hashCode.equals(etag);
		}
		else
			return getTime(ifRangeValue).map(t -> lastModified.toEpochMilli() <= t).getOrElse(false);
	}

	static Option<Long> getTime(@NonNull final String value)
	{
		return Try.of(() -> HttpDate.IMF_FIXDATE.getDateFormat().parse(value).getTime())
				.orElse(Try.of(() -> HttpDate.RFC_850.getDateFormat().parse(value).getTime()))
				.orElse(Try.of(() -> HttpDate.ANSI_C.getDateFormat().parse(value).getTime()))
				.toOption();
	}

	public static Seq<Range> parseRangeHeader(final String value)
	{
		if (value != null && value.startsWith("bytes"))
		{
			val byteRanges = value.substring("bytes=".length());
			val ranges = CharSeq.of(byteRanges).split(",");
			return ranges.flatMap(r -> createContentRange(r));
		}
		else
			return List.empty();
	}
	
	private static Option<Range> createContentRange(@NonNull final CharSeq range)
	{
		val parts = range.split("-",2);
		return parts.headOption().flatMap(f -> Range.of(f,parts.tail().headOption().getOrNull()));
	}
}
