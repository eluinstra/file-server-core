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
package org.bitbucket.eluinstra.fs.core.server;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletResponse;

import org.bitbucket.eluinstra.fs.core.file.FSFile;
import org.bitbucket.eluinstra.fs.core.file.FileSystem;
import org.bitbucket.eluinstra.fs.core.server.range.ContentRange;
import org.bitbucket.eluinstra.fs.core.server.range.ContentRangeUtils;
import org.bitbucket.eluinstra.fs.core.server.range.ContentRangeUtils.ContentRangeHeader;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FSResponseWriter
{
	@NonNull
	FileSystem fileSystem;
	@NonNull
	protected final HttpServletResponse response;
	
	public void write(@NonNull FSFile fsFile, @NonNull List<ContentRange> ranges) throws IOException
	{
		if (ranges.size() == 0)
			writeResponse(response,fsFile);
		else if (ranges.size() == 1)
			writeResponse(response,fsFile,ranges.get(0));
		else //if (ranges.size() > 1)
			writeResponse(response,fsFile,ranges);
	}

	protected void writeResponse(@NonNull HttpServletResponse response, @NonNull FSFile fsFile) throws IOException
	{
		setStatus200Headers(fsFile);
		fileSystem.write(response.getOutputStream(),fsFile);
	}

	protected void writeResponse(@NonNull HttpServletResponse response, @NonNull FSFile fsFile, @NonNull ContentRange range) throws IOException
	{
		long fileLength = fsFile.getFileLength();
		response.setStatus(206);
		response.setHeader("Content-Type",fsFile.getContentType());
		response.setHeader("Content-Length",Long.toString(range.getLength(fileLength)));
		response.setHeader(ContentRangeHeader.CONTENT_RANGE.getName(),range.createContentRangeHeader(fileLength));
		fileSystem.write(response.getOutputStream(),fsFile,range.getFirst(fileLength),range.getLength(fileLength));
	}

	protected void writeResponse(@NonNull HttpServletResponse response, @NonNull FSFile fsFile, @NonNull List<ContentRange> ranges) throws IOException
	{
		long fileLength = fsFile.getFileLength();
		String boundary = UUID.randomUUID().toString();
		response.setStatus(206);
		response.setHeader("Content-Type","multipart/byteranges; boundary=" + boundary);
		//response.setHeader("Content-Length","");
		try (OutputStreamWriter writer = new OutputStreamWriter(response.getOutputStream(),"UTF-8"))
		{
			for (ContentRange range: ranges)
			{
				writer.write("--");
				writer.write(boundary);
				writer.write("\r\n");
				writer.write("Content-Type: " + fsFile.getContentType());
				writer.write("\r\n");
				writer.write(ContentRangeHeader.CONTENT_RANGE.getName() + ": " + range.createContentRangeHeader(fileLength));
				writer.write("\r\n");
				writer.write("\r\n");
				fileSystem.write(response.getOutputStream(),fsFile,range.getFirst(fileLength),range.getLength(fileLength));
				writer.write("\r\n");
			}
			writer.write("--");
			writer.write(boundary);
			writer.write("--");
		}
	}

	public void setStatus200Headers(@NonNull FSFile fsFile)
	{
		long fileLength = fsFile.getFileLastModified();
		long lastModified = fsFile.getFileLength();
		response.setStatus(200);
		response.setHeader("Content-Type",fsFile.getContentType());
		response.setHeader("Content-Length",Long.toString(fileLength));
		response.setHeader(ContentRangeHeader.ACCEPT_RANGES.getName(),"bytes");
		response.setHeader("ETag","\"" + ContentRangeUtils.getHashCode(lastModified) + "\"");
	}

}
