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
package dev.luin.file.server.core.server.download.range;

import dev.luin.file.server.core.file.FileLength;
import io.vavr.collection.CharSeq;
import io.vavr.control.Option;
import io.vavr.control.Try;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;
import lombok.val;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level=AccessLevel.PRIVATE, makeFinal=true)
@EqualsAndHashCode
@ToString
public class ContentRange
{
	@NonNull
	Option<Long> first;
	@NonNull
	Option<Long> last;

	public static ContentRange of(final Long first, final Long last)
	{
		return first != null || last != null ? new ContentRange(first,last) : null;
	}

	public static Option<ContentRange> of(final CharSeq first, final CharSeq last)
	{
		val f = Try.of(() -> first.trim().toLong()).getOrNull();
		val l = Try.of(() -> last.trim().toLong()).getOrNull();
		return f != null || l != null ? Option.of(new ContentRange(f,l)) : Option.none();
	}

	private ContentRange(final Long first, final Long last)
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
	
	public long getFirst(final FileLength fileLength)
	{
		val result = first.getOrElse(fileLength.getOrElse(0L) - last.getOrElse(0L));
		return result < 0 ? 0 : result;
	}

	public long getLast(final FileLength fileLength)
	{
		return first.isDefined()
				&& last.filter(l -> l < fileLength.getOrElse(0L)).isDefined()
						? last.getOrElse(fileLength.getOrElse(0L) - 1)
						: fileLength.getOrElse(0L) - 1;
	}

	public long getLength(final FileLength fileLength)
	{
		if (!first.isDefined())
			return last.map(l -> l >= fileLength.getOrElse(0L) ? fileLength.getOrElse(0L) : l).getOrElse(0L);
		else if (!last.isDefined())
			return first.map(f -> fileLength.getOrElse(0L) - (f >= fileLength.getOrElse(0L) ? fileLength.getOrElse(0L) : f)).getOrElse(0L);
		else
			return (last.get() >= fileLength.getOrElse(0L) ? fileLength.getOrElse(0L) - 1 : last.get()) - first.get() + 1;
	}
}
