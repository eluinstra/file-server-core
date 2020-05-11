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

import java.io.IOException;
import java.security.cert.CertificateEncodingException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bitbucket.eluinstra.fs.core.file.FileSystem;
import org.bitbucket.eluinstra.fs.core.server.FSHttpException;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level=AccessLevel.PRIVATE, makeFinal=true)
@AllArgsConstructor
public class HttpHandler
{
	@NonNull
	FileSystem fs;

	public void handle(@NonNull final HttpServletRequest request, @NonNull final HttpServletResponse response) throws IOException
	{
		try
		{
			switch(request.getMethod())
			{
				case "OPTIONS":
					handleOPTIONS(request,response);
					break;
				default:
					throw new FSHttpException(404,"File not found!");
			}
		}
		catch (FSHttpException e)
		{
			e.getHeaders().forEach((k,v) -> response.setHeader(k,v));
			if (e.getMessage() == null) response.sendError(e.getStatusCode()); else response.sendError(e.getStatusCode(),e.getMessage());
		}
		catch (Exception e)
		{
			response.setStatus(500);;
		}
	}

	private void handleOPTIONS(final HttpServletRequest request, final HttpServletResponse response) throws CertificateEncodingException, IOException
	{
	}
}
