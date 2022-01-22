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

import static io.vavr.control.Try.failure;
import static io.vavr.control.Try.success;

import java.io.IOException;
import java.util.function.Consumer;
import java.util.function.Function;

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
import io.vavr.control.Option;
import io.vavr.control.Try;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.val;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
class UploadFileHandler implements BaseHandler
{
	@NonNull
	FileSystem fs;
	TusMaxSize tusMaxSize;

	public UploadFileHandler(@NonNull FileSystem fs, TusMaxSize tusMaxSize)
	{
		this.fs = fs;
		this.tusMaxSize = tusMaxSize;
	}

	@Override
	public Try<Consumer<UploadResponse>> handle(@NonNull final UploadRequest request, @NonNull final User user)
	{
		log.debug("HandleUploadFile {}",user);
		return validate(request)
				.flatMap(appendFile(user,fs))
				.flatMap(this::sendResponse);
	}

	private Try<UploadRequest> validate(UploadRequest request)
	{
		return success(request)
				.flatMap(TusResumable::validate)
				.flatMap(ContentType::validate);
	}

	private Function1<UploadRequest,Try<FSFile>> appendFile(User user, FileSystem fs)
	{
		return request -> getFile(user,fs,request)
				.peek(logger("Upload file {}"))
				.flatMap(file -> appendToFile(fs,request,getFileLength(request,file)).apply(file)
						.toTry(UploadException::illegalStateException))
				.peek(logger("Uploaded file {}"));
	}

	private static Consumer<Object> logger(String msg)
	{
		return o -> log.info(msg,o);
	}

	private Try<FSFile> getFile(final User user, final FileSystem fs, final UploadRequest request)
	{
		val file = fs.findFile(user,request.getPath())
				.toTry(() -> UploadException.fileNotFound(request.getPath()));
		val uploadLength = file.flatMap(f -> f.getLength() == null ? UploadLength.of(request,tusMaxSize) : success(Option.<UploadLength>none()));
		return file.flatMap(f ->
				uploadLength.flatMap(length ->
						success(length.map(l -> f.withLength(l.toFileLength()))
								.getOrElse(f))
				));
	}

	private static Function1<FSFile,Try<FSFile>> appendToFile(FileSystem fs, UploadRequest request, Length fileLength)
	{
		try
		{
			return fs.appendToFile(request.getInputStream(),fileLength);
		}
		catch (IOException e)
		{
			return f -> failure(e);
		}
	}

	private static Length getFileLength(final UploadRequest request, final FSFile file)
	{
		return UploadOffset.of(request)
				.flatMap(uploadOffset -> uploadOffset.validateFileLength(file.getFileLength())
						.map(offset -> {
								val contentLength = ContentLength.of(request);
								contentLength.forEach(validateContentLength(file,offset));
								return contentLength.map(toLength()).getOrNull();
						})
				)
				.getOrNull();
	}

	private static Consumer<Option<ContentLength>> validateContentLength(final FSFile file, UploadOffset offset)
	{
		return contentLength -> contentLength.forEach(c -> c.validate(offset,file.getLength()));
	}

	private static Function<Option<ContentLength>,Length> toLength()
	{
		return contentLength -> contentLength.map(ContentLength::toLength).getOrNull();
	}

	private Try<Consumer<UploadResponse>> sendResponse(FSFile file)
	{
		return success(response -> Option.of(response)
				.peek(UploadResponse::setStatusNoContent)
				.peek(r -> UploadOffset.write(r,file.getFileLength()))
				.peek(TusResumable::write));
	}

}
