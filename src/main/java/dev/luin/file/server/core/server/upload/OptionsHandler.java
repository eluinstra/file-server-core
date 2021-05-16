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

import dev.luin.file.server.core.file.FileSystem;
import dev.luin.file.server.core.server.upload.header.TusExtension;
import dev.luin.file.server.core.server.upload.header.TusMaxSize;
import dev.luin.file.server.core.server.upload.header.TusResumable;
import dev.luin.file.server.core.server.upload.header.TusVersion;
import dev.luin.file.server.core.service.model.User;
import lombok.extern.slf4j.Slf4j;

@Slf4j
class OptionsHandler extends BaseHandler
{
	public OptionsHandler(FileSystem fs)
	{
		super(fs);
	}

	@Override
	public void handle(final UploadRequest request, final UploadResponse response, User user) throws IOException
	{
		log.debug("HandleOptions {}",user);
		sendResponse(response);
	}

	private void sendResponse(final UploadResponse response)
	{
		response.setStatus(UploadResponseStatus.NO_CONTENT);
		TusResumable.get().write(response);
		TusVersion.get().write(response);
		TusMaxSize.get().forEach(h -> h.write(response));
		TusExtension.get().write(response);
	}
}
