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

import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlElement;

import org.bitbucket.eluinstra.fs.core.service.model.Client;

@WebService(targetNamespace="http://bitbucket.org/eluinstra/fs/core/1.0.0")
public interface FSAdminService
{
	void createClient(@WebParam(name="client") @XmlElement(required=true) Client client) throws FSServiceException;
	@WebResult(name="client")
	Client getClient(@WebParam(name="id") long id) throws FSServiceException;
	void updateClient(@WebParam(name="client") @XmlElement(required=true) Client client) throws FSServiceException;
	void deleteClient(@WebParam(name="id") long id) throws FSServiceException;
	@WebResult(name="clients")
	List<Client> getClients() throws FSServiceException;
}
