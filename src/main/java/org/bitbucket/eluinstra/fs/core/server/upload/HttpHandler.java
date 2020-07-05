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
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bitbucket.eluinstra.fs.core.ClientManager;
import org.bitbucket.eluinstra.fs.core.server.ClientCertificateManager;
import org.bitbucket.eluinstra.fs.core.server.FSHttpException;
import org.bitbucket.eluinstra.fs.core.server.FSHttpException.FSMethodNotAllowedException;
import org.bitbucket.eluinstra.fs.core.server.FSHttpException.FSNotFoundException;
import org.bitbucket.eluinstra.fs.core.server.FSHttpException.FSUnauthorizedException;
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
	OptionsHandler optionsHandler;

	public void handle(@NonNull final HttpServletRequest request, @NonNull final HttpServletResponse response) throws IOException
	{
		try
		{
			val client = authenticate(request);
			val handler = getHandler(request);
			handler.handle(request,response,client);
		}
		catch (FSHttpException e)
		{
			log.error("",e);
			sendError(response,e);
		}
		catch (Exception e)
		{
			log.error("",e);
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}

	private Client authenticate(final HttpServletRequest request) throws CertificateEncodingException
	{
		val clientCertificate = ClientCertificateManager.getEncodedCertificate();
		val path = request.getPathInfo();
		val name = getClientName(path).orElseThrow(() -> new FSUnauthorizedException());
		val client = clientManager.findClient(name,clientCertificate).orElseThrow(() -> new FSNotFoundException());
		return client;
	}

	private Optional<String> getClientName(String path)
	{
		val result = path.replaceFirst("^/[^/]*/.*$","$1");
		return Optional.ofNullable(path.length() != result.length() ? result : null);
	}

	private BaseHandler getHandler(final HttpServletRequest request)
	{
		val handler = Match(request.getMethod()).of(
				Case($("HEAD"),headHandler),
				Case($("POST"),postHandler),
				Case($("PATCH"),patchHandler),
				Case($("OPTIONS"),optionsHandler),
				Case($(),o -> {
					throw new FSMethodNotAllowedException(request.getMethod());
				}));
		return handler;
	}

	private void sendError(final HttpServletResponse response, FSHttpException e) throws IOException
	{
		e.getHeaders().forEach((k,v) -> response.setHeader(k,v));
		if (e.getMessage() == null)
			response.sendError(e.getStatusCode());
		else
			response.sendError(e.getStatusCode(),e.getMessage());
	}
}
