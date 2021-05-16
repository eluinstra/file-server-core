package dev.luin.file.server.core.user;

import java.security.cert.CertificateEncodingException;

import dev.luin.file.server.core.server.ClientCertificateManager;
import dev.luin.file.server.core.service.model.User;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.val;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
public class AuthenticationManager
{
	@NonNull
	UserDAO userDAO;

	public User authenticate()
	{
		try
		{
			val clientCertificate = ClientCertificateManager.getEncodedCertificate();
			return userDAO.findUser(clientCertificate).getOrElseThrow(() -> UserManagerException.unauthorizedException());
		}
		catch (CertificateEncodingException e)
		{
			throw new UserManagerException(e);
		}
	}

}
