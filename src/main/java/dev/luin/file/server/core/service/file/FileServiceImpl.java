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

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javax.activation.DataHandler;

import dev.luin.file.server.core.file.FSFile;
import dev.luin.file.server.core.file.FileSystem;
import dev.luin.file.server.core.file.UserId;
import dev.luin.file.server.core.file.VirtualPath;
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
class FileServiceImpl implements FileService
{
	@NonNull
	UserManager userManager;
	@NonNull
	FileSystem fs;

	@Override
	public String uploadFile(final long userId, @NonNull final NewFile file) throws ServiceException
	{
		log.debug("uploadFile file={},\nuserId={}",file,userId);
		return Try.of(() -> userManager.findUser(new UserId(userId)))
				.getOrElseThrow(ServiceException.defaultExceptionProvider)
				.toTry(() -> new ServiceException("User not found!"))
				.flatMap(u -> createFile(file,u).toTry(ServiceException::new))
				.peek(logger("Uploaded file {}"))
				.getOrElseThrow(ServiceException.defaultExceptionProvider)
				.getVirtualPath().getValue();
	}

	private static Consumer<Object> logger(String msg)
	{
		return o -> log.info(msg,o);
	}

	@Override
	public File downloadFile(@NonNull final String path) throws ServiceException
	{
		log.debug("downloadFile {}",path);
		val fsFile = Try.of(() -> fs.findFile(new VirtualPath(path))).getOrElseThrow(ServiceException.defaultExceptionProvider);
		val dataSource = fsFile.map(FSFile::toDataSource);
		return fsFile.filter(FSFile::isCompleted)
				.peek(logger("Downloaded file {}"))
				.flatMap(f -> dataSource.map(d -> new File(f,new DataHandler(d))))
				.getOrElseThrow(() -> new ServiceException("File not found!"));
	}

	@Override
	public List<String> getFiles() throws ServiceException
	{
		log.debug("getFiles");
		return Try.of(() -> fs.getFiles())
				.getOrElseThrow(ServiceException.defaultExceptionProvider)
				.stream()
				.map(p -> p.getValue())
				.collect(Collectors.toList());
	}

	@Override
	public FileInfo getFileInfo(@NonNull final String path) throws ServiceException
	{
		log.debug("getFileInfo {}",path);
		return Try.of(() -> fs.findFile(new VirtualPath(path)))
				.getOrElseThrow(ServiceException.defaultExceptionProvider)
				.toTry(() -> new ServiceException("File not found!"))
				.map(FileInfo::new)
				.getOrElseThrow(ServiceException.defaultExceptionProvider);
	}

	@Override
	public Boolean deleteFile(@NonNull final String path, final Boolean force) throws ServiceException
	{
		log.debug("deleteFile {}",path);
		return Try.of(() -> fs.findFile(new VirtualPath(path)))
				.getOrElseThrow(ServiceException.defaultExceptionProvider)
				.toTry(() -> new ServiceException("File not found!"))
				.flatMap(f -> fs.deleteFile().apply(force,f)
						.peek(t -> logger("Deleted file {}").accept(f)))
				.getOrElseThrow(ServiceException.defaultExceptionProvider);
	}

	private Try<FSFile> createFile(final NewFile file, final User user)
	{
		return fs.createNewFile(NewFSFileImpl.of(file),user);
	}
}
