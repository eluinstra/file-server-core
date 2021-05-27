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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dev.luin.file.server.core.http.LongHeaderValue;
import dev.luin.file.server.core.server.upload.UploadException;
import io.vavr.control.Option;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UploadOffset
{
	public static final String HEADER_NAME = "Upload-Offset";

	public static Long get(HttpServletRequest request)
	{
		return get(request.getHeader(HEADER_NAME));
	}

	private static Long get(String value)
	{
		return Option.of(value)
				.map(v -> LongHeaderValue.get(v,0L,Long.MAX_VALUE).getOrElseThrow(() -> UploadException.invalidUploadOffset()))
				.getOrElseThrow(() -> UploadException.missingUploadOffset());
	}

	public static void write(HttpServletResponse response, Long fileLength)
	{
		response.setHeader(HEADER_NAME,fileLength.toString());
	}
}
