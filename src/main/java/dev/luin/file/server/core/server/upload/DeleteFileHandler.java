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
import dev.luin.file.server.core.service.model.User;
import lombok.val;
import lombok.extern.slf4j.Slf4j;

@Slf4j
class DeleteFileHandler extends BaseHandler
{
	public DeleteFileHandler(FileSystem fs)
	{
		super(fs);
	}

	@Override
	public void handle(final UploadRequest request, final UploadResponse response, User user)
	{
		log.debug("HandleDeleteFile {}",user);
		validate(request);
		deleteFile(request.getPath(),user);
		sendResponse(response);
	}

	private void validate(final UploadRequest request)
	{
		request.validateTusResumable();
		request.validateContentLength();
	}

	private void deleteFile(final String path, User user)
	{
		val file = getFile(path,user);
		getFs().deleteFile(file,false);
		log.info("Deleted file {}",file);
	}

	private FSFile getFile(final String path, User user)
	{
		return getFs().findFile(user,path).getOrElseThrow(() -> UploadException.fileNotFound(path));
	}

	private void sendResponse(final UploadResponse response)
	{
		response.sendDeleteFileResponse();
	}
}
