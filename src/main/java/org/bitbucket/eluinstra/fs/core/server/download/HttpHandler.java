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
package org.bitbucket.eluinstra.fs.core.server.download;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bitbucket.eluinstra.fs.core.http.HttpException;
import org.bitbucket.eluinstra.fs.core.server.ClientCertificateManager;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NonNull;
import lombok.val;
import lombok.experimental.FieldDefaults;

@Builder
@FieldDefaults(level=AccessLevel.PRIVATE, makeFinal=true)
@AllArgsConstructor
public class HttpHandler
{
	@NonNull
	HeadHandler headHandler;
	@NonNull
	GetHandler getHandler;

	public void handle(@NonNull final HttpServletRequest request, @NonNull final HttpServletResponse response) throws IOException
	{
		try
		{
			val clientCertificate = ClientCertificateManager.getEncodedCertificate();
			val handler = Match(request.getMethod()).of(
					Case($("GET"),getHandler),
					Case($("HEAD"),headHandler),
					Case($(),o -> {
						throw HttpException.notFound();
					}));
			handler.handle(request,response,clientCertificate);
		}
		catch (HttpException e)
		{
			sendError(response,e);
		}
		catch (Exception e)
		{
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			//response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}

	private void sendError(final HttpServletResponse response, HttpException e) throws IOException
	{
		e.getHeaders().forEach((k,v) -> response.setHeader(k,v));
		if (e.getMessage() == null)
			response.sendError(e.getStatusCode());
		else
			response.sendError(e.getStatusCode(),e.getMessage());
		//response.setStatus(e.getStatusCode());
		//if (e.getMessage() != null) response.getWriter().print(e.getMessage());
	}
}
