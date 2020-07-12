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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bitbucket.eluinstra.fs.core.file.FileSystem;
import org.bitbucket.eluinstra.fs.core.http.HttpException;
import org.bitbucket.eluinstra.fs.core.server.upload.header.CacheControl;
import org.bitbucket.eluinstra.fs.core.server.upload.header.TusResumable;
import org.bitbucket.eluinstra.fs.core.server.upload.header.UploadOffset;
import org.bitbucket.eluinstra.fs.core.service.model.Client;

import lombok.NonNull;
import lombok.val;

public class HeadHandler extends BaseHandler
{
	public HeadHandler(FileSystem fs)
	{
		super(fs);
	}

	@Override
	public void handle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, Client client)
	{
		TusResumable.of(request);
		val path = request.getPathInfo();
		val file = getFs().findFile(client.getCertificate(),path).getOrElseThrow(() -> HttpException.notFound());
		response.setStatus(HttpServletResponse.SC_CREATED);
		UploadOffset.of(file.getFileLength()).write(response);
		TusResumable.get().write(response);
		CacheControl.get().write(response);
	}
}
