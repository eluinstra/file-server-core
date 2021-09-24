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

import java.util.function.Consumer;

import dev.luin.file.server.core.file.FSFile;
import dev.luin.file.server.core.file.FileSystem;
import dev.luin.file.server.core.service.user.User;
import io.vavr.Function1;
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
class FileInfoHandler implements BaseHandler
{
	private static final Consumer<FSFile> logGetFileInfo = f -> log.info("GetFileInfo {}",f);
	@NonNull
	FileSystem fs;

	@Override
	public Try<Function1<DownloadResponse,Try<Void>>> handle(@NonNull final DownloadRequest request, @NonNull final User user)
	{
		log.debug("HandleGetFileInfo {}",user);
		return handleRequest(request,user)
				.flatMap(this::sendFileInfo);
	}

	private Try<FSFile> handleRequest(final DownloadRequest request, final User user)
	{
		val path = request.getPath();
		return fs.findFile(user,path)
				.toTry(() -> DownloadException.fileNotFound(path))
				.peek(logGetFileInfo);
	}

	private Try<Function1<DownloadResponse,Try<Void>>> sendFileInfo(final FSFile fsFile)
	{
		return success(response -> success(writeFileInfo(fsFile,response)));
	}

	private Void writeFileInfo(final FSFile fsFile, DownloadResponse response)
	{
		new ResponseWriter(response).writeFileInfo(response,fsFile);
		return null;
	}
}
