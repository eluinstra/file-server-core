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

import dev.luin.file.server.core.file.FSFile;
import dev.luin.file.server.core.file.FileSystem;
import dev.luin.file.server.core.server.upload.header.ContentLength;
import dev.luin.file.server.core.server.upload.header.ContentType;
import dev.luin.file.server.core.server.upload.header.TusMaxSize;
import dev.luin.file.server.core.server.upload.header.TusResumable;
import dev.luin.file.server.core.server.upload.header.UploadLength;
import dev.luin.file.server.core.server.upload.header.UploadOffset;
import dev.luin.file.server.core.service.user.User;
import io.vavr.control.Option;
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
class UploadFileHandler implements BaseHandler
{
	@NonNull
	FileSystem fs;
	TusMaxSize tusMaxSize;

	@Override
	public void handle(@NonNull final UploadRequest request, @NonNull final UploadResponse response, @NonNull final User User)
	{
		log.debug("HandleUploadFile {}",User);
		validate(request);
		val file = appendFile(request,User);
		sendResponse(response,file);
	}

	private void validate(final UploadRequest request)
	{
		TusResumable.validate(request);
		ContentType.validate(request);
	}

	private FSFile appendFile(final UploadRequest request, final User User)
	{
		val file = getFile(User,fs,request);
		log.info("Upload file {}",file);
		val uploadOffset = UploadOffset.of(request);
		uploadOffset.validateFileLength(file.getFileLength());
		val contentLength = ContentLength.of(request);
		contentLength.forEach(c -> c.validate(uploadOffset,file.getLength()));
		val fileLength = contentLength.map(v -> v.toLength()).getOrNull();
		val newFile = Try.success(file)
				.andThenTry(f -> fs.appendToFile(f,request.getInputStream(),fileLength))
				.getOrElseThrow(t -> new IllegalStateException(t));
		if (newFile.isCompleted())
			log.info("Uploaded file {}",newFile);
		return file;
	}

	private FSFile getFile(final User User, final FileSystem fs, final UploadRequest request)
	{
		val file = fs.findFile(User,request.getPath()).getOrElseThrow(() -> UploadException.fileNotFound(request.getPath()));
		val uploadLength = file.getLength() == null ? UploadLength.of(request,tusMaxSize) : Option.<UploadLength>none();
		//TODO FIXME
		return uploadLength.map(v -> file.withLength(v.toFileLength())).getOrElse(file);
	}

	private void sendResponse(final UploadResponse response, final FSFile file)
	{
		response.setStatusNoContent();
		UploadOffset.write(response,file.getLength());
		TusResumable.write(response);
	}
}
