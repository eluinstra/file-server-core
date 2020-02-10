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
package org.bitbucket.eluinstra.fs.validation;

import java.util.List;
import java.util.stream.Collectors;

import org.bitbucket.eluinstra.fs.model.FSFile;
import org.bitbucket.eluinstra.fs.model.ContentRange;

public class ContentRangeValidator
{
	private ContentRangeValidator()
	{
	}

	public static boolean isValid(FSFile fsFile, List<ContentRange> ranges)
	{
		long fileLength = fsFile.getFile().length();
		return ranges.stream()
				.anyMatch(r -> r.getFirst(fileLength) < fileLength);
	}

	public static List<ContentRange> filterValidRanges(FSFile fsFile, List<ContentRange> ranges)
	{
		long fileLength = fsFile.getFile().length();
		return ranges.stream()
				.filter(r -> r.getFirst(fileLength) < fileLength)
				.collect(Collectors.toList());
	}

}
