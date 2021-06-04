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
import dev.luin.file.server.core.server.upload.header.UploadLength;
import dev.luin.file.server.core.service.user.User;
import lombok.val;
import lombok.extern.slf4j.Slf4j;

@Slf4j
class UploadFileHandler extends BaseHandler
{
	public UploadFileHandler(FileSystem fs)
	{
		super(fs);
	}

	@Override
	public void handle(UploadRequest request, UploadResponse response, User user) throws IOException
	{
		log.debug("HandleUploadFile {}",user);
		validate(request);
		val file = appendFile(request,user);
		sendResponse(response,file);
	}

	private void validate(UploadRequest request)
	{
		request.validateTusResumable();
		request.validateContentType();
	}

	private FSFile appendFile(UploadRequest request, User user) throws IOException
	{
		val file = getFile(user,getFs(),request);
		log.info("Upload file {}",file);
		val uploadOffset = request.getUploadOffset();
		uploadOffset.validateFileLength(file.getFileLength());
		val contentLength = request.getContentLength();
		contentLength.validate(uploadOffset,file.getLength());
		val newFile = getFs().appendToFile(file,request.getInputStream(),contentLength.toFileLength());
		if (newFile.isCompleted())
			log.info("Uploaded file {}",newFile);
		return file;
	}

	private FSFile getFile(User user, FileSystem fs, UploadRequest request)
	{
		val file = fs.findFile(user,request.getPath()).getOrElseThrow(() -> UploadException.fileNotFound(request.getPath()));
		val uploadLength = file.getLength() == null ? request.getUploadLength() : new UploadLength();
		//TODO FIXME
		return uploadLength.map(l -> file.withLength(uploadLength.toFileLength())).getOrElse(file);
	}

	private void sendResponse(UploadResponse response, final FSFile file)
	{
		response.sendUploadFileResponse(file.getLength());
	}
}
