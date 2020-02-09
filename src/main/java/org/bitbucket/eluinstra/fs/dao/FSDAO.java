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
package org.bitbucket.eluinstra.fs.dao;

import java.util.Optional;

import org.bitbucket.eluinstra.fs.model.Client;
import org.bitbucket.eluinstra.fs.model.FSFile;

public interface FSDAO
{
	boolean isAuthorized(byte[] certificate, long fileId);
	Optional<Client> findClient(long id);
	Optional<Client> findClient(String name);
	void insertClient(Client client);
	void deleteClient(long id);

	Optional<FSFile> findFile(long id);
	Optional<FSFile> findFile(String path);
	void insertFile(FSFile fSFile);
	void deleteFile(long id);
}
