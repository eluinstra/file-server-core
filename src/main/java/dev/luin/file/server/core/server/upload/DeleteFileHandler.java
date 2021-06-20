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
import dev.luin.file.server.core.file.VirtualPath;
import dev.luin.file.server.core.server.upload.header.ContentLength;
import dev.luin.file.server.core.server.upload.header.TusResumable;
import dev.luin.file.server.core.service.user.User;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.val;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
class DeleteFileHandler implements BaseHandler
{
	@NonNull
	FileSystem fs;

	@Override
	public void handle(@NonNull final UploadRequest request, @NonNull final UploadResponse response, @NonNull final User User)
	{
		log.debug("HandleDeleteFile {}",User);
		validate(request);
		deleteFile(request.getPath(),User);
		sendResponse(response);
	}

	private void validate(final UploadRequest request)
	{
		TusResumable.validate(request);
		ContentLength.fromNullable(request).equalsZero();
	}

	private void deleteFile(final VirtualPath path, final User User)
	{
		val file = getFile(path,User);
		fs.deleteFile(file,false);
		log.info("Deleted file {}",file);
	}

	private FSFile getFile(final VirtualPath path, final User User)
	{
		return fs.findFile(User,path).getOrElseThrow(() -> UploadException.fileNotFound(path));
	}

	private void sendResponse(final UploadResponse response)
	{
		response.setStatusNoContent();
		TusResumable.write(response);
	}
}
