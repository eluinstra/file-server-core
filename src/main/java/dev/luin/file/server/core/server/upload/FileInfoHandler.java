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
package dev.luin.file.server.core.server.upload;

import java.util.function.Consumer;

import dev.luin.file.server.core.file.FSFile;
import dev.luin.file.server.core.file.FileSystem;
import dev.luin.file.server.core.server.upload.header.CacheControl;
import dev.luin.file.server.core.server.upload.header.TusResumable;
import dev.luin.file.server.core.server.upload.header.UploadOffset;
import dev.luin.file.server.core.service.user.User;
import io.vavr.Function1;
import io.vavr.Function2;
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
	private static final Function1<UploadRequest,Either<UploadException,UploadRequest>> validate =
			request -> Either.<UploadException,UploadRequest>right(request).flatMap(TusResumable::validate);
	private final Function2<User,UploadRequest,Either<UploadException,FSFile>> findFile = Function2.of(this::findFile);
	private final Function2<UploadResponse,FSFile,Void> sendResponse = Function2.of(this::sendResponse);
	private final Consumer<FSFile> logGetFileInfo = f -> log.debug("GetFileInfo {}",f);
	
	@NonNull
	FileSystem fs;

	@Override
	public Either<UploadException,Void> handle(@NonNull final UploadRequest request, @NonNull final UploadResponse response, @NonNull final User user)
	{
		log.debug("HandleGetFileInfo {}",user);
		return validate.apply(request)
				.flatMap(findFile.apply(user))
				.peek(logGetFileInfo)
				.map(sendResponse.apply(response));
	}

	private Either<UploadException,FSFile> findFile(final User User, final UploadRequest request)
	{
		val path = request.getPath();
		return fs.findFile(User,path)
				.toEither(UploadException.fileNotFound(path));
	}

	private Void sendResponse(final UploadResponse response, final FSFile file)
	{
		response.setStatusCreated();
		UploadOffset.write(response,file.getFileLength());
		TusResumable.write(response);
		CacheControl.write(response);
		return null;
	}
}
