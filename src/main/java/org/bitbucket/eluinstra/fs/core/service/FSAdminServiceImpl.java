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

import org.bitbucket.eluinstra.fs.core.dao.UserDAO;
import org.bitbucket.eluinstra.fs.core.service.model.User;
import org.springframework.transaction.annotation.Transactional;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level=AccessLevel.PRIVATE, makeFinal=true)
@AllArgsConstructor
@Transactional(transactionManager = "dataSourceTransactionManager")
public class FSAdminServiceImpl implements FSAdminService
{
	@NonNull
	UserDAO userDAO;

	@Override
	public User getUser(final long id) throws FSServiceException
	{
		return userDAO.findUser(id).getOrNull();
	}

	@Override
	public List<User> getUsers() throws FSServiceException
	{
		return userDAO.selectUsers().asJava();
	}

	@Override
	public long createUser(@NonNull final User user) throws FSServiceException
	{
		return userDAO.insertUser(user);
	}

	@Override
	public void updateUser(@NonNull final User user) throws FSServiceException
	{
		userDAO.updateUser(user);
	}

	@Override
	public void deleteUser(final long id) throws FSServiceException
	{
		userDAO.deleteUser(id);
	}
}
