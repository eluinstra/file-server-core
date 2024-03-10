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
package dev.luin.file.server.core.server.download.header;

import dev.luin.file.server.core.server.download.DownloadResponse;
import java.io.IOException;
import java.io.OutputStreamWriter;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ContentTransferEncoding
{
	private static final String HEADER_NAME = "Content-Transfer-Encoding";
	private static final String BINARY_VALUE = "binary";
	private static final String BASE64_VALUE = "base64";

	public static void writeBinary(@NonNull final DownloadResponse response)
	{
		response.setHeader(HEADER_NAME, BINARY_VALUE);
	}

	public static void writeBase64(@NonNull final DownloadResponse response)
	{
		response.setHeader(HEADER_NAME, BASE64_VALUE);
	}

	public static void writeBinary(@NonNull final OutputStreamWriter writer) throws IOException
	{
		writer.write(HEADER_NAME + ": " + BINARY_VALUE);
	}

	public static void writeBase64(@NonNull final OutputStreamWriter writer) throws IOException
	{
		writer.write(HEADER_NAME + ": " + BASE64_VALUE);
	}

}
