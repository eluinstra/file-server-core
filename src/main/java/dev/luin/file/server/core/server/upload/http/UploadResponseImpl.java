/*
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
package dev.luin.file.server.core.server.upload.http;

import javax.servlet.http.HttpServletResponse;

import dev.luin.file.server.core.server.upload.UploadResponse;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
public class UploadResponseImpl implements UploadResponse
{
	@NonNull
	HttpServletResponse response;

	@Override
	public void setStatusNoContent()
	{
		setStatus(HttpServletResponse.SC_NO_CONTENT);
	}

	private void setStatus(final int statusCode)
	{
		response.setStatus(statusCode);
	}

	@Override
	public void setStatusCreated()
	{
		setStatus(HttpServletResponse.SC_CREATED);
	}

	@Override
	public void setHeader(String headerName, String value)
	{
		response.setHeader(headerName,value);
	}
}
