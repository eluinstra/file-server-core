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
import dev.luin.file.server.core.service.user.User;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.val;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
class CreateFileHandler extends BaseHandler
{
	@NonNull
	String uploadPath;

	public CreateFileHandler(FileSystem fs, String uploadPath)
	{
		super(fs);
		this.uploadPath = uploadPath;
	}

	@Override
	public void handle(final UploadRequest request, final UploadResponse response, User user) throws IOException
	{
		log.debug("HandleCreateFile {}",user);
		validate(request);
		val file = createFile(request,user);
		sendResponse(response,file);
	}

	private void validate(final UploadRequest request)
	{
		request.validateTusResumable();
		request.getContentLength().assertEquals(0);
	}

	private FSFile createFile(final UploadRequest request, User user) throws IOException
	{
		val file = getFs().createEmptyFile(EmptyFSFileImpl.of(request),user.getId());
		log.info("Created file {}",file);
		return file;
	}

	private void sendResponse(final UploadResponse response, final FSFile file)
	{
		response.sendCreateFileResponse(file,uploadPath);
	}
}
