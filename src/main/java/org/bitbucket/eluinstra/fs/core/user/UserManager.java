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
package org.bitbucket.eluinstra.fs.core.user;

import org.bitbucket.eluinstra.fs.core.service.model.User;
import org.springframework.transaction.annotation.Transactional;

import io.vavr.collection.Seq;
import io.vavr.control.Option;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
public class UserManager
{
	@NonNull
	UserDAO userDAO;

	public Option<User> findUser(long userId)
	{
		return userDAO.findUser(userId);
	}

	@Transactional(transactionManager = "dataSourceTransactionManager")
	public Option<User> findUser(@NonNull byte[] certificate)
	{
		return userDAO.findUser(certificate);
	}

	public Seq<User> selectUsers()
	{
		return userDAO.selectUsers();
	}

	public long insertUser(@NonNull User user)
	{
		return userDAO.insertUser(user);
	}

	public long updateUser(@NonNull User user)
	{
		return userDAO.updateUser(user);
	}

	public long deleteUser(long id)
	{
		return userDAO.deleteUser(id);
	}
}
