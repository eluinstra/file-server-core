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

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;
import lombok.experimental.FieldDefaults;

@RequiredArgsConstructor
@FieldDefaults(level=AccessLevel.PRIVATE, makeFinal=true)
public class FSResponseWriter
{
	@NonNull
	FileSystem fileSystem;
	@NonNull
	HttpServletResponse response;
	
	public void write(@NonNull final FSFile fsFile, @NonNull final List<ContentRange> ranges) throws IOException
	{
		if (ranges.size() == 0)
			writeResponse(response,fsFile);
		else if (ranges.size() == 1)
			writeResponse(response,fsFile,ranges.get(0));
		else //if (ranges.size() > 1)
			writeResponse(response,fsFile,ranges);
	}

	protected void writeResponse(@NonNull final HttpServletResponse response, @NonNull final FSFile fsFile) throws IOException
	{
		setStatus200Headers(fsFile);
		fileSystem.write(fsFile,response.getOutputStream());
	}

	protected void writeResponse(@NonNull final HttpServletResponse response, @NonNull final FSFile fsFile, @NonNull final ContentRange range) throws IOException
	{
		val fileLength = fsFile.getFileLength();
		response.setStatus(206);
		response.setHeader("Content-Type",fsFile.getContentType());
		response.setHeader("Content-Length",Long.toString(range.getLength(fileLength)));
		response.setHeader(ContentRangeHeader.CONTENT_RANGE.getName(),ContentRangeUtils.createContentRangeHeader(range,fileLength));
		fileSystem.write(fsFile,response.getOutputStream(),range.getFirst(fileLength),range.getLength(fileLength));
	}

	protected void writeResponse(@NonNull final HttpServletResponse response, @NonNull final FSFile fsFile, @NonNull final List<ContentRange> ranges) throws IOException
	{
		val fileLength = fsFile.getFileLength();
		val boundary = UUID.randomUUID().toString();
		response.setStatus(206);
		response.setHeader("Content-Type","multipart/byteranges; boundary=" + boundary);
		//response.setHeader("Content-Length","");
		try (OutputStreamWriter writer = new OutputStreamWriter(response.getOutputStream(),"UTF-8"))
		{
			for (val range: ranges)
			{
				writer.write("--");
				writer.write(boundary);
				writer.write("\r\n");
				writer.write("Content-Type: " + fsFile.getContentType());
				writer.write("\r\n");
				writer.write(ContentRangeHeader.CONTENT_RANGE.getName() + ": " + ContentRangeUtils.createContentRangeHeader(range,fileLength));
				writer.write("\r\n");
				writer.write("\r\n");
				fileSystem.write(fsFile,response.getOutputStream(),range.getFirst(fileLength),range.getLength(fileLength));
				writer.write("\r\n");
			}
			writer.write("--");
			writer.write(boundary);
			writer.write("--");
		}
	}

	public void setStatus200Headers(@NonNull final FSFile fsFile)
	{
		val fileLength = fsFile.getFileLength();
		val lastModified = fsFile.getFileLastModified();
		response.setStatus(200);
		response.setHeader("Content-Type",fsFile.getContentType());
		response.setHeader("Content-Length",Long.toString(fileLength));
		response.setHeader(ContentRangeHeader.ACCEPT_RANGES.getName(),"bytes");
		response.setHeader("ETag","\"" + ContentRangeUtils.getHashCode(lastModified) + "\"");
	}

}