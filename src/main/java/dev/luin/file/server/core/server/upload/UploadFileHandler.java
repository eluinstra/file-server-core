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

import java.io.IOException;

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
import io.vavr.Function3;
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
	private static final Function1<UploadRequest,Either<UploadException,UploadRequest>> validate =
			request -> Either.<UploadException,UploadRequest>right(request).flatMap(TusResumable::validate).flatMap(ContentType::validate);

	private static final Function3<FileSystem,UploadRequest,Length,Function1<FSFile,Either<IOException,FSFile>>> appendToFile = (fs, request, fileLength) ->
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

	@NonNull
	Function2<User,UploadRequest,FSFile> appendFile = Function2.of(this::appendFile);
	@NonNull
	Function2<UploadResponse,FSFile,Void> sendResponse = Function2.of(this::sendResponse);
	@NonNull
	FileSystem fs;
	TusMaxSize tusMaxSize;

	public UploadFileHandler(@NonNull FileSystem fs, TusMaxSize tusMaxSize)
	{
		this.fs = fs;
		this.tusMaxSize = tusMaxSize;
	}

	@Override
	public Either<UploadException,Void> handle(@NonNull final UploadRequest request, @NonNull final UploadResponse response, @NonNull final User user)
	{
		log.debug("HandleUploadFile {}",user);
		return validate.apply(request)
				.map(appendFile.apply(user))
				.map(sendResponse.apply(response))
				.map(toNull);
	}

	private FSFile appendFile(final User user, final UploadRequest request)
	{
		val file = getFile(user,fs,request);
		log.info("Upload file {}",file);
		val fileLength = getFileLength(request,file);
		val newFile = Either.<IOException,FSFile>right(file)
				.flatMap(appendToFile.apply(fs,request,fileLength))
				.getOrElseThrow(t -> new IllegalStateException(t));
		if (newFile.isCompleted())
			log.info("Uploaded file {}",newFile);
		return file;
	}

	private Length getFileLength(final UploadRequest request, final FSFile file)
	{
		val uploadOffset = UploadOffset.of(request);
		uploadOffset.validateFileLength(file.getFileLength());
		val contentLength = ContentLength.of(request);
		contentLength.forEach(c -> c.validate(uploadOffset,file.getLength()));
		return contentLength.map(v -> v.toLength()).getOrNull();
	}

	private FSFile getFile(final User User, final FileSystem fs, final UploadRequest request)
	{
		val file = fs.findFile(User,request.getPath()).getOrElseThrow(() -> UploadException.fileNotFound(request.getPath()));
		val uploadLength = file.getLength() == null ? UploadLength.of(request,tusMaxSize) : Option.<UploadLength>none();
		//TODO FIXME
		return uploadLength.map(v -> file.withLength(v.toFileLength())).getOrElse(file);
	}

	private Void sendResponse(final UploadResponse response, final FSFile file)
	{
		response.setStatusNoContent();
		UploadOffset.write(response,file.getLength());
		TusResumable.write(response);
		return null;
	}
}
