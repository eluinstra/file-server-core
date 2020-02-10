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
package org.bitbucket.eluinstra.fs.model;

import java.util.Optional;

import lombok.Getter;

public class ContentRange
{
	@Getter
	private Optional<Long> first;
	@Getter
	private Optional<Long> last;

	public ContentRange(Long first, Long last)
	{
		if (first == null && last == null)
			throw new IllegalArgumentException("first and last are null!");
		if (first != null && first < 0)
			throw new IllegalArgumentException("first < 0!");
		if (first != null && last != null && first > last)
			throw new IllegalArgumentException("first > last!");
		this.first = Optional.ofNullable(first);
		this.last = Optional.ofNullable(last);
	}
	
	public long getFirst(long fileLength)
	{
		long result = first.orElse(fileLength - last.orElse(0L));
		return result < 0 ? 0 : result;
	}

	public long getLast(long fileLength)
	{
		return first.isPresent() && last.isPresent() && last.get() < fileLength ? last.orElse(fileLength - 1) : fileLength - 1;
	}

	public long getLength(long fileLength)
	{
		long result = 0;
		if (!first.isPresent())
			result = last.get();
		else if (!last.isPresent())
			result = fileLength - first.get();
		else
			result = last.get() - first.get() + 1;
		return result > fileLength ? fileLength : result;
	}

	public String createContentRangeHeader(long fileLength)
	{
		return "bytes " + first + "-" + last + "/" + fileLength;
	}

}
