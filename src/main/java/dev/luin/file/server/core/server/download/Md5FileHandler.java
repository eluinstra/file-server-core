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

import static io.vavr.control.Try.success;

import dev.luin.file.server.core.file.ContentType;
import dev.luin.file.server.core.file.FSFile;
import dev.luin.file.server.core.file.Length;
import dev.luin.file.server.core.server.download.header.ContentLength;
import io.vavr.Function1;
import io.vavr.control.Try;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
public class Md5FileHandler implements FileHandler
{
	@NonNull
	FSFile fsFile;

	@Override
	public Try<Function1<DownloadResponse,Try<Void>>> handle(@NonNull final DownloadRequest request)
	{
		log.debug("GetMD5Checksum {}",fsFile);
		return sendContent(ContentType.TEXT,fsFile.getMd5Checksum().getValue());
	}

	private Try<Function1<DownloadResponse,Try<Void>>> sendContent(final ContentType contentType, final String content)
	{
		return success(response ->
		{
			response.setStatusOk();
			dev.luin.file.server.core.server.download.header.ContentType.write(response,contentType);
			ContentLength.write(response,new Length(content.length()));
			response.write(content);
			return success(null);
		});
	}
}
