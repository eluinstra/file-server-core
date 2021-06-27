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

import org.slf4j.Logger;

import dev.luin.file.server.core.file.FSFile;
import dev.luin.file.server.core.file.FileSystem;
import dev.luin.file.server.core.file.VirtualPath;
import dev.luin.file.server.core.server.upload.header.ContentLength;
import dev.luin.file.server.core.server.upload.header.TusResumable;
import dev.luin.file.server.core.service.user.User;
import io.vavr.Function1;
import io.vavr.Function2;
import io.vavr.control.Either;
import io.vavr.control.Option;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
class DeleteFileHandler implements BaseHandler
{
	private final static Function1<UploadRequest,Either<UploadException,UploadRequest>> validate = 
			request -> Either.<UploadException,UploadRequest>right(request).flatMap(TusResumable::validate).flatMap(ContentLength::equalsZero);

	private static final Function1<Logger,Consumer<FSFile>> logFileDeleted = logger -> f -> log.info("Deleted file {}",f);

	private static final Consumer<UploadResponse> sendResponse = response -> Option.of(response)
			.peek(UploadResponse::setStatusNoContent)
			.peek(TusResumable::write);

	@NonNull
	Function2<User,VirtualPath,Either<UploadException,FSFile>> deleteFile;

	public DeleteFileHandler(@NonNull FileSystem fs)
	{
		deleteFile = (user,path) -> fs.findFile(user,path)
				.toEither(() -> UploadException.fileNotFound(path))
				.flatMap(f -> fs.deleteFile().apply(true,f)
						.map(v -> f)
						.mapLeft(t -> UploadException.illegalStateException(t)));
	}

	@Override
	public Either<UploadException,Void> handle(@NonNull final UploadRequest request, @NonNull final UploadResponse response, @NonNull final User user)
	{
		log.debug("HandleDeleteFile {}",user);
		return validate.apply(request)
				.map(UploadRequest::getPath)
				.flatMap(deleteFile.apply(user))
				.peek(logFileDeleted.apply(log))
				.peek(v -> sendResponse.accept(response))
				.map(toNull);
	}
}
