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

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;

import dev.luin.file.server.core.service.user.AuthenticationManager;
import dev.luin.file.server.core.service.user.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NonNull;
import lombok.val;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Builder(access = AccessLevel.PACKAGE)
@FieldDefaults(level=AccessLevel.PRIVATE, makeFinal=true)
public class UploadHandler
{
	@NonNull
	AuthenticationManager authenticationManager;
	@NonNull
	TusOptionsHandler tusOptionsHandler;
	@NonNull
	FileInfoHandler fileInfoHandler;
	@NonNull
	CreateFileHandler createFileHandler;
	@NonNull
	UploadFileHandler uploadFileHandler;
	@NonNull
	DeleteFileHandler deleteFileHandler;

	public void handle(@NonNull final UploadRequest request, @NonNull final UploadResponse response)
	{
		val user = authenticationManager.authenticate(request.getClientCertificate());
		log.info("User {}",user);
		handle(request,response,user);
	}

	private void handle(@NonNull final UploadRequest request, @NonNull final UploadResponse response, User user)
	{
		val handler = getHandler(request);
		handler.handle(request,response,user);
	}

	private BaseHandler getHandler(final UploadRequest request)
	{
		return Match(request.getMethod()).of(
				Case($(UploadMethod.TUS_OPTIONS),tusOptionsHandler),
				Case($(UploadMethod.FILE_INFO),fileInfoHandler),
				Case($(UploadMethod.CREATE_FILE),createFileHandler),
				Case($(UploadMethod.UPLOAD_FILE),uploadFileHandler),
				Case($(UploadMethod.DELETE_FILE),deleteFileHandler),
				Case($(),m -> {
					throw UploadException.methodNotAllowed(m);
				})
			);
	}
}
