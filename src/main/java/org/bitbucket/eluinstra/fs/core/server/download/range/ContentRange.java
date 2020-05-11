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
package org.bitbucket.eluinstra.fs.core.server.download.range;

import java.util.Optional;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.val;
import lombok.var;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level=AccessLevel.PRIVATE, makeFinal=true)
@EqualsAndHashCode
@ToString
public class ContentRange
{
	Optional<Long> first;
	Optional<Long> last;

	public static ContentRange of(final Long first, final Long last)
	{
		return new ContentRange(first,last);
	}

	private ContentRange(final Long first, final Long last)
	{
		if (first == null && last == null)
			throw new NullPointerException("first and last are null!");
		if (first != null && first < 0)
			throw new IllegalArgumentException("first < 0!");
		if (first != null && last != null && first > last)
			throw new IllegalArgumentException("first > last!");
		this.first = Optional.ofNullable(first);
		this.last = Optional.ofNullable(last);
	}
	
	public long getFirst(final long fileLength)
	{
		val result = first.orElse(fileLength - last.orElse(0L));
		return result < 0 ? 0 : result;
	}

	public long getLast(final long fileLength)
	{
		return first.isPresent() && last.filter(l -> l < fileLength).isPresent() ? last.orElse(fileLength - 1) : fileLength - 1;
	}

	public long getLength(final long fileLength)
	{
		var result = 0L;
		if (!first.isPresent())
			result = last.get();
		else if (!last.isPresent())
			result = fileLength - first.get();
		else
			result = last.get() - first.get() + 1;
		return result > fileLength ? fileLength : result;
	}

}
