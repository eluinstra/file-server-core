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

import dev.luin.fs.core.file.FileSystem;
import dev.luin.fs.core.http.HttpException;
import dev.luin.fs.core.server.BaseHandler;
import dev.luin.fs.core.server.upload.header.ContentLength;
import dev.luin.fs.core.server.upload.header.Location;
import dev.luin.fs.core.server.upload.header.TusResumable;
import dev.luin.fs.core.server.upload.header.UploadDeferLength;
import dev.luin.fs.core.server.upload.header.UploadLength;
import dev.luin.fs.core.server.upload.header.UploadMetadata;
import dev.luin.fs.core.service.model.User;
import lombok.val;

class PostHandler extends BaseHandler
{
	public PostHandler(FileSystem fs)
	{
		super(fs);
	}

	@Override
	public void handle(final HttpServletRequest request, final HttpServletResponse response, User user) throws IOException
	{
		TusResumable.of(request);
		val uploadMetadata = UploadMetadata.of(request);
		val filename = uploadMetadata.map(m -> m.getParameter("filename")).getOrNull();
		val contentType = uploadMetadata.map(m -> m.getParameter("content-type")).getOrElse("application/octet-stream");
		val contentLength = ContentLength.of(request);
		if (contentLength.isDefined())
			contentLength.filter(l -> l.getValue() == 0).getOrElseThrow(() -> HttpException.invalidHeaderException(ContentLength.HEADER_NAME));
		val uploadLength = UploadLength.of(request);
		if (!uploadLength.isDefined())
			UploadDeferLength.of(request).getOrElseThrow(() -> HttpException.invalidHeaderException(UploadLength.HEADER_NAME));
		val file = getFs().createEmptyFile(filename,contentType,null,uploadLength.map(l -> l.getValue()).getOrNull(),user.getId());
		response.setStatus(HttpServletResponse.SC_CREATED);
		Location.of(file.getVirtualPath()).forEach(h -> h.write(response));
		TusResumable.get().write(response);
	}
}