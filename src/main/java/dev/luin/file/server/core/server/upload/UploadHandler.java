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

import java.io.IOException;

import dev.luin.file.server.core.service.model.User;
import dev.luin.file.server.core.user.AuthenticationManager;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NonNull;
import lombok.val;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@FieldDefaults(level=AccessLevel.PRIVATE, makeFinal=true)
public class UploadHandler
{
	@NonNull
	AuthenticationManager authenticationManager;
	@NonNull
	HeadHandler headHandler;
	@NonNull
	PostHandler postHandler;
	@NonNull
	PatchHandler patchHandler;
	@NonNull
	DeleteHandler deleteHandler;
	@NonNull
	OptionsHandler optionsHandler;

	@Builder(access = AccessLevel.PACKAGE)
	public UploadHandler(@NonNull AuthenticationManager authenticationManager, @NonNull HeadHandler headHandler, PostHandler postHandler, PatchHandler patchHandler, DeleteHandler deleteHandler, OptionsHandler optionsHandler)
	{
		this.authenticationManager = authenticationManager;
		this.headHandler = headHandler;
		this.postHandler = postHandler;
		this.patchHandler = patchHandler;
		this.deleteHandler = deleteHandler;
		this.optionsHandler = optionsHandler;
	}
	
	public void handle(@NonNull final UploadRequest request, @NonNull final UploadResponse response) throws UploadException, IOException
	{
		val user = authenticationManager.authenticate();
		log.info("User {}",user);
		handle(request,response,user);
	}

	public void handle(@NonNull final UploadRequest request, @NonNull final UploadResponse response, User user) throws UploadException, IOException
	{
		val handler = getHandler(request);
		handler.handle(request,response,user);
	}

	private BaseHandler getHandler(final UploadRequest request)
	{
		return Match(request.getRequestMethod()).of(
				Case($("HEAD"),headHandler),
				Case($("POST"),postHandler),
				Case($("PATCH"),patchHandler),
				Case($("DELETE"),deleteHandler),
				Case($("OPTIONS"),optionsHandler),
				Case($(),m -> {
					throw UploadException.methodNotAllowed(m);
				})
			);
	}
}
