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
package dev.luin.file.server.core.service;

import java.util.List;

import dev.luin.file.server.core.service.model.User;
import dev.luin.file.server.core.user.UserManager;
import io.vavr.control.Try;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level=AccessLevel.PRIVATE, makeFinal=true)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
class UserServiceImpl implements UserService
{
	@NonNull
	UserManager userManager;

	@Override
	public User getUser(final long id) throws ServiceException
	{
		return Try.of(() -> userManager.findUser(id).getOrNull()).<ServiceException>getOrElseThrow(ServiceException.defaultExceptionProvider);
	}

	@Override
	public List<User> getUsers() throws ServiceException
	{
		return Try.of(() -> userManager.selectUsers().asJava()).<ServiceException>getOrElseThrow(ServiceException.defaultExceptionProvider);
	}

	@Override
	public long createUser(@NonNull final User user) throws ServiceException
	{
		return Try.of(() -> userManager.insertUser(user)).map(u -> u.getId()).<ServiceException>getOrElseThrow(ServiceException.defaultExceptionProvider);
	}

	@Override
	public void updateUser(@NonNull final User user) throws ServiceException
	{
		Try.of(() -> userManager.updateUser(user)).<ServiceException>getOrElseThrow(ServiceException.defaultExceptionProvider);
	}

	@Override
	public void deleteUser(final long id) throws ServiceException
	{
		Try.of(() -> userManager.deleteUser(id)).<ServiceException>getOrElseThrow(ServiceException.defaultExceptionProvider);
	}
}