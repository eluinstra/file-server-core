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
package dev.luin.fs.core.server.upload;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dev.luin.fs.core.file.FSFile;
import dev.luin.fs.core.file.FileSystem;
import dev.luin.fs.core.http.HttpException;
import dev.luin.fs.core.server.BaseHandler;
import dev.luin.fs.core.server.upload.header.ContentLength;
import dev.luin.fs.core.server.upload.header.ContentType;
import dev.luin.fs.core.server.upload.header.TusResumable;
import dev.luin.fs.core.server.upload.header.UploadLength;
import dev.luin.fs.core.server.upload.header.UploadOffset;
import dev.luin.fs.core.service.model.User;
import io.vavr.control.Option;
import lombok.val;

class PatchHandler extends BaseHandler
{
	public PatchHandler(FileSystem fs)
	{
		super(fs);
	}

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response, User user) throws IOException
	{
		TusResumable.of(request);
		ContentType.of(request);
		val contentLength = ContentLength.of(request);
		val uploadOffset = UploadOffset.of(request);
		val file = getFile(request,user);
		validate(file,uploadOffset);
		validate(contentLength,file.getLength(),uploadOffset);
		getFs().append(file,request.getInputStream(),contentLength.map(l -> l.getValue()).getOrNull());
		response.setStatus(HttpServletResponse.SC_NO_CONTENT);
		UploadOffset.of(file.getLength()).write(response);
		TusResumable.get().write(response);
	}

	private FSFile getFile(HttpServletRequest request, User user)
	{
		val path = request.getPathInfo();
		val file = getFs().findFile(user,path).getOrElseThrow(() -> HttpException.notFound());
		val uploadLength = file.getLength() == null ? UploadLength.of(request) : Option.<UploadLength>none();
		return uploadLength.map(l -> file.withLength(l.getValue())).getOrElse(file);
	}

	private void validate(FSFile file, UploadOffset uploadOffset)
	{
		if (file.getFileLength() != uploadOffset.getValue())
			throw HttpException.conflictException();
	}

	private void validate(Option<ContentLength> contentLength, Long fileLength, UploadOffset uploadOffset)
	{
		if (contentLength.isDefined() && fileLength != null)
			if (uploadOffset.getValue() + contentLength.get().getValue() > fileLength)
				throw HttpException.badRequestException();
	}
}
