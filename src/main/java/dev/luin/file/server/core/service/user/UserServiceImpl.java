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
package dev.luin.file.server.core.service.user;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import dev.luin.file.server.core.file.UserId;
import dev.luin.file.server.core.service.NotFoundException;
import dev.luin.file.server.core.service.ServiceException;
import io.vavr.control.Try;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@FieldDefaults(level=AccessLevel.PRIVATE, makeFinal=true)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@Path("users")
@Produces(MediaType.APPLICATION_JSON)
public class UserServiceImpl implements UserService
{
	@NonNull
	UserDAO userDAO;

	@GET
	@Path("{id}")
	@Override
	public UserInfo getUser(@PathParam("id") final long id) throws ServiceException
	{
		log.debug("getUser {}",id);
		return Try.of(() -> userDAO.findUser(new UserId(id)).map(UserInfo::new).getOrElseThrow(userNotFound()))
				.getOrElseThrow(ServiceException.defaultExceptionProvider);
	}

	private Supplier<NotFoundException> userNotFound()
	{
		return () -> new NotFoundException("User not found");
	}

	@GET
	@Override
	public List<UserInfo> getUsers() throws ServiceException
	{
		log.debug("getUsers");
		return Try.of(() -> userDAO.selectUsers().map(UserInfo::new).asJava())
				.getOrElseThrow(ServiceException.defaultExceptionProvider);
	}

	@POST
	@Override
	public long createUser(@NonNull final NewUser user) throws ServiceException
	{
		log.debug("createUser {}",user);
		return Try.of(() -> userDAO.insertUser(user.toUser()))
				.peek(logger("Created user {}"))
				.map(User::getId)
				.map(UserId::getValue)
				.getOrElseThrow(ServiceException.defaultExceptionProvider);
	}

	private static Consumer<Object> logger(String msg)
	{
		return o -> log.info(msg,o);
	}

	@PUT
	@Path("{id}")
	public void updateUserRest(@PathParam("id") final long id, @NonNull final NewUser user) throws ServiceException
	{
		updateUser(new UserInfo(id,user.getName(),user.getCertificate()));
	}

	@Override
	public void updateUser(@NonNull final UserInfo userInfo) throws ServiceException
	{
		log.debug("updateUser {}",userInfo);
		Try.success(userInfo)
				.map(UserInfo::toUser)
				.mapTry(userDAO::updateUser)
				.filter(n -> n > 0,userNotFound())
				.getOrElseThrow(ServiceException.defaultExceptionProvider);
		log.info("Updated user {}",userInfo);
	}

	@DELETE
	@Path("{id}")
	@Override
	public void deleteUser(@PathParam("id") final long id) throws ServiceException
	{
		log.debug("deleteUser {}",id);
		Try.of(() -> userDAO.deleteUser(new UserId(id)))
				.filter(n -> n > 0,userNotFound())
				.getOrElseThrow(ServiceException.defaultExceptionProvider);
		log.info("Deleted user {}",id);
	}
}
