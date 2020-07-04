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

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bitbucket.eluinstra.fs.core.server.ClientCertificateManager;
import org.bitbucket.eluinstra.fs.core.server.FSHttpException;

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
			switch(request.getMethod())
			{
				case "GET":
					getHandler.handle(request,response,clientCertificate);
					break;
				case "HEAD":
					headHandler.handle(request,response,clientCertificate);
					break;
				default:
					throw new FSHttpException(404,"File not found!");
			}
		}
		catch (FSHttpException e)
		{
			e.getHeaders().forEach((k,v) -> response.setHeader(k,v));
			if (e.getMessage() == null) response.sendError(e.getStatusCode()); else response.sendError(e.getStatusCode(),e.getMessage());
			//response.setStatus(e.getStatusCode());
			//if (e.getMessage() != null) response.getWriter().print(e.getMessage());
		}
		catch (Exception e)
		{
			response.sendError(500);
			//response.setStatus(500);
		}
	}
}
