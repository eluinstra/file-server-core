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

import org.apache.commons.codec.binary.Base64OutputStream;

import dev.luin.file.server.core.file.FSFile;
import dev.luin.file.server.core.server.download.DownloadResponse;
import dev.luin.file.server.core.server.download.header.Range;
import lombok.NonNull;
import lombok.val;

class Base64ResponseWriter extends ResponseWriter
{
	Base64ResponseWriter(@NonNull final DownloadResponse response)
	{
		super(response);
	}

	@Override
	protected void setTransferEncoding()
	{
		response.setHeader("Content-Transfer-Encoding","base64");
	}

	@Override
	protected void writeContent(final FSFile fsFile) throws IOException
	{
		try (val output = fsFile.isBinary() ? new Base64OutputStream(response.getOutputStream()) : response.getOutputStream())
		{
			fsFile.write(output);
		}
	}

	@Override
	protected void writeTransferEncoding(final OutputStreamWriter writer) throws IOException
	{
		writer.write("Content-Transfer-Encoding: base64");
	}

	@Override
	protected void writeContent(final FSFile fsFile, final Range range) throws IOException
	{
		try (val output = fsFile.isBinary() ? new Base64OutputStream(response.getOutputStream()) : response.getOutputStream())
		{
			fsFile.write(output,range);
		}
	}

}
