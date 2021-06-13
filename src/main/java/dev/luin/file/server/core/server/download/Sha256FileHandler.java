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
package dev.luin.file.server.core.server.download;

import dev.luin.file.server.core.file.ContentType;
import dev.luin.file.server.core.file.FSFile;
import dev.luin.file.server.core.file.Length;
import dev.luin.file.server.core.server.download.header.ContentLength;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
public class Sha256FileHandler implements FileHandler
{
	@NonNull
	FSFile fsFile;
	@NonNull
	Extension extension;

	@Override
	public void handle(@NonNull final DownloadRequest request, @NonNull final DownloadResponse response)
	{
		log.debug("GetSHA256Checksum {}",fsFile);
		sendContent(response,extension.getDefaultContentType(),fsFile.getSha256Checksum().getValue());
	}

	public void sendContent(final DownloadResponse response, final ContentType contentType, final String content)
	{
		response.setStatusOk();
		dev.luin.file.server.core.server.download.header.ContentType.write(response,contentType);
		ContentLength.write(response,new Length(content.length()));
		response.getWriter().write(content);
	}
}
