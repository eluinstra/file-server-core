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
import dev.luin.file.server.core.http.HttpException;
import dev.luin.file.server.core.server.upload.header.CacheControl;
import dev.luin.file.server.core.server.upload.header.TusResumable;
import dev.luin.file.server.core.server.upload.header.UploadOffset;
import dev.luin.file.server.core.service.model.User;
import lombok.NonNull;
import lombok.val;
import lombok.extern.slf4j.Slf4j;

@Slf4j
class HeadHandler extends BaseHandler
{
	public HeadHandler(FileSystem fs)
	{
		super(fs);
	}

	@Override
	public void handle(@NonNull UploadRequest request, @NonNull UploadResponse response, User user)
	{
		log.debug("HandleHead {}",user);
		val file = handleRequest(request,user);
		sendResponse(response,file);
	}

	private FSFile handleRequest(UploadRequest request, User user)
	{
		TusResumable.of(request);
		val path = request.getPath();
		val file = getFs().findFile(user,path).getOrElseThrow(() -> HttpException.notFound(path));
		log.debug("GetFileInfo {}",file);
		return file;
	}

	private void sendResponse(UploadResponse response, final FSFile file)
	{
		response.setStatus(UploadResponseStatus.CREATED);
		UploadOffset.of(file.getFileLength()).write(response);
		TusResumable.get().write(response);
		CacheControl.get().write(response);
	}
}
