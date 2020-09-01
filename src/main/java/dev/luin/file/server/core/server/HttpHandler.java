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
package dev.luin.file.server.core.server;

import java.io.IOException;
import java.security.cert.CertificateEncodingException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dev.luin.file.server.core.http.HttpException;
import dev.luin.file.server.core.service.model.User;
import dev.luin.file.server.core.user.UserManager;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.val;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
public abstract class HttpHandler
{
	@NonNull
	UserManager userManager;

	public abstract void handle(@NonNull final HttpServletRequest request, @NonNull final HttpServletResponse response) throws IOException;

	protected User authenticate(final HttpServletRequest request) throws CertificateEncodingException
	{
		val clientCertificate = ClientCertificateManager.getEncodedCertificate();
		return userManager.findUser(clientCertificate).getOrElseThrow(() -> HttpException.unauthorizedException());
	}

	protected void sendError(final HttpServletResponse response, HttpException e) throws IOException
	{
		response.setStatus(e.getStatusCode());
		e.getHeaders().forEach((k,v) -> response.setHeader(k,v));
	}
}
