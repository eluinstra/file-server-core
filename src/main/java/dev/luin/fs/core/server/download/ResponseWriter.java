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
package dev.luin.fs.core.server.download;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.UUID;

import javax.servlet.http.HttpServletResponse;

import dev.luin.fs.core.file.FSFile;
import dev.luin.fs.core.file.FileSystem;
import dev.luin.fs.core.server.download.range.ContentRange;
import dev.luin.fs.core.server.download.range.ContentRangeHeader;
import dev.luin.fs.core.server.download.range.ContentRangeUtils;
import io.vavr.collection.Seq;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.val;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level=AccessLevel.PROTECTED, makeFinal=true)
@AllArgsConstructor
class ResponseWriter
{
	@NonNull
	FileSystem fileSystem;
	@NonNull
	HttpServletResponse response;

	public void write(@NonNull final FSFile fsFile, @NonNull final Seq<ContentRange> ranges) throws IOException
	{
		switch (ranges.size())
		{
			case 0:
				writeResponse(response,fsFile);
				break;
			case 1:
				writeResponse(response,fsFile,ranges.get(0));
				break;
			default:
				writeResponse(response,fsFile,ranges);
		}
	}

	protected void writeResponse(@NonNull final HttpServletResponse response, @NonNull final FSFile fsFile) throws IOException
	{
		setStatus200Headers(fsFile);
		if (isBinaryContent(fsFile))
			response.setHeader("Content-Transfer-Encoding","binary");
		fileSystem.write(fsFile,response.getOutputStream());
	}

	protected void writeResponse(@NonNull final HttpServletResponse response, @NonNull final FSFile fsFile, @NonNull final ContentRange range) throws IOException
	{
		val fileLength = fsFile.getLength();
		response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
		response.setHeader("Content-Type",fsFile.getContentType());
		response.setHeader("Content-Length",Long.toString(range.getLength(fileLength)));
		response.setHeader(ContentRangeHeader.CONTENT_RANGE.getName(),ContentRangeUtils.createContentRangeHeader(range,fileLength));
		if (isBinaryContent(fsFile))
			response.setHeader("Content-Transfer-Encoding","binary");
		fileSystem.write(fsFile,response.getOutputStream(),range.getFirst(fileLength),range.getLength(fileLength));
	}

	protected void writeResponse(@NonNull final HttpServletResponse response, @NonNull final FSFile fsFile, @NonNull final Seq<ContentRange> ranges) throws IOException
	{
		val fileLength = fsFile.getLength();
		val boundary = createMimeBoundary();
		val isBinary = isBinaryContent(fsFile);
		response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
		response.setHeader("Content-Type","multipart/byteranges; boundary=" + boundary);
		//response.setHeader("Content-Length","");
		try (val writer = new OutputStreamWriter(response.getOutputStream(),"UTF-8"))
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
				if (isBinary)
				{
					writer.write("Content-Transfer-Encoding: binary");
					writer.write("\r\n");
				}
				writer.write("\r\n");
				writer.flush();
				fileSystem.write(fsFile,response.getOutputStream(),range.getFirst(fileLength),range.getLength(fileLength));
				writer.write("\r\n");
			}
			writer.write("--");
			writer.write(boundary);
			writer.write("--");
		}
	}

	protected String createMimeBoundary()
	{
		return UUID.randomUUID().toString();
	}

	protected boolean isBinaryContent(final FSFile fsFile)
	{
		return !fsFile.getContentType().matches("^(text/.*|.*/xml)$");
	}

	public void setStatus200Headers(@NonNull final FSFile fsFile)
	{
		val fileLength = fsFile.getLength();
		val lastModified = fsFile.getLastModified();
		response.setStatus(HttpServletResponse.SC_OK);
		response.setHeader("Content-Type",fsFile.getContentType());
		if (fsFile.getName() != null)
			response.setHeader("Content-Disposition","attachment; filename=\"" + fsFile.getName() + "\"");
		response.setHeader("Content-Length",Long.toString(fileLength));
		response.setHeader(ContentRangeHeader.ACCEPT_RANGES.getName(),"bytes");
		response.setHeader("ETag","\"" + ContentRangeUtils.getHashCode(lastModified.toEpochMilli()) + "\"");
	}

}
