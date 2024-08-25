/*
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
package dev.luin.file.server.core.service.user;

import dev.luin.file.server.core.service.ServiceException;
import jakarta.jws.WebParam;
import jakarta.jws.WebResult;
import jakarta.jws.WebService;
import jakarta.xml.bind.annotation.XmlElement;
import java.util.List;

@WebService(
		name = "UserService",
		targetNamespace = "http://luin.dev/file/server/2.0",
		serviceName = "UserService",
		endpointInterface = "UserServiceSoapBinding",
		portName = "UserServicePort")
public interface UserService
{
	@WebResult(name = "user")
	UserInfo getUser(@WebParam(name = "id") long id) throws ServiceException;

	@WebResult(name = "users")
	List<UserInfo> getUsers() throws ServiceException;

	@WebResult(name = "id")
	long createUser(@WebParam(name = "user") @XmlElement(required = true) NewUser user) throws ServiceException;

	void updateUser(@WebParam(name = "user") @XmlElement(required = true) UserInfo userInfo) throws ServiceException;

	void deleteUser(@WebParam(name = "id") long id) throws ServiceException;

	@WebResult(name = "certificates")
	List<byte[]> getCertificates(@WebParam(name = "userId") long userId) throws ServiceException;

	void addCertificate(@WebParam(name = "certificate") @XmlElement(required = true) NewCertificate certificate) throws ServiceException;

	void deleteCertificate(@WebParam(name = "certificate") @XmlElement(required = true) byte[] certificate) throws ServiceException;
}
