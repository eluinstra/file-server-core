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

import org.bitbucket.eluinstra.fs.core.dao.UserDAO;
import org.bitbucket.eluinstra.fs.core.service.model.User;

import io.vavr.control.Option;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.val;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
public class UserManager
{
	@NonNull
	UserDAO userDAO;

	public Option<User> findUser(@NonNull byte[] certificate)
	{
		val users = userDAO.selectUsers();
		return users.find(c -> c.getCertificate().equals(certificate));
	}

	public Option<User> findUser(String name, @NonNull byte[] certificate)
	{
		val user = userDAO.findUser(name);
		return user.filter(c -> c.getCertificate().equals(certificate));
	}
}
