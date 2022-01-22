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

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;
import static io.vavr.API.Some;
import static io.vavr.control.Try.failure;
import static io.vavr.control.Try.success;

import dev.luin.file.server.core.service.user.User;
import io.vavr.Function1;
import io.vavr.control.Try;
import lombok.Builder;
import lombok.NonNull;

public interface BaseHandler
{
	@Builder(builderMethodName = "getDownloadHandlerBuilder")
	public static Function1<DownloadRequest,Try<BaseHandler>> getDownloadHandler(@NonNull FileInfoHandler fileInfoHandler, @NonNull DownloadFileHandler downloadFileHandler)
	{
		return request -> Match(request.getMethod()).of(
				Case($(Some(DownloadMethod.FILE_INFO)),success(fileInfoHandler)),
				Case($(Some(DownloadMethod.DOWNLOAD_FILE)),success(downloadFileHandler)),
				Case($(),() -> failure(DownloadException.methodNotFound())));
	}

	public abstract Try<Function1<DownloadResponse,Try<Void>>> handle(DownloadRequest request, User user);
}
