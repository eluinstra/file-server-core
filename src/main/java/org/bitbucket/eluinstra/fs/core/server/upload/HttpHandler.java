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
package org.bitbucket.eluinstra.fs.core.server.upload;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;

import java.io.IOException;
import java.security.cert.CertificateEncodingException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bitbucket.eluinstra.fs.core.ClientManager;
import org.bitbucket.eluinstra.fs.core.http.HttpException;
import org.bitbucket.eluinstra.fs.core.server.ClientCertificateManager;
import org.bitbucket.eluinstra.fs.core.service.model.Client;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.val;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@FieldDefaults(level=AccessLevel.PRIVATE, makeFinal=true)
@AllArgsConstructor
public class HttpHandler
{
	@NonNull
	ClientManager clientManager;
	HeadHandler headHandler;
	PostHandler postHandler;
	PatchHandler patchHandler;
	DeleteHandler deleteHandler;
	OptionsHandler optionsHandler;

	public void handle(@NonNull final HttpServletRequest request, @NonNull final HttpServletResponse response) throws IOException
	{
		try
		{
			val client = authenticate(request);
			val handler = getHandler(request);
			handler.handle(request,response,client);
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

	private Client authenticate(final HttpServletRequest request) throws CertificateEncodingException
	{
		val clientCertificate = ClientCertificateManager.getEncodedCertificate();
		return clientManager.findClient(clientCertificate).getOrElseThrow(() -> HttpException.notFound());
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

	private void sendError(final HttpServletResponse response, HttpException e) throws IOException
	{
		e.getHeaders().forEach((k,v) -> response.setHeader(k,v));
		if (e.getMessage() == null)
			response.sendError(e.getStatusCode());
		else
			response.sendError(e.getStatusCode(),e.getMessage());
	}
}
