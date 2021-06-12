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

import dev.luin.file.server.core.service.user.AuthenticationManager;
import dev.luin.file.server.core.service.user.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NonNull;
import lombok.val;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@FieldDefaults(level=AccessLevel.PRIVATE, makeFinal=true)
public class DownloadHandler
{
	@NonNull
	AuthenticationManager authenticationManager;
	@NonNull
	FileInfoHandler fileInfoHandler;
	@NonNull
	DownloadFileHandler downloadFileHandler;

	@Builder
	public DownloadHandler(@NonNull AuthenticationManager authenticationManager, @NonNull FileInfoHandler fileInfoHandler, @NonNull DownloadFileHandler downloadFileHandler)
	{
		this.authenticationManager = authenticationManager;
		this.fileInfoHandler = fileInfoHandler;
		this.downloadFileHandler = downloadFileHandler;
	}

	public void handle(@NonNull final DownloadRequest request, @NonNull final DownloadResponse response)
	{
		val user = authenticationManager.authenticate(request.getClientCertificate());
		log.info("User {}",user);
		handle(request,response,user);
	}

	private void handle(final DownloadRequest request, final DownloadResponse response, final User user)
	{
		val handler = getHandler(request);
		handler.handle(request,response,user);
	}

	private BaseHandler getHandler(final DownloadRequest request)
	{
		return Match(request.getMethod()).of(
				Case($(Some(DownloadMethod.FILE_INFO)),fileInfoHandler),
				Case($(Some(DownloadMethod.DOWNLOAD_FILE)),downloadFileHandler),
				Case($(None()),() -> {
					throw DownloadException.methodNotFound();
				}),
				Case($(),m -> {
					throw DownloadException.methodNotAllowed(m.get());
				}));
	}
}
