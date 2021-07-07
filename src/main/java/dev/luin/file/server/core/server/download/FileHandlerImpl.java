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

import java.util.function.Consumer;

import dev.luin.file.server.core.file.FSFile;
import dev.luin.file.server.core.server.download.header.ContentRange;
import io.vavr.control.Try;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.val;
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
	public Consumer<DownloadResponse> handle(@NonNull final DownloadRequest request)
	{
		log.info("Download {}",fsFile);
		val ranges = getRanges(request,fsFile);
		return sendFile(fsFile,ranges);
	}

	private ContentRange getRanges(final DownloadRequest request, final FSFile fsFile)
	{
		if (!fsFile.isCompleted())
			throw DownloadException.fileNotFound(fsFile.getVirtualPath());
		return new ContentRange(request,fsFile);
	}

	private Consumer<DownloadResponse> sendFile(final FSFile fsFile, final ContentRange ranges)
	{
		return response ->
		{
			Try.success(response)
				.map(ResponseWriter::new)
				//FIXME
				.andThenTry(w -> w.write(fsFile,ranges))
				.getOrElseThrow(t -> new IllegalStateException(t));
		};
	}
}
