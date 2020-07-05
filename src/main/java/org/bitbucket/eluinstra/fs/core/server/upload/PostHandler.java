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
package org.bitbucket.eluinstra.fs.core.server.upload;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bitbucket.eluinstra.fs.core.file.FileSystem;
import org.bitbucket.eluinstra.fs.core.server.download.ContentType;
import org.bitbucket.eluinstra.fs.core.service.model.Client;

import lombok.val;

public class PostHandler extends BaseHandler
{
	public PostHandler(FileSystem fs)
	{
		super(fs);
	}

	@Override
	public void handle(final HttpServletRequest request, final HttpServletResponse response, Client client) throws IOException
	{
		validateTUSHeader(request);
		val path = request.getPathInfo();
		//getFs().findFile(client.getCertificate(),path).orElseThrow(() -> new FSBadRequestException());
		val uploadMetadata = UploadMetadata.of(request.getHeader(UploadMetadata.headerName));
		val filename = uploadMetadata.getParameter("filename");
		val contentType = ContentType.of(request.getHeader(ContentType.headerName));
		val file = getFs().createFile(path,filename,contentType.getBaseType(),null,client.getId(),request.getInputStream());
		response.setStatus(201);
		response.setHeader("Location",file.getVirtualPath());
		response.setHeader("Tus-Resumable","1.0.0");
	}
}
