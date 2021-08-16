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
package dev.luin.file.server.core.service.file;

import static dev.luin.file.server.core.service.ServiceException.defaultExceptionProvider;

import java.time.Instant;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javax.activation.DataHandler;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;

import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.apache.cxf.jaxrs.ext.multipart.Multipart;
import org.apache.cxf.jaxrs.ext.multipart.MultipartBody;

import dev.luin.file.server.core.file.FSFile;
import dev.luin.file.server.core.file.FileSystem;
import dev.luin.file.server.core.file.UserId;
import dev.luin.file.server.core.file.VirtualPath;
import dev.luin.file.server.core.service.NotFoundException;
import dev.luin.file.server.core.service.ServiceException;
import dev.luin.file.server.core.service.user.User;
import dev.luin.file.server.core.service.user.UserManager;
import io.vavr.control.Try;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.val;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@FieldDefaults(level=AccessLevel.PRIVATE, makeFinal=true)
@AllArgsConstructor
@Path("files")
@Produces(MediaType.APPLICATION_JSON)
public class FileServiceImpl implements FileService
{
	@NonNull
	UserManager userManager;
	@NonNull
	FileSystem fs;

	@POST
	@Path("user/{userId}")
	@Consumes("multipart/form-data")
	@Produces("text/plain")
	public String uploadFile(
		@PathParam("userId") final long userId,
		@Multipart("sha256Checksum") String sha256Checksum,
		@Multipart("startDate") Instant startDate,
		@Multipart("endDate") Instant endDate,
		@Multipart("file") @NonNull final Attachment file) throws ServiceException
	{
		return uploadFile(userId,NewFile.builder()
				.sha256Checksum(sha256Checksum)
				.startDate(startDate)
				.endDate(endDate)
				.content(file.getDataHandler())
				.build());
	}

	@Override
	public String uploadFile(@PathParam("userId") final long userId, @NonNull final NewFile file) throws ServiceException
	{
		log.debug("uploadFile file={},\nuserId={}",file,userId);
		return Try.of(() -> userManager.findUser(new UserId(userId)))
				.getOrElseThrow(defaultExceptionProvider)
				.toTry(() -> new NotFoundException("User not found!"))
				.flatMap(u -> createFile(file,u).toTry(ServiceException::new))
				.peek(logger("Uploaded file {}"))
				.getOrElseThrow(defaultExceptionProvider)
				.getVirtualPath().getValue();
	}

	private static Consumer<Object> logger(String msg)
	{
		return o -> log.info(msg,o);
	}

	@GET
	@Path("{path}")
	@Produces("multipart/mixed")
	public MultipartBody downloadFileRest(@PathParam("path") @NonNull final String path) throws ServiceException
	{
		return toMultipartBody(downloadFile(path));
	}

	public MultipartBody toMultipartBody(File file)
	{
		val attachments = new LinkedList<Attachment>();
		attachments.add(new Attachment("sha256Checksum", "text/plain", file.getSha256Checksum()));
		attachments.add(new Attachment("file", file.getContent(), new MultivaluedHashMap<>()));
		return new MultipartBody(attachments, true);  
	}

	@Override
	public File downloadFile(@NonNull final String path) throws ServiceException
	{
		log.debug("downloadFile {}",path);
		val fsFile = Try.of(() -> fs.findFile(new VirtualPath(path))).getOrElseThrow(defaultExceptionProvider);
		val dataSource = fsFile.map(FSFile::toDataSource);
		return fsFile.filter(FSFile::isCompleted)
				.peek(logger("Downloaded file {}"))
				.flatMap(f -> dataSource.map(d -> new File(f,new DataHandler(d))))
				.getOrElseThrow(() -> defaultExceptionProvider.apply(new NotFoundException("File not found!")));
	}

	@GET
	@Override
	public List<String> getFiles() throws ServiceException
	{
		log.debug("getFiles");
		return Try.of(() -> fs.getFiles())
				.getOrElseThrow(defaultExceptionProvider)
				.stream()
				.map(p -> p.getValue())
				.collect(Collectors.toList());
	}

	@GET
	@Path("{path}/info")
	@Override
	public FileInfo getFileInfo(@PathParam("path") @NonNull final String path) throws ServiceException
	{
		log.debug("getFileInfo {}",path);
		return Try.of(() -> fs.findFile(new VirtualPath(path)))
				.getOrElseThrow(defaultExceptionProvider)
				.toTry(() -> new NotFoundException("File not found!"))
				.map(FileInfo::new)
				.getOrElseThrow(defaultExceptionProvider);
	}

	@DELETE
	@Path("{path}?force={force}")
	@Override
	public Boolean deleteFile(@PathParam("path") @NonNull final String path, @QueryParam("force") final Boolean force) throws ServiceException
	{
		log.debug("deleteFile {}",path);
		return Try.of(() -> fs.findFile(new VirtualPath(path)))
				.getOrElseThrow(defaultExceptionProvider)
				.toTry(() -> new NotFoundException("File not found!"))
				.flatMap(f -> fs.deleteFile(force).apply(f)
						.peek(t -> logger("Deleted file {}").accept(f)))
				.getOrElseThrow(defaultExceptionProvider);
	}

	private Try<FSFile> createFile(final NewFile file, final User user)
	{
		return fs.createNewFile(NewFSFileImpl.of(file),user);
	}
}
