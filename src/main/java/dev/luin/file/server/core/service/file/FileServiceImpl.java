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

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import javax.activation.DataHandler;

import dev.luin.file.server.core.file.FSFile;
import dev.luin.file.server.core.file.FileSystem;
import dev.luin.file.server.core.file.VirtualPath;
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
		log.debug("uploadFile userId={}, {}",userId,file);
		return Try.of(() -> 
				{
					val user = userManager.findUser(userId);
					return user.map(u -> Try.of(() -> createFile(file,u)))
							.peek(f -> log.info("Uploaded file {}",f))
							.getOrElseThrow(() -> new ServiceException("User not found!"))
							.get()
							.getVirtualPath().getValue();
				})
				.getOrElseThrow(ServiceException.defaultExceptionProvider);
	}

	@Override
	public File downloadFile(String path) throws ServiceException
	{
		log.debug("downloadFile {}",path);
		return Try.of(() -> 
				{
					val fsFile = fs.findFile(new VirtualPath(path));
					val dataSource = fsFile.map(f -> f.toDataSource());
					return fsFile.filter(f -> f.isCompleted())
							.peek(f -> log.info("Downloaded file {}",f))
							.flatMap(f -> dataSource.map(d -> FileMapper.INSTANCE.toFile(f,new DataHandler(d))))
							.getOrElseThrow(() -> new ServiceException("File not found!"));
				})
				.getOrElseThrow(ServiceException.defaultExceptionProvider);
	}

	@Override
	public List<String> getFiles() throws ServiceException
	{
		log.debug("getFiles");
		return Try.of(() -> fs.getFiles()).getOrElseThrow(ServiceException.defaultExceptionProvider)
				.stream()
				.map(p -> p.getValue())
				.collect(Collectors.toList());
	}

	@Override
	public FileInfo getFileInfo(String path) throws ServiceException
	{
		log.debug("getFileInfo {}",path);
		return Try.of(() -> 
				{
					val fsFile = fs.findFile(new VirtualPath(path));
					return fsFile.map(f -> FileInfoMapper.INSTANCE.toFileInfo(f))
							.getOrElseThrow(() -> new ServiceException("File not found!"));
				})
				.getOrElseThrow(ServiceException.defaultExceptionProvider);
	}

	@Override
	public void deleteFile(final String path, final Boolean force) throws ServiceException
	{
		log.debug("deleteFile {}",path);
		Try.of(() -> 
				{
					val fsFile = fs.findFile(new VirtualPath(path));
					val deleted = fsFile.map(f -> fs.deleteFile(fsFile.get(),force != null && force))
							.getOrElseThrow(() -> new ServiceException("File not found!"));
					if (deleted)
						log.info("Deleted file {}",fsFile);
					else
						throw new ServiceException("Unable to delete file!");
					return null;
				})
				.getOrElseThrow(ServiceException.defaultExceptionProvider);
	}

	private FSFile createFile(final NewFile file, final User user) throws IOException
	{
		return fs.createNewFile(NewFSFileImpl.of(file), user.getId());
	}
}
