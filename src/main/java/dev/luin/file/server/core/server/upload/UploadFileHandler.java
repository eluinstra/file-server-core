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

import java.io.IOException;
import java.util.function.Consumer;

import dev.luin.file.server.core.file.FSFile;
import dev.luin.file.server.core.file.FileSystem;
import dev.luin.file.server.core.file.Length;
import dev.luin.file.server.core.server.upload.header.ContentLength;
import dev.luin.file.server.core.server.upload.header.ContentType;
import dev.luin.file.server.core.server.upload.header.TusMaxSize;
import dev.luin.file.server.core.server.upload.header.TusResumable;
import dev.luin.file.server.core.server.upload.header.UploadLength;
import dev.luin.file.server.core.server.upload.header.UploadOffset;
import dev.luin.file.server.core.service.user.User;
import io.vavr.Function1;
import io.vavr.Function2;
import io.vavr.control.Either;
import io.vavr.control.Option;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.val;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
class UploadFileHandler implements BaseHandler
{
	private static final Function1<String,Consumer<FSFile>> logger = m -> o -> log.info(m,o);
	
	@NonNull
	FileSystem fs;
	TusMaxSize tusMaxSize;

	public UploadFileHandler(@NonNull FileSystem fs, TusMaxSize tusMaxSize)
	{
		this.fs = fs;
		this.tusMaxSize = tusMaxSize;
	}

	@Override
	public Either<UploadException,Consumer<UploadResponse>> handle(@NonNull final UploadRequest request, @NonNull final User user)
	{
		log.debug("HandleUploadFile {}",user);
		return validate(request)
				.flatMap(appendFile().apply(user))
				.flatMap(this::sendResponse);
	}

	private Either<UploadException,UploadRequest> validate(UploadRequest request)
	{
		return Either.<UploadException,UploadRequest>right(request)
				.flatMap(TusResumable::validate)
				.flatMap(ContentType::validate);
	}

	private Function2<User,UploadRequest,Either<UploadException,FSFile>> appendFile()
	{
		return (user,request) ->
		{
			return getFile(user,fs,request)
					.peek(logger.apply("Upload file {}"))
					.flatMap(file -> appendToFile(fs,request,getFileLength(request,file)).apply(file)
							.mapLeft(UploadException::illegalStateException))
					.peek(logger.apply("Uploaded file {}"));
		};
	}

	private Either<UploadException,FSFile> getFile(final User User, final FileSystem fs, final UploadRequest request)
	{
		val file = fs.findFile(User,request.getPath())
				.toEither(() -> UploadException.fileNotFound(request.getPath()));
		val uploadLength = file.flatMap(f -> f.getLength() == null ? UploadLength.of(request,tusMaxSize) : Either.right(Option.<UploadLength>none()));
		return file.flatMap(f ->
				uploadLength.flatMap(length ->
					Either.right(length.map(l -> f.withLength(l.toFileLength()))
							.getOrElse(f))
				));
	}

	private Function1<FSFile,Either<IOException,FSFile>> appendToFile(FileSystem fs, UploadRequest request, Length fileLength)
	{
		try
		{
			return fs.appendToFile().apply(request.getInputStream(),fileLength);
		}
		catch (IOException e)
		{
			return f -> Either.left(e);
		}
	};

	private Length getFileLength(final UploadRequest request, final FSFile file)
	{
		return UploadOffset.of(request)
				.flatMap(offset -> offset.validateFileLength(file.getFileLength())
						.map(o -> {
								val contentLength = ContentLength.of(request);
								contentLength.forEach(c -> c.validate(o,file.getLength()));
								return contentLength.map(v -> v.toLength()).getOrNull();
						})
				)
				.getOrNull();
	}

	private Either<UploadException,Consumer<UploadResponse>> sendResponse(FSFile file)
	{
		return Either.right(response -> Option.of(response)
				.peek(UploadResponse::setStatusNoContent)
				.peek(r -> UploadOffset.write(r,file.getFileLength()))
				.peek(TusResumable::write));
	}

}
