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
package dev.luin.file.server.core.server.upload.header;

import javax.servlet.http.HttpServletResponse;

import dev.luin.file.server.core.ValueObjectOptional;
import io.vavr.control.Option;
import lombok.Value;

@Value
public class TusMaxSize implements ValueObjectOptional<Long>
{
	private static String HEADER_NAME = "Tus-Max-Size";
	Option<Long> value;

	public TusMaxSize(Long maxSize)
	{
		value = Option.of(maxSize);
	}

	@Override
	public Option<Long> getValue()
	{
		return value;
	}

	public void write(HttpServletResponse response)
	{
		response.setHeader(HEADER_NAME,value.toString());
	}
}
