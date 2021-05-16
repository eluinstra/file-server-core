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

import dev.luin.file.server.core.file.FSFile;
import dev.luin.file.server.core.file.FileSystem;
import dev.luin.file.server.core.http.HttpException;
import dev.luin.file.server.core.server.upload.header.ContentLength;
import dev.luin.file.server.core.server.upload.header.ContentType;
import dev.luin.file.server.core.server.upload.header.TusResumable;
import dev.luin.file.server.core.server.upload.header.UploadLength;
import dev.luin.file.server.core.server.upload.header.UploadOffset;
import dev.luin.file.server.core.service.model.User;
import io.vavr.control.Option;
import lombok.val;
import lombok.extern.slf4j.Slf4j;

@Slf4j
class PatchHandler extends BaseHandler
{
	public PatchHandler(FileSystem fs)
	{
		super(fs);
	}

	@Override
	public void handle(UploadRequest request, UploadResponse response, User user) throws IOException
	{
		log.debug("HandlePatch {}",user);
		val file = handleRequest(request,user);
		sendResponse(response,file);
	}

	private FSFile handleRequest(UploadRequest request, User user) throws IOException
	{
		TusResumable.of(request);
		ContentType.of(request);
		val contentLength = ContentLength.of(request);
		val uploadOffset = UploadOffset.of(request);
		val file = getFile(request,user);
		log.info("Upload file {}",file);
		validate(file,uploadOffset);
		validate(contentLength,file.getLength(),uploadOffset);
		val newFile = getFs().append(file,request.getInputStream(),contentLength.map(l -> l.getValue()).getOrNull());
		if (newFile.isCompleted())
			log.info("Uploaded file {}",newFile);
		return file;
	}

	private void sendResponse(UploadResponse response, final FSFile file)
	{
		response.setStatus(UploadResponseStatus.NO_CONTENT);
		UploadOffset.of(file.getLength()).write(response);
		TusResumable.get().write(response);
	}

	private FSFile getFile(UploadRequest request, User user)
	{
		val path = request.getPath();
		val file = getFs().findFile(user,path).getOrElseThrow(() -> HttpException.notFound(path));
		val uploadLength = file.getLength() == null ? UploadLength.of(request) : Option.<UploadLength>none();
		return uploadLength.map(l -> file.withLength(l.getValue())).getOrElse(file);
	}

	private void validate(FSFile file, UploadOffset uploadOffset)
	{
		if (file.getFileLength() != uploadOffset.getValue())
			throw HttpException.conflict();
	}

	private void validate(Option<ContentLength> contentLength, Long fileLength, UploadOffset uploadOffset)
	{
		if (contentLength.isDefined() && fileLength != null)
			if (uploadOffset.getValue() + contentLength.get().getValue() > fileLength)
				throw HttpException.badRequest();
	}
}
