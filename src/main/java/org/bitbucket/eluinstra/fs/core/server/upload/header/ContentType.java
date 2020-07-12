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
package org.bitbucket.eluinstra.fs.core.server.upload.header;

import javax.servlet.http.HttpServletRequest;

import org.bitbucket.eluinstra.fs.core.http.ConstHeaderValue;
import org.bitbucket.eluinstra.fs.core.http.HttpException;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ContentType extends TusHeader
{
	private static final String HEADER_NAME = "Content-Type";

	public static ContentType of(HttpServletRequest request)
	{
		return ConstHeaderValue.of(request.getHeader(HEADER_NAME),"application/offset+octet-stream")
				.map(v -> new ContentType(v))
				.getOrElseThrow(() -> HttpException.unsupportedMediaTypeException());
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
