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
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bitbucket.eluinstra.fs.core.ClientManager;
import org.bitbucket.eluinstra.fs.core.file.FileSystem;
import org.bitbucket.eluinstra.fs.core.server.FSHttpException;
import org.bitbucket.eluinstra.fs.core.server.download.ContentType;

import lombok.NonNull;
import lombok.val;

public class CreateHandler extends BaseHandler
{
	public CreateHandler(@NonNull FileSystem fs, @NonNull ClientManager clientManager)
	{
		super(fs,clientManager);
	}

	@Override
	public void handle(final HttpServletRequest request, final HttpServletResponse response, @NonNull byte[] clientCertificate) throws IOException
	{
		val path = request.getPathInfo();
		val name = getName(path).orElseThrow(() -> new FSHttpException(401));
		val client = getClientManager().findClient(name,clientCertificate).orElseThrow(() -> new FSHttpException(404));
		//getFs().findFile(clientCertificate,path).orElseThrow(() -> new FSHttpException(400));
		val uploadMetadata = UploadMetadata.of(request.getHeader(UploadMetadata.headerName));
		val filename = uploadMetadata.getParameter("filename");
		val contentType = ContentType.of(request.getHeader(ContentType.headerName));
		val file = getFs().createFile(path,filename,contentType.getBaseType(),null,client.getId(),request.getInputStream());
		response.setStatus(201);
		response.setHeader("Location",file.getVirtualPath());
	}

	private Optional<String> getName(String path)
	{
		val result = path.replaceFirst("^/[^/]*/.*$","$1");
		return Optional.ofNullable(path.length() != result.length() ? result : null);
	}
}
