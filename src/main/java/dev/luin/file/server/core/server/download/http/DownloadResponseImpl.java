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
package dev.luin.file.server.core.server.download.http;

import static io.vavr.control.Try.failure;
import static io.vavr.control.Try.success;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;

import dev.luin.file.server.core.server.download.DownloadResponse;
import io.vavr.control.Try;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
public class DownloadResponseImpl implements DownloadResponse
{
	HttpServletResponse response;

	@Override
	public void setStatusOk()
	{
		response.setStatus(HttpServletResponse.SC_OK);
	}

	@Override
	public void setStatusPartialContent()
	{
		response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
	}

	@Override
	public void setHeader(@NonNull final String headerName, @NonNull final String value)
	{
		response.setHeader(headerName,value);
	}

	@Override
	public void write(String content)
	{
		try
		{
			response.getWriter().write(content);
		}
		catch (IOException e)
		{
			throw new IllegalStateException(e);
		}
	}

	@Override
	public Try<OutputStream> getOutputStream()
	{
		try
		{
			return success(response.getOutputStream());
		}
		catch (IOException e)
		{
			return failure(e);
		}
	}
}
