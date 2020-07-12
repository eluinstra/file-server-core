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
package org.bitbucket.eluinstra.fs.core;

import org.bitbucket.eluinstra.fs.core.dao.ClientDAO;
import org.bitbucket.eluinstra.fs.core.service.model.Client;

import io.vavr.control.Option;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.val;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
public class ClientManager
{
	@NonNull
	ClientDAO clientDAO;

	public Option<Client> findClient(@NonNull byte[] clientCertificate)
	{
		val clients = clientDAO.selectClients();
		return clients.find(c -> c.getCertificate().equals(clientCertificate));
	}

	public Option<Client> findClient(String name, @NonNull byte[] clientCertificate)
	{
		val client = clientDAO.findClient(name);
		return client.filter(c -> c.getCertificate().equals(clientCertificate));
	}
}
