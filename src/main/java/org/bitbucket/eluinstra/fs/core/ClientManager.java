package org.bitbucket.eluinstra.fs.core;

import java.util.Optional;

import org.bitbucket.eluinstra.fs.core.dao.ClientDAO;
import org.bitbucket.eluinstra.fs.core.service.model.Client;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
public class ClientManager
{
	@NonNull
	ClientDAO clientDAO;

	public Optional<Client> findClient(String name, @NonNull byte[] clientCertificate)
	{
		Optional<Client> client = clientDAO.findClient(name);
		return client.filter(c -> c.getCertificate().equals(clientCertificate));
	}
}
