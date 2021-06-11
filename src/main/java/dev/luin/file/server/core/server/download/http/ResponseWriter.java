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
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.UUID;

import dev.luin.file.server.core.file.FSFile;
import dev.luin.file.server.core.server.download.DownloadResponse;
import dev.luin.file.server.core.server.download.header.AcceptRanges;
import dev.luin.file.server.core.server.download.header.ContentRange;
import dev.luin.file.server.core.server.download.header.ETag;
import dev.luin.file.server.core.server.download.header.Range;
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
	DownloadResponse response;

	void write(@NonNull final FSFile fsFile, @NonNull final ContentRange ranges) throws IOException
	{
		switch (ranges.count())
		{
			case 0:
				writeResponse(fsFile);
				break;
			case 1:
				writeResponse(fsFile,ranges.getRanges().getOrElseThrow(() -> new IllegalStateException("Range not found")));
				break;
			default:
				writeResponse(fsFile,ranges);
		}
	}

	private void writeResponse(final FSFile fsFile) throws IOException
	{
		writeFileInfo(fsFile);
		if (fsFile.isBinary())
			setTransferEncoding();
		writeContent(fsFile);
	}

	void writeFileInfo(@NonNull final FSFile fsFile)
	{
		response.setStatusOk();
		response.setHeader("Content-Type",fsFile.getContentType().getValue());
		if (fsFile.getName() != null)
			response.setHeader("Content-Disposition","attachment; filename=\"" + fsFile.getName().getValue() + "\"");
		response.setHeader("Content-Length",fsFile.getFileLength().getStringValue());
		AcceptRanges.write(response);
		response.setHeader("ETag","\"" + ETag.getHashCode(fsFile.getLastModified()) + "\"");
	}

	protected void setTransferEncoding()
	{
		response.setHeader("Content-Transfer-Encoding","binary");
	}

	protected void writeContent(final FSFile fsFile) throws IOException
	{
		fsFile.write(response.getOutputStream());
	}

	private void writeResponse(final FSFile fsFile, final Range range) throws IOException
	{
		response.setStatusPartialContent();
		response.setHeader("Content-Type",fsFile.getContentType().getValue());
		val fileLength = fsFile.getFileLength();
		response.setHeader("Content-Length",range.getLength(fileLength).getStringValue());
		range.write(response,fileLength);
		if (fsFile.isBinary())
			setTransferEncoding();
		writeContent(fsFile,range);
	}

	private void writeResponse(final FSFile fsFile, final ContentRange contentRange) throws IOException
	{
		val boundary = createMimeBoundary();
		response.setStatusPartialContent();
		response.setHeader("Content-Type","multipart/byteranges; boundary=" + boundary);
		//response.setHeader("Content-Length","");
		write(fsFile,contentRange.getRanges(),boundary);
	}

	private String createMimeBoundary()
	{
		return UUID.randomUUID().toString();
	}

	private void write(final FSFile fsFile, final Seq<Range> ranges, final String boundary) throws IOException, UnsupportedEncodingException
	{
		try (val writer = new OutputStreamWriter(response.getOutputStream(),"UTF-8"))
		{
			for (val range: ranges)
			{
				writer.write("--");
				writer.write(boundary);
				writer.write("\r\n");
				writer.write("Content-Type: " + fsFile.getContentType());
				writer.write("\r\n");
				writer.write(range.write(fsFile.getFileLength()));
				writer.write("\r\n");
				if (fsFile.isBinary())
				{
					writeTransferEncoding(writer);
					writer.write("\r\n");
				}
				writer.write("\r\n");
				writer.flush();
				writeContent(fsFile,range);
				writer.write("\r\n");
			}
			writer.write("--");
			writer.write(boundary);
			writer.write("--");
		}
	}

	protected void writeTransferEncoding(final OutputStreamWriter writer) throws IOException
	{
		writer.write("Content-Transfer-Encoding: binary");
	}

	protected void writeContent(final FSFile fsFile, final Range range) throws IOException
	{
		fsFile.write(response.getOutputStream(),range);
	}

}
