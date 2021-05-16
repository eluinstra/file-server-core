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
package dev.luin.file.server.core.server.download;

import java.io.IOException;

import dev.luin.file.server.core.FileExtension;
import dev.luin.file.server.core.file.FSFile;
import dev.luin.file.server.core.file.FileSystem;
import dev.luin.file.server.core.service.model.User;
import lombok.val;
import lombok.extern.slf4j.Slf4j;

@Slf4j
class DownloadFileHandler extends BaseHandler
{
	public DownloadFileHandler(FileSystem fs)
	{
		super(fs);
	}

	@Override
	public void handle(final DownloadRequest request, final DownloadResponse response, User user) throws IOException
	{
		log.debug("HandleGetFile {}",user);
		val path = request.getPath();
		val extension = FileExtension.getExtension(path);
		val fsFile = getFs().findFile(user,extension.getPath(path)).getOrElseThrow(() -> DownloadException.fileNotFound(path));
		switch(extension)
		{
			case MD5:
				log.debug("GetMD5Checksum {}",fsFile);
				response.sendContent(extension.getContentType(),fsFile.getMd5Checksum());
				break;
			case SHA256:
				log.debug("GetSHA256Checksum {}",fsFile);
				response.sendContent(extension.getContentType(),fsFile.getSha256Checksum());
				break;
			default:
				log.info("Download {}",fsFile);
				handle(request,response,fsFile);
		}
	}

	private void handle(final DownloadRequest request, final DownloadResponse response, final FSFile fsFile) throws IOException
	{
		if (!fsFile.isCompleted())
			throw DownloadException.fileNotFound(fsFile.getVirtualPath());
		val ranges = request.getRanges(fsFile);
		response.sendFile(getFs(),fsFile,ranges);
	}
}
