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

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dev.luin.file.server.core.http.HttpException;
import dev.luin.file.server.core.server.BaseHandler;
import dev.luin.file.server.core.user.UserManager;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NonNull;
import lombok.val;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@FieldDefaults(level=AccessLevel.PRIVATE, makeFinal=true)
public class HttpHandler extends dev.luin.file.server.core.server.HttpHandler
{
	@NonNull
	HeadHandler headHandler;
	@NonNull
	GetHandler getHandler;

	@Builder
	public HttpHandler(@NonNull UserManager userManager, @NonNull HeadHandler headHandler, @NonNull GetHandler getHandler)
	{
		super(userManager);
		this.headHandler = headHandler;
		this.getHandler = getHandler;
	}

	public void handle(@NonNull final HttpServletRequest request, @NonNull final HttpServletResponse response) throws IOException
	{
		try
		{
			val user = authenticate(request);
			log.info("User {}",user);
			val handler = getHandler(request);
			handler.handle(request,response,user);
		}
		catch (HttpException e)
		{
			log.error("",e);
			sendError(response,e);
		}
		catch (FileNotFoundException e)
		{
			log.error("",e);
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
		}
		catch (Exception e)
		{
			log.error("",e);
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}

	private BaseHandler getHandler(final HttpServletRequest request)
	{
		val handler = Match(request.getMethod()).of(
				Case($("GET"),getHandler),
				Case($("HEAD"),headHandler),
				Case($(),m -> {
					throw HttpException.methodNotAllowedException(m);
				}));
		return handler;
	}
}
