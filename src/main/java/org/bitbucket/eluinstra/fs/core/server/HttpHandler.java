package org.bitbucket.eluinstra.fs.core.server;

import java.io.IOException;
import java.security.cert.CertificateEncodingException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bitbucket.eluinstra.fs.core.http.HttpException;
import org.bitbucket.eluinstra.fs.core.service.model.User;
import org.bitbucket.eluinstra.fs.core.user.UserManager;

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
		return userManager.findUser(clientCertificate).getOrElseThrow(() -> HttpException.notFound());
	}

	protected void sendError(final HttpServletResponse response, HttpException e) throws IOException
	{
		if (e.getMessage() == null)
			response.sendError(e.getStatusCode());
		else
			response.sendError(e.getStatusCode(),e.getMessage());
		e.getHeaders().forEach((k,v) -> response.setHeader(k,v));
	}
}
