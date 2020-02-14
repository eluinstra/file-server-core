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
package org.bitbucket.eluinstra.fs.core.service;

import java.util.List;

import org.bitbucket.eluinstra.fs.core.dao.ClientDAO;
import org.bitbucket.eluinstra.fs.core.service.model.Client;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RequiredArgsConstructor
@FieldDefaults(level=AccessLevel.PRIVATE, makeFinal=true)
public class FSAdminServiceImpl implements FSAdminService
{
	@NonNull
	ClientDAO clientDAO;

	@Override
	public Client getClient(@NonNull final String name)
	{
		return clientDAO.findClient(name).orElse(null);
	}

	@Override
	public List<Client> getClients()
	{
		return clientDAO.selectClients();
	}

	@Override
	public void createClient(@NonNull final Client client)
	{
		clientDAO.insertClient(client);
	}

	@Override
	public void updateClient(@NonNull final Client client)
	{
		clientDAO.updateClient(client);
	}

	@Override
	public void deleteClient(@NonNull final String name)
	{
		clientDAO.deleteClient(name);
	}

}
