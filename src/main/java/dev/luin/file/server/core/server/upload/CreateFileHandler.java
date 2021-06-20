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
import dev.luin.file.server.core.server.upload.header.ContentLength;
import dev.luin.file.server.core.server.upload.header.Location;
import dev.luin.file.server.core.server.upload.header.TusMaxSize;
import dev.luin.file.server.core.server.upload.header.TusResumable;
import dev.luin.file.server.core.service.user.User;
import io.vavr.Function1;
import io.vavr.Function2;
import io.vavr.control.Either;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
class CreateFileHandler implements BaseHandler
{
	private static final Function1<UploadRequest,Either<UploadException,UploadRequest>> validate =
			request -> Either.<UploadException,UploadRequest>right(request).flatMap(TusResumable::validate).flatMap(ContentLength::equalsZero);
	private final Function2<User,UploadRequest,FSFile> createFile = Function2.of(this::createFile);
	private final Function2<UploadResponse,FSFile,Void> sendResponse = Function2.of(this::sendResponse);
	private final Consumer<FSFile> logFileCreated = f -> log.info("Created file {}",f);
	@NonNull
	FileSystem fs;
	@NonNull
	String uploadPath;
	TusMaxSize tusMaxSize;

	@Override
	public Either<UploadException,Void> handle(@NonNull final UploadRequest request, @NonNull final UploadResponse response, @NonNull final User user)
	{
		log.debug("HandleCreateFile {}",user);
		return validate.apply(request)
				.map(createFile.apply(user))
				.peek(logFileCreated)
				.map(sendResponse.apply(response));
	}

	private FSFile createFile(final User user, final UploadRequest request)
	{
		return fs.createEmptyFile(EmptyFSFileImpl.of(request,tusMaxSize),user.getId());
	}

	private Void sendResponse(final UploadResponse response, final FSFile file)
	{
		response.setStatusCreated();
		Location.write(response,uploadPath + file.getVirtualPath());
		TusResumable.write(response);
		return null;
	}
}
