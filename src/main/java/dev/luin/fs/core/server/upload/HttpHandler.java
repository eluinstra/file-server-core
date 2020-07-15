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
package dev.luin.fs.core.server.upload;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dev.luin.fs.core.http.HttpException;
import dev.luin.fs.core.server.BaseHandler;
import dev.luin.fs.core.server.upload.header.TusResumable;
import dev.luin.fs.core.user.UserManager;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NonNull;
import lombok.val;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@FieldDefaults(level=AccessLevel.PRIVATE, makeFinal=true)
public class HttpHandler extends dev.luin.fs.core.server.HttpHandler
{
	@NonNull
	HeadHandler headHandler;
	PostHandler postHandler;
	PatchHandler patchHandler;
	DeleteHandler deleteHandler;
	OptionsHandler optionsHandler;

	@Builder(access = AccessLevel.PACKAGE)
	public HttpHandler(@NonNull UserManager userManager, @NonNull HeadHandler headHandler, PostHandler postHandler, PatchHandler patchHandler, DeleteHandler deleteHandler, OptionsHandler optionsHandler)
	{
		super(userManager);
		this.headHandler = headHandler;
		this.postHandler = postHandler;
		this.patchHandler = patchHandler;
		this.deleteHandler = deleteHandler;
		this.optionsHandler = optionsHandler;
	}
	
	public void handle(@NonNull final HttpServletRequest request, @NonNull final HttpServletResponse response) throws IOException
	{
		try
		{
			val user = authenticate(request);
			val handler = getHandler(request);
			handler.handle(request,response,user);
		}
		catch (HttpException e)
		{
			log.error("",e);
			sendError(response,e);
		}
		catch (Exception e)
		{
			log.error("",e);
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}

	private BaseHandler getHandler(final HttpServletRequest request)
	{
		return Match(request.getMethod()).of(
				Case($("HEAD"),headHandler),
				Case($("POST"),postHandler),
				Case($("PATCH"),patchHandler),
				Case($("DELETE"),deleteHandler),
				Case($("OPTIONS"),optionsHandler),
				Case($(),o -> {
					throw HttpException.methodNotAllowedException(request.getMethod());
				}));
	}

	protected void sendError(final HttpServletResponse response, HttpException e) throws IOException
	{
		e.getHeaders().put(TusResumable.get().asTuple());
		super.sendError(response,e);
	}
}
