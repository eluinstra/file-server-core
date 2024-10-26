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

import static dev.luin.file.server.core.service.ServiceException.defaultExceptionProvider;
import static io.vavr.API.For;

import dev.luin.file.server.core.file.UserId;
import dev.luin.file.server.core.server.servlet.Certificate;
import dev.luin.file.server.core.service.NotFoundException;
import dev.luin.file.server.core.service.ServiceException;
import io.vavr.control.Try;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
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
		log.debug("getUser {}", id);
		return Try.of(() -> userDAO.findUser(new UserId(id)).map(UserInfo::new).getOrElseThrow(userNotFound())).getOrElseThrow(defaultExceptionProvider);
	}

	private Supplier<NotFoundException> userNotFound()
	{
		return () -> new NotFoundException("User not found");
	}

	@GET
	@Path("")
	@Override
	public List<UserInfo> getUsers() throws ServiceException
	{
		log.debug("getUsers");
		return Try.of(() -> userDAO.selectUsers().map(UserInfo::new).asJava()).getOrElseThrow(defaultExceptionProvider);
	}

	@POST
	@Path("")
	@Override
	public long createUser(@NonNull final NewUser user) throws ServiceException
	{
		log.debug("createUser {}", user);
		return Try.of(() -> userDAO.insertUser(user.toUser()))
				.peek(logger("Created user {}"))
				.map(User::getId)
				.map(UserId::getValue)
				.getOrElseThrow(defaultExceptionProvider);
	}

	private static Consumer<Object> logger(String msg)
	{
		return o -> log.info(msg, o);
	}

	@PUT
	@Path("{id}")
	public void updateUserRest(@PathParam("id") final long id, @NonNull final NewUser user) throws ServiceException
	{
		updateUser(new UserInfo(id, user.getName()));
	}

	@Override
	public void updateUser(@NonNull final UserInfo userInfo) throws ServiceException
	{
		log.debug("updateUser {}", userInfo);
		Try.success(userInfo).map(UserInfo::toUser).mapTry(userDAO::updateUser).filter(n -> n > 0, userNotFound()).getOrElseThrow(defaultExceptionProvider);
		log.info("Updated user {}", userInfo);
	}

	@DELETE
	@Path("{id}")
	@Override
	public void deleteUser(@PathParam("id") final long id) throws ServiceException
	{
		log.debug("deleteUser {}", id);
		Try.of(() -> userDAO.deleteUser(new UserId(id))).filter(n -> n > 0, userNotFound()).getOrElseThrow(defaultExceptionProvider);
		log.info("Deleted user {}", id);
	}

	@GET
	@Path("{id}/certificates")
	@Override
	public List<byte[]> getCertificates(long userId) throws ServiceException
	{
		log.debug("getCertificates");
		return Try.of(() -> userDAO.selectCertificates(new UserId(userId)).map(Certificate::getEncoded).asJava()).getOrElseThrow(defaultExceptionProvider);
	}

	@PUT
	@Path("{id}")
	public void addCertificateRest(@PathParam("id") long userId, @NonNull byte[] certificate) throws ServiceException
	{
		addCertificate(new NewCertificate(userId, certificate));
	}

	@Override
	public void addCertificate(@NonNull NewCertificate certificate) throws ServiceException
	{
		log.debug("addCertificate {}", certificate);
		For(Try.success(certificate.getUserId()).mapTry(UserId::new), Try.success(certificate.getCertificate()).mapTry(Certificate::of))
				.yield(userDAO::insertCertificate)
				.get();
		log.info("Added certificate {}", certificate);
	}

	@DELETE
	@Path("{id}/certificate")
	@Consumes(MediaType.TEXT_PLAIN)
	@Override
	public void deleteCertificate(@NonNull byte[] certificate) throws ServiceException
	{
		log.debug("deleteCertificate {}", certificate);
		Try.of(() -> userDAO.deleteCertificate(Certificate.of(certificate))).filter(n -> n > 0, certificateNotFound()).getOrElseThrow(defaultExceptionProvider);
		log.info("Deleted certificate {}", certificate);
	}

	private Supplier<NotFoundException> certificateNotFound()
	{
		return () -> new NotFoundException("Certificate not found");
	}
}
