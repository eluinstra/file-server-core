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
import dev.luin.file.server.core.service.user.User;
import lombok.NonNull;
import lombok.val;
import lombok.extern.slf4j.Slf4j;

@Slf4j
class FileInfoHandler extends BaseHandler
{
	public FileInfoHandler(FileSystem fs)
	{
		super(fs);
	}

	@Override
	public void handle(@NonNull UploadRequest request, @NonNull UploadResponse response, User User)
	{
		log.debug("HandleGetFileInfo {}",User);
		validate(request);
		val file = findFile(request,User);
		sendResponse(response,file);
	}

	private void validate(UploadRequest request)
	{
		request.validateTusResumable();
	}

	private FSFile findFile(UploadRequest request, User User)
	{
		val path = request.getPath();
		val file = getFs().findFile(User,path).getOrElseThrow(() -> UploadException.fileNotFound(path));
		log.debug("GetFileInfo {}",file);
		return file;
	}

	private void sendResponse(UploadResponse response, final FSFile file)
	{
		response.sendFileInfoResponse(file.getFileLength());
	}
}
