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
import dev.luin.file.server.core.server.upload.header.ContentLength;
import dev.luin.file.server.core.server.upload.header.TusResumable;
import dev.luin.file.server.core.service.model.User;
import io.vavr.control.Option;
import lombok.val;
import lombok.extern.slf4j.Slf4j;

@Slf4j
class DeleteHandler extends BaseHandler
{
	public DeleteHandler(FileSystem fs)
	{
		super(fs);
	}

	@Override
	public void handle(final UploadRequest request, final UploadResponse response, User user)
	{
		log.debug("HandleDelete {}",user);
		handleRequest(request,user);
		sendResponse(response);
	}

	private FSFile handleRequest(final UploadRequest request, User user)
	{
		TusResumable.of(request);
		getContentLength(request);
		val path = request.getPath();
		val file = getFs().findFile(user,path).getOrElseThrow(() -> HttpException.notFound(path));
		getFs().deleteFile(file,false);
		log.info("Deleted file {}",file);
		return file;
	}

	private Option<ContentLength> getContentLength(final UploadRequest request)
	{
		val result = ContentLength.of(request);
		if (result.isDefined())
			result.filter(l -> l.getValue() == 0).getOrElseThrow(() -> HttpException.invalidHeader(ContentLength.HEADER_NAME));
		return result;
	}

	private void sendResponse(final UploadResponse response)
	{
		response.setStatus(UploadResponseStatus.NO_CONTENT);
		TusResumable.get().write(response);
	}
}
