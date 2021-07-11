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

import dev.luin.file.server.core.file.FSFile;
import dev.luin.file.server.core.file.Length;
import dev.luin.file.server.core.server.download.DownloadException;
import dev.luin.file.server.core.server.download.DownloadRequest;
import io.vavr.collection.CharSeq;
import io.vavr.collection.List;
import io.vavr.collection.Seq;
import io.vavr.control.Option;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.Value;
import lombok.val;
import lombok.var;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ContentRange
{
	private final static String HEADER_NAME = "Range";
	@NonNull
	Seq<Range> ranges;
	
	//FIXME: move to of() and use Either
	public ContentRange(@NonNull final DownloadRequest request, @NonNull final FSFile fsFile)
	{
		var ranges = parseRangeHeader(request.getHeader(HEADER_NAME));
		if (ranges.size() > 0)
		{
			val lastModified = fsFile.getLastModified();
			if (IfRange.of(request)
					.map(r -> r.map(s -> s.isValid(lastModified))).getOrElse(Option.some(true))
					.getOrElse(true))
			{
				ranges = filterValidRanges(fsFile.getFileLength(),ranges);
				if (ranges.size() == 0)
					throw DownloadException.requestedRangeNotSatisfiable(fsFile.getLength());
			}
			else
				ranges = List.empty();
		}
		this.ranges = ranges;
	}

	static Seq<Range> parseRangeHeader(final String value)
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

	static Seq<Range> filterValidRanges(@NonNull final Length length, @NonNull final Seq<Range> ranges)
	{
		return ranges.filter(r -> r.inRange(length));
	}

	public int count()
	{
		return ranges.size();
	}
}
