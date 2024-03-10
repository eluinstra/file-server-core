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

import static io.vavr.control.Try.success;

import dev.luin.file.server.core.file.FileSystem;
import dev.luin.file.server.core.service.user.User;
import io.vavr.Function1;
import io.vavr.control.Try;
import lombok.NonNull;
import lombok.val;

public interface FileHandler
{
	static Try<FileHandler> create(@NonNull final FileSystem fs, @NonNull final VirtualPathWithExtension virtualPath, @NonNull final User user)
	{
		return fs.findFile(user, virtualPath.getValue()).toTry(() -> DownloadException.fileNotFound(virtualPath.getValue().getValue())).flatMap(fsFile ->
		{
			val extension = virtualPath.getExtension();
			switch (extension)
			{
				case MD5:
					return success(new Md5FileHandler(fsFile));
				case SHA256:
					return success(new Sha256FileHandler(fsFile));
				default:
					return success(new FileHandlerImpl(fsFile));
			}
		});
	}

	Try<Function1<DownloadResponse, Try<Void>>> handle(DownloadRequest request);
}
