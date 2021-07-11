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

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;
import static io.vavr.API.None;
import static io.vavr.API.Some;

import java.io.IOException;

import dev.luin.file.server.core.service.user.User;
import io.vavr.Function1;
import io.vavr.control.Either;
import lombok.Builder;
import lombok.NonNull;

public interface BaseHandler
{
	@Builder(builderMethodName = "getDownloadHandlerBuilder")
	public static Function1<DownloadRequest,Either<DownloadException,BaseHandler>> getDownloadHandler(@NonNull FileInfoHandler fileInfoHandler, @NonNull DownloadFileHandler downloadFileHandler)
	{
		return request -> Match(request.getMethod()).of(
				Case($(Some(DownloadMethod.FILE_INFO)),Either.right(fileInfoHandler)),
				Case($(Some(DownloadMethod.DOWNLOAD_FILE)),Either.right(downloadFileHandler)),
				Case($(None()),() -> Either.left(DownloadException.methodNotFound())));
	}

	public abstract Either<DownloadException,Function1<DownloadResponse,Either<IOException,Void>>> handle(DownloadRequest request, User user);
}
