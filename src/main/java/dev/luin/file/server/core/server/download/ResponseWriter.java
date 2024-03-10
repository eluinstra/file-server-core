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
package dev.luin.file.server.core.server.download;

import static io.vavr.control.Try.failure;

import dev.luin.file.server.core.file.FSFile;
import dev.luin.file.server.core.server.download.header.AcceptRanges;
import dev.luin.file.server.core.server.download.header.ContentDisposition;
import dev.luin.file.server.core.server.download.header.ContentLength;
import dev.luin.file.server.core.server.download.header.ContentRange;
import dev.luin.file.server.core.server.download.header.ContentTransferEncoding;
import dev.luin.file.server.core.server.download.header.ContentType;
import dev.luin.file.server.core.server.download.header.ETag;
import dev.luin.file.server.core.server.download.header.Range;
import io.vavr.Function1;
import io.vavr.collection.Seq;
import io.vavr.control.Try;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import lombok.val;

@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
@AllArgsConstructor
class ResponseWriter
{
	@NonNull
	DownloadResponse response;

	Function1<DownloadResponse, Try<Long>> write(@NonNull final FSFile fsFile, @NonNull final ContentRange ranges)
	{
		switch (ranges.count())
		{
			case 0:
				return writeResponse(fsFile);
			case 1:
				return writeResponse(fsFile, ranges.getRanges().get());
			default:
				return writeResponse(fsFile, ranges);
		}
	}

	private Function1<DownloadResponse, Try<Long>> writeResponse(final FSFile fsFile)
	{
		return response ->
		{
			writeFileInfo(response, fsFile);
			if (fsFile.isBinary())
				setTransferEncoding(response);
			return writeContent(response, fsFile);
		};
	}

	void writeFileInfo(@NonNull DownloadResponse response, @NonNull final FSFile fsFile)
	{
		response.setStatusOk();
		ContentType.write(response, fsFile.getContentType());
		if (fsFile.getName() != null)
			ContentDisposition.write(response, fsFile.getName());
		ContentLength.write(response, fsFile.getFileLength());
		AcceptRanges.write(response);
		ETag.write(response, fsFile.getLastModified());
	}

	protected void setTransferEncoding(@NonNull DownloadResponse response)
	{
		ContentTransferEncoding.writeBinary(response);
	}

	protected Try<Long> writeContent(@NonNull DownloadResponse response, final FSFile fsFile)
	{
		return response.getOutputStream().flatMap(fsFile::write);
	}

	private Function1<DownloadResponse, Try<Long>> writeResponse(final FSFile fsFile, final Range range)
	{
		return response ->
		{
			response.setStatusPartialContent();
			ContentType.write(response, fsFile.getContentType());
			val fileLength = fsFile.getFileLength();
			ContentLength.write(response, range.getLength(fileLength));
			range.write(response, fileLength);
			if (fsFile.isBinary())
				setTransferEncoding(response);
			return writeContent(fsFile, range);
		};
	}

	private Function1<DownloadResponse, Try<Long>> writeResponse(final FSFile fsFile, final ContentRange contentRange)
	{
		return result ->
		{
			val boundary = createMimeBoundary();
			response.setStatusPartialContent();
			ContentType.writeMultiPartBoundary(response, boundary);
			// ContentLength.write(response);
			return write(fsFile, contentRange.getRanges(), boundary);
		};
	}

	private String createMimeBoundary()
	{
		return UUID.randomUUID().toString();
	}

	private Try<Long> write(final FSFile fsFile, final Seq<Range> ranges, final String boundary)
	{
		return response.getOutputStream().flatMap(out -> write(fsFile, ranges, boundary, out));
	}

	private Try<Long> write(final FSFile fsFile, final Seq<Range> ranges, final String boundary, OutputStream out)
	{
		try (val writer = new OutputStreamWriter(out, StandardCharsets.UTF_8))
		{
			for (val range : ranges)
			{
				writer.write("--");
				writer.write(boundary);
				writer.write("\r\n");
				ContentType.write(writer, fsFile.getContentType());
				writer.write("\r\n");
				range.write(writer, fsFile.getFileLength());
				writer.write("\r\n");
				if (fsFile.isBinary())
				{
					writeTransferEncoding(writer);
					writer.write("\r\n");
				}
				writer.write("\r\n");
				writer.flush();
				writeContent(fsFile, range).get();
				writer.write("\r\n");
			}
			writer.write("--");
			writer.write(boundary);
			writer.write("--");
			return null;
		}
		catch (IOException e)
		{
			return failure(e);
		}
	}

	protected void writeTransferEncoding(final OutputStreamWriter writer) throws IOException
	{
		ContentTransferEncoding.writeBinary(writer);
	}

	protected Try<Long> writeContent(final FSFile fsFile, final Range range)
	{
		return response.getOutputStream().flatMap(out -> fsFile.write(out, range));
	}

}
