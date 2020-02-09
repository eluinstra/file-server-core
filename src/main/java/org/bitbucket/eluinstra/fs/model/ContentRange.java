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
			throw new IllegalArgumentException("Both first and last are null!");
		this.first = Optional.ofNullable(first);
		this.last = Optional.ofNullable(last);
	}
	
	public long getFirst(FSFile fsFile)
	{
		return first.orElse(fsFile.getFile().length());
	}

	public long getLast(FSFile fsFile)
	{
		return last.orElse(fsFile.getFile().length());
	}

	public long getLength(FSFile fsFile)
	{
		if (!first.isPresent())
			return last.get();
		else if (!last.isPresent())
			return fsFile.getFile().length() - first.get();
		else
			return last.get() - first.get();
	}

	public String createContentRangeHeader(FSFile fsFile)
	{
		return "bytes " + first + "-" + last + "/" + fsFile.getFile().length();
	}

}
