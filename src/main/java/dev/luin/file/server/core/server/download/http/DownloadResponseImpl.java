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

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;

import dev.luin.file.server.core.file.ContentType;
import dev.luin.file.server.core.file.FSFile;
import dev.luin.file.server.core.server.download.DownloadResponse;
import dev.luin.file.server.core.server.download.header.ContentRange;
import io.vavr.control.Try;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
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
	public void setHeader(String headerName, String value)
	{
		response.setHeader(headerName,value);
	}

	@Override
	public OutputStream getOutputStream()
	{
		try
		{
			return response.getOutputStream();
		}
		catch (IOException e)
		{
			throw new IllegalStateException(e);
		}
	}

	@Override
	public void sendContent(ContentType contentType, String content)
	{
		setStatusOk();
		response.setHeader("Content-Type",contentType.getValue());
		response.setHeader("Content-Length",Long.toString(content.length()));
		Try.success(content)
			.andThenTry(c -> response.getWriter().write(c))
			.getOrElseThrow(t -> new IllegalStateException(t));
	}

	@Override
	public void sendFileInfo(FSFile fsFile)
	{
		new ResponseWriter(this).writeFileInfo(fsFile);
	}

	@Override
	public void sendFile(FSFile fsFile, ContentRange ranges)
	{
		Try.success(this)
			.map(ResponseWriter::new)
			.andThenTry(w -> w.write(fsFile,ranges))
			.getOrElseThrow(t -> new IllegalStateException(t));
	}
}
