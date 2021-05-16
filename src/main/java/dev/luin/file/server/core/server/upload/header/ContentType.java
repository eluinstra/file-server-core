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

import dev.luin.file.server.core.http.ConstHeaderValue;
import dev.luin.file.server.core.http.HttpException;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ContentType extends TusHeader
{
	private static final String HEADER_NAME = "Content-Type";

	public static void validate(HttpServletRequest request)
	{
		validate(request.getHeader(HEADER_NAME));
	}

	private static void validate(String value)
	{
		ConstHeaderValue.of(value,"application/offset+octet-stream")
				.map(v -> new ContentType(v))
				.getOrElseThrow(() -> HttpException.unsupportedMediaType());
	}

	@NonNull
	ConstHeaderValue value;

	public ContentType(@NonNull ConstHeaderValue value)
	{
		super(HEADER_NAME);
		this.value = value;
	}

	@Override
	public String toString()
	{
		return value.toString();
	}
}
