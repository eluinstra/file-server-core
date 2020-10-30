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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dev.luin.file.server.core.file.FSFile;
import dev.luin.file.server.core.file.FileSystem;
import dev.luin.file.server.core.http.HttpException;
import dev.luin.file.server.core.server.BaseHandler;
import dev.luin.file.server.core.server.upload.header.ContentLength;
import dev.luin.file.server.core.server.upload.header.Location;
import dev.luin.file.server.core.server.upload.header.TusResumable;
import dev.luin.file.server.core.server.upload.header.UploadDeferLength;
import dev.luin.file.server.core.server.upload.header.UploadLength;
import dev.luin.file.server.core.server.upload.header.UploadMetadata;
import dev.luin.file.server.core.service.model.User;
import io.vavr.control.Option;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.val;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
class PostHandler extends BaseHandler
{
	@NonNull
	String uploadPath;

	public PostHandler(FileSystem fs, String uploadPath)
	{
		super(fs);
		this.uploadPath = uploadPath;
	}

	@Override
	public void handle(final HttpServletRequest request, final HttpServletResponse response, User user) throws IOException
	{
		log.debug("HandlePost {}",user);
		val file = handleRequest(request,user);
		sendResponse(response,file);
	}

	private FSFile handleRequest(final HttpServletRequest request, User user) throws IOException
	{
		TusResumable.of(request);
		val uploadMetadata = UploadMetadata.of(request);
		val filename = uploadMetadata.map(m -> m.getParameter("filename")).getOrNull();
		val contentType = uploadMetadata.map(m -> m.getParameter("Content-Type")).getOrElse("application/octet-stream");
		getContentLength(request);
		val uploadLength = getUploadLength(request);
		val file = getFs().createEmptyFile(filename,contentType,uploadLength.map(l -> l.getValue()).getOrNull(),user.getId());
		log.info("Created file {}",file);
		return file;
	}

	private Option<ContentLength> getContentLength(final HttpServletRequest request)
	{
		val result = ContentLength.of(request);
		if (result.isDefined())
			result.filter(l -> l.getValue() == 0).getOrElseThrow(() -> HttpException.invalidHeaderException(ContentLength.HEADER_NAME));
		return result;
	}

	private Option<UploadLength> getUploadLength(final HttpServletRequest request)
	{
		val uploadLength = UploadLength.of(request);
		if (!uploadLength.isDefined())
			UploadDeferLength.of(request).getOrElseThrow(() -> HttpException.invalidHeaderException(UploadLength.HEADER_NAME));
		return uploadLength;
	}

	private void sendResponse(final HttpServletResponse response, final FSFile file)
	{
		response.setStatus(HttpServletResponse.SC_CREATED);
		Location.of(uploadPath + file.getVirtualPath()).forEach(h -> h.write(response));
		TusResumable.get().write(response);
	}
}
