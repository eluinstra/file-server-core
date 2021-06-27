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
import dev.luin.file.server.core.file.FileSystem;
import dev.luin.file.server.core.service.user.User;
import io.vavr.control.Either;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.val;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
class FileInfoHandler implements BaseHandler
{
	private static final Consumer<FSFile> logGetFileInfo = f -> log.info("GetFileInfo {}",f);
	@NonNull
	FileSystem fs;

	@Override
	public Either<DownloadException,Void> handle(@NonNull final DownloadRequest request, @NonNull final DownloadResponse response, @NonNull final User user)
	{
		log.debug("HandleGetFileInfo {}",user);
		return handleRequest(request,user)
				.flatMap(f -> sendFileInfo(response,f));
	}

	private Either<DownloadException,FSFile> handleRequest(final DownloadRequest request, final User user)
	{
		val path = request.getPath();
		return fs.findFile(user,path)
				.toEither(() -> DownloadException.fileNotFound(path))
				.peek(logGetFileInfo);
	}

	private Either<DownloadException,Void> sendFileInfo(final DownloadResponse response, final FSFile fsFile)
	{
		new ResponseWriter(response).writeFileInfo(fsFile);
		return Either.right(null);
	}
}
