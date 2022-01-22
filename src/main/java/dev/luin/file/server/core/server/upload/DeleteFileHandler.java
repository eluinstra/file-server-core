/*
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

import static io.vavr.control.Try.success;

import java.util.function.Consumer;
import java.util.function.Function;

import dev.luin.file.server.core.file.FSFile;
import dev.luin.file.server.core.file.FileSystem;
import dev.luin.file.server.core.file.VirtualPath;
import dev.luin.file.server.core.server.upload.header.ContentLength;
import dev.luin.file.server.core.server.upload.header.TusResumable;
import dev.luin.file.server.core.service.user.User;
import io.vavr.control.Option;
import io.vavr.control.Try;
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
	@NonNull
	FileSystem fs;

	@Override
	public Try<Consumer<UploadResponse>> handle(@NonNull final UploadRequest request, @NonNull final User user)
	{
		log.debug("HandleDeleteFile {}",user);
		return validate(request)
				.map(UploadRequest::getPath)
				.flatMap(deleteFile(user))
				.peek(logger("Deleted file {}"))
				.flatMap(file -> sendResponse());
	}

	private Function<VirtualPath,Try<FSFile>> deleteFile(User user)
	{
		return path -> fs.findFile(user,path)
		.toTry(() -> UploadException.fileNotFound(path))
		.flatMap(file -> fs.deleteFile(true).apply(file)
				.map(isDeleted -> file));
	}

	private static Consumer<Object> logger(String msg)
	{
		return o -> log.info(msg,o);
	}

	private static Try<UploadRequest> validate(UploadRequest request)
	{
		return success(request)
				.flatMap(TusResumable::validate)
				.flatMap(ContentLength::equalsEmptyOrZero);
	}

	private Try<Consumer<UploadResponse>> sendResponse()
	{
		return success(response -> Option.of(response)
				.peek(UploadResponse::setStatusNoContent)
				.peek(TusResumable::write));
	}

}
