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

import static dev.luin.file.server.core.Common.toNull;

import java.util.function.Consumer;

import dev.luin.file.server.core.file.FSFile;
import dev.luin.file.server.core.file.FileSystem;
import dev.luin.file.server.core.file.VirtualPath;
import dev.luin.file.server.core.server.upload.header.CacheControl;
import dev.luin.file.server.core.server.upload.header.TusResumable;
import dev.luin.file.server.core.server.upload.header.UploadOffset;
import dev.luin.file.server.core.service.user.User;
import io.vavr.Function1;
import io.vavr.Function2;
import io.vavr.control.Either;
import io.vavr.control.Option;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
class FileInfoHandler implements BaseHandler
{
	private static final Function1<UploadRequest,Either<UploadException,UploadRequest>> validate =
			request -> Either.<UploadException,UploadRequest>right(request).flatMap(TusResumable::validate);

	private static final Consumer<FSFile> logGetFileInfo = f -> log.debug("GetFileInfo {}",f);

	private static final Function1<UploadResponse,Consumer<FSFile>> sendResponse =
			response -> file -> Option.of(response)
				.peek(UploadResponse::setStatusCreated)
				.peek(r -> UploadOffset.write(r,file.getFileLength()))
				.peek(TusResumable::write)
				.peek(CacheControl::write);

	@NonNull
	Function2<User,UploadRequest,Either<UploadException,FSFile>> findFile;

	public FileInfoHandler(@NonNull FileSystem fs)
	{
		findFile = (user,request) -> Either.<UploadException,VirtualPath>right(request.getPath())
				.flatMap(p -> fs.findFile(user,p)
						.toEither(UploadException.fileNotFound(p)));
	}

	@Override
	public Either<UploadException,Void> handle(@NonNull final UploadRequest request, @NonNull final UploadResponse response, @NonNull final User user)
	{
		log.debug("HandleGetFileInfo {}",user);
		return validate.apply(request)
				.flatMap(findFile.apply(user))
				.peek(logGetFileInfo)
				.peek(sendResponse.apply(response))
				.map(toNull);
	}
}
