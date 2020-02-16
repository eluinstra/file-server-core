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

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64OutputStream;
import org.bitbucket.eluinstra.fs.core.file.FSFile;
import org.bitbucket.eluinstra.fs.core.file.FileSystem;
import org.bitbucket.eluinstra.fs.core.server.range.ContentRange;
import org.bitbucket.eluinstra.fs.core.server.range.ContentRangeUtils;
import org.bitbucket.eluinstra.fs.core.server.range.ContentRangeUtils.ContentRangeHeader;

import lombok.NonNull;
import lombok.val;

public class FSBase64ResponseWriter extends FSResponseWriter
{
	public FSBase64ResponseWriter(@NonNull final FileSystem fileSystem, @NonNull final HttpServletResponse response)
	{
		super(fileSystem,response);
	}

	@Override
	protected void writeResponse(@NonNull final HttpServletResponse response, @NonNull final FSFile fsFile) throws IOException
	{
		val isBinary = isBinaryContent(fsFile);
		setStatus200Headers(fsFile);
		if (isBinary)
			response.setHeader("Content-Transfer-Encoding","base64");
		try (val output = isBinary ? new Base64OutputStream(response.getOutputStream()) : response.getOutputStream())
		{
			fileSystem.write(fsFile,output);
		}
	}

	@Override
	protected void writeResponse(@NonNull final HttpServletResponse response, @NonNull final FSFile fsFile, @NonNull final ContentRange range) throws IOException
	{
		val fileLength = fsFile.getFileLength();
		val isBinary = isBinaryContent(fsFile);
		response.setStatus(206);
		response.setHeader("Content-Type",fsFile.getContentType());
		response.setHeader("Content-Length",Long.toString(range.getLength(fileLength)));
		response.setHeader(ContentRangeHeader.CONTENT_RANGE.getName(),ContentRangeUtils.createContentRangeHeader(range,fileLength));
		if (isBinary)
			response.setHeader("Content-Transfer-Encoding","base64");
		try (val output = isBinary ? new Base64OutputStream(response.getOutputStream()) : response.getOutputStream())
		{
			fileSystem.write(fsFile,output,range.getFirst(fileLength),range.getLength(fileLength));
		}
	}

	@Override
	protected void writeResponse(@NonNull final HttpServletResponse response, @NonNull final FSFile fsFile, @NonNull final List<ContentRange> ranges) throws IOException
	{
		val fileLength = fsFile.getFileLength();
		val boundary = createMimeBoundary();
		val isBinary = isBinaryContent(fsFile);
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
				if (isBinary)
				{
					writer.write("Content-Transfer-Encoding: base64");
					writer.write("\r\n");
				}
				writer.write("\r\n");
				try (val output = isBinary ? new Base64OutputStream(response.getOutputStream()) : response.getOutputStream())
				{
					fileSystem.write(fsFile,output,range.getFirst(fileLength),range.getLength(fileLength));
				}
				writer.write("\r\n");
			}
			writer.write("--");
			writer.write(boundary);
			writer.write("--");
		}
	}
}
