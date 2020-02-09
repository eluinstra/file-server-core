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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.bitbucket.eluinstra.fs.model.ContentRange;

public class ContentRangeParser
{
	public final List<ContentRange> parseRangeHeader(String range)
	{
		List<ContentRange> result = new ArrayList<>();
		if (range != null && range.startsWith("bytes"))
		{
			range = range.substring("bytes=".length());
			String[] ranges = StringUtils.split(range,",");
			for (String r: ranges)
				result.add(createContentRange(r));
		}
		return result;
	}
	
	private ContentRange createContentRange(String range)
	{
		String[] r = StringUtils.split(range,"-");
		Long first = StringUtils.isEmpty(r[0]) ? null : Long.parseLong(r[0]);
		Long last = StringUtils.isEmpty(r[1]) ? null : Long.parseLong(r[1]);
		return (first != null || last != null) ? new ContentRange(first,last) : null;
	}	

}
