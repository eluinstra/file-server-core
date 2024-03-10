/*
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

import static io.vavr.API.For;

import dev.luin.file.server.core.file.Length;
import dev.luin.file.server.core.server.download.DownloadResponse;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.collection.CharSeq;
import io.vavr.control.Option;
import io.vavr.control.Try;
import java.io.IOException;
import java.io.Writer;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import lombok.val;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@EqualsAndHashCode
@ToString
public class Range
{
	private static final String HEADER_NAME = "Content-Range";
	@NonNull
	Option<Long> first;
	@NonNull
	Option<Long> last;

	public static Range of(final Long first, final Long last)
	{
		return first != null || last != null ? new Range(first, last) : null;
	}

	public static Option<Range> of(final CharSeq first, final CharSeq last)
	{
		val f = Try.of(() -> first.trim().toLong()).getOrNull();
		val l = Try.of(() -> last.trim().toLong()).getOrNull();
		return f != null || l != null ? Option.of(new Range(f, l)) : Option.none();
	}

	public static Tuple2<String, String> createHeader(@NonNull final Length length)
	{
		return Tuple.of(HEADER_NAME, "bytes */" + length.getValue());
	}

	private Range(final Long first, final Long last)
	{
		if (first == null && last == null)
			throw new NullPointerException("first and last are null!");
		if (first != null && first < 0)
			throw new IllegalArgumentException("first < 0!");
		if (first != null && last != null && first > last)
			throw new IllegalArgumentException("first > last!");
		this.first = Option.of(first);
		this.last = Option.of(last);
	}

	public long getFirst(@NonNull final Length length)
	{
		val result = first.getOrElse(length.getValue() - last.getOrElse(0L));
		return result < 0 ? 0 : result;
	}

	public long getLast(@NonNull final Length length)
	{
		return first.isDefined() && last.filter(l -> l < length.getValue()).isDefined() ? last.getOrElse(length.getValue() - 1) : length.getValue() - 1;
	}

	public Length getLength(@NonNull final Length length)
	{
		if (!first.isDefined())
			return new Length(last.map(l -> l >= length.getValue() ? length.getValue() : l).getOrElse(0L));
		else if (!last.isDefined())
			return new Length(first.map(f -> length.getValue() - (f >= length.getValue() ? length.getValue() : f)).getOrElse(0L));
		else
			return For(first, last).yield((f, l) -> new Length((l >= length.getValue() ? length.getValue() - 1 : l) - f + 1)).get();
	}

	public boolean inRange(@NonNull final Length length)
	{
		return getFirst(length) < length.getValue();
	}

	public void write(@NonNull final DownloadResponse response, @NonNull final Length fileLength)
	{
		response.setHeader(HEADER_NAME, createContentRangeValue(fileLength));
	}

	public void write(@NonNull final Writer writer, @NonNull final Length fileLength) throws IOException
	{
		writer.write(HEADER_NAME + ": " + createContentRangeValue(fileLength));
	}

	private String createContentRangeValue(final Length length)
	{
		return "bytes " + getFirst(length) + "-" + getLast(length) + "/" + length.getValue();
	}
}
