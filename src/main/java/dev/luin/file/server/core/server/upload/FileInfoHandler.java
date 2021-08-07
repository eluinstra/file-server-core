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

import static io.vavr.control.Try.success;

import java.util.function.Consumer;
import java.util.function.Function;

import dev.luin.file.server.core.file.FSFile;
import dev.luin.file.server.core.file.FileSystem;
import dev.luin.file.server.core.server.upload.header.CacheControl;
import dev.luin.file.server.core.server.upload.header.TusResumable;
import dev.luin.file.server.core.server.upload.header.UploadOffset;
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
class FileInfoHandler implements BaseHandler
{
	@NonNull
	FileSystem fs;

	@Override
	public Try<Consumer<UploadResponse>> handle(@NonNull final UploadRequest request, @NonNull final User user)
	{
		log.debug("HandleGetFileInfo {}",user);
		return validate(request)
				.flatMap(findFile(user))
				.peek(logger("GetFileInfo {}"))
				.flatMap(this::sendResponse);
	}

	private static Consumer<Object> logger(String msg)
	{
		return o -> log.info(msg,o);
	}

	private Function<UploadRequest,Try<FSFile>> findFile(User user)
	{
		return request -> success(request.getPath())
		.flatMap(path -> fs.findFile(user,path)
				.toTry(() -> UploadException.fileNotFound(path)));
	}

	private static Try<UploadRequest> validate(UploadRequest request)
	{
		return success(request)
				.flatMap(TusResumable::validate);
	}

	private Try<Consumer<UploadResponse>> sendResponse(FSFile file)
	{
		return success(response -> Option.of(response)
				.peek(UploadResponse::setStatusCreated)
				.peek(r -> UploadOffset.write(r,file.getFileLength()))
				.peek(TusResumable::write)
				.peek(CacheControl::write));
	}
}
