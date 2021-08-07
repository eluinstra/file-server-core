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

import static io.vavr.control.Try.failure;
import static io.vavr.control.Try.success;

import dev.luin.file.server.core.file.FSFile;
import dev.luin.file.server.core.server.download.header.ContentRange;
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
public class FileHandlerImpl implements FileHandler
{
	@NonNull
	FSFile fsFile;

	@Override
	public Try<Function1<DownloadResponse,Try<Void>>> handle(@NonNull final DownloadRequest request)
	{
		log.info("Download {}",fsFile);
		return getRanges(request,fsFile)
				.flatMap(ranges -> sendFile(fsFile,ranges));
	}

	private Try<ContentRange> getRanges(final DownloadRequest request, final FSFile fsFile)
	{
		if (!fsFile.isCompleted())
			return failure(DownloadException.fileNotFound(fsFile.getVirtualPath()));
		return ContentRange.of(request,fsFile);
	}

	private Try<Function1<DownloadResponse,Try<Void>>> sendFile(final FSFile fsFile, final ContentRange ranges)
	{
		return success(response ->
		{
			return success(new ResponseWriter(response))
					.flatMap(writer -> writer.write(fsFile,ranges).apply(response))
					.map(x -> null);
		});
	}
}
