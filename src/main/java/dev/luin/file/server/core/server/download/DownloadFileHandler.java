/*
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

import dev.luin.file.server.core.file.FileSystem;
import dev.luin.file.server.core.service.user.User;
import io.vavr.Function1;
import io.vavr.control.Try;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.val;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
class DownloadFileHandler implements BaseHandler
{
	@NonNull
	FileSystem fs;

	@Override
	public Try<Function1<DownloadResponse,Try<Void>>> handle(final DownloadRequest request, final User user)
	{
		log.debug("HandleGetFile {}",user);
		val path = request.getVirtualPathWithExtension();
		return FileHandler.create(fs,path,user)
				.flatMap(f -> f.handle(request));
	}

}
