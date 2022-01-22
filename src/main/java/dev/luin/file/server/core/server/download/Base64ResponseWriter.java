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

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import org.apache.commons.codec.binary.Base64OutputStream;

import dev.luin.file.server.core.file.FSFile;
import dev.luin.file.server.core.server.download.header.ContentTransferEncoding;
import dev.luin.file.server.core.server.download.header.Range;
import io.vavr.control.Try;
import lombok.NonNull;
import lombok.val;

class Base64ResponseWriter extends ResponseWriter
{
	Base64ResponseWriter(@NonNull final DownloadResponse response)
	{
		super(response);
	}

	@Override
	protected void setTransferEncoding(@NonNull DownloadResponse response)
	{
		ContentTransferEncoding.writeBase64(response);
	}

	@Override
	protected Try<Long> writeContent(@NonNull DownloadResponse response, final FSFile fsFile)
	{
		return response.getOutputStream()
				.flatMap(out -> writeContent(fsFile,out));
	}

	private Try<Long> writeContent(final FSFile fsFile, OutputStream out)
	{
		try (val output = fsFile.isBinary() ? new Base64OutputStream(out) : out)
		{
			return fsFile.write(output);
		}
		catch (IOException e)
		{
			return failure(e);
		}
	}

	@Override
	protected void writeTransferEncoding(final OutputStreamWriter writer) throws IOException
	{
		ContentTransferEncoding.writeBase64(writer);
	}

	@Override
	protected Try<Long> writeContent(final FSFile fsFile, final Range range)
	{
		return response.getOutputStream()
				.flatMap(out -> writeContent(fsFile,range,out));
	}

	private Try<? extends Long> writeContent(final FSFile fsFile, final Range range, OutputStream out)
	{
		try (val output = fsFile.isBinary() ? new Base64OutputStream(out) : out)
		{
			return fsFile.write(output,range);
		}
		catch (IOException e)
		{
			return failure(e);
		}
	}

}
