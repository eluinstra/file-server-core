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
package dev.luin.file.server.core.server.download.header;

import java.io.IOException;
import java.io.OutputStreamWriter;

import dev.luin.file.server.core.server.download.DownloadResponse;
import lombok.NonNull;

public class ContentType
{
	private static final String HEADER_NAME = "Content-Type";

	public static void write(@NonNull final DownloadResponse response, @NonNull final dev.luin.file.server.core.file.ContentType contentType)
	{
		response.setHeader(HEADER_NAME,contentType.getValue());
	}

	public static void writeMultiPartBoundary(@NonNull final DownloadResponse response, @NonNull final String boundary)
	{
		response.setHeader(HEADER_NAME,"multipart/byteranges; boundary=" + boundary);
	}

	public static void write(@NonNull final OutputStreamWriter writer, @NonNull final dev.luin.file.server.core.file.ContentType contentType) throws IOException
	{
		writer.write("Content-Type: " + contentType.getValue());
	}
}
