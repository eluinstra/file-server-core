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

import java.util.function.Consumer;

import dev.luin.file.server.core.server.upload.header.TusExtension;
import dev.luin.file.server.core.server.upload.header.TusMaxSize;
import dev.luin.file.server.core.server.upload.header.TusResumable;
import dev.luin.file.server.core.server.upload.header.TusVersion;
import dev.luin.file.server.core.service.user.User;
import io.vavr.control.Either;
import io.vavr.control.Option;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
class TusOptionsHandler implements BaseHandler
{
	@NonNull
	Consumer<UploadResponse> sendResponse;

	public TusOptionsHandler(TusMaxSize tusMaxSize)
	{
		sendResponse = response -> Option.of(response)
				.peek(UploadResponse::setStatusNoContent)
				.peek(TusResumable::write)
				.peek(TusVersion::write)
				.peek(tusMaxSize::write)
				.peek(TusExtension::write);
	}

	@Override
	public Either<UploadException,Void> handle(@NonNull final UploadRequest request, @NonNull final UploadResponse response, @NonNull final User user)
	{
		log.debug("HandleGetTusOptions {}",user);
		return Either.<UploadException,Void>right(null)
				.peek(v -> sendResponse.accept(response));
	}
}
