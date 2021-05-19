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

import javax.servlet.http.HttpServletResponse;

import dev.luin.file.server.core.file.FSFile;
import dev.luin.file.server.core.file.FileSystem;
import dev.luin.file.server.core.server.download.DownloadResponse;
import dev.luin.file.server.core.server.download.range.ContentRange;
import io.vavr.collection.Seq;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
public class DownloadResponseImpl implements DownloadResponse
{
	HttpServletResponse response;

	@Override
	public void sendContent(String contentType, String content) throws IOException
	{
		response.setStatus(HttpServletResponse.SC_OK);
		response.setHeader("Content-Type",contentType);
		response.setHeader("Content-Length",Long.toString(content.length()));
		response.getWriter().write(content);
	}

	@Override
	public void sendFileInfo(FileSystem fs, FSFile fsFile)
	{
		new ResponseWriter(fs,response).writeFileInfo(fsFile);
	}

	@Override
	public void sendFile(FileSystem fs, FSFile fsFile, Seq<ContentRange> ranges) throws IOException
	{
		new ResponseWriter(fs,response).write(fsFile,ranges);
	}
}
