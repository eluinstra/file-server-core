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

import java.io.IOException;
import java.util.List;

import javax.activation.DataHandler;

import dev.luin.file.server.core.file.FileSystem;
import dev.luin.file.server.core.service.model.File;
import dev.luin.file.server.core.service.model.FileInfo;
import dev.luin.file.server.core.service.model.FileInfoMapper;
import dev.luin.file.server.core.service.model.FileMapper;
import dev.luin.file.server.core.service.model.User;
import dev.luin.file.server.core.user.UserManager;
import io.vavr.control.Try;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.val;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level=AccessLevel.PRIVATE, makeFinal=true)
@AllArgsConstructor
class FileServiceImpl implements FileService
{
	@NonNull
	UserManager userManager;
	@NonNull
	FileSystem fs;

	@Override
	public String uploadFile(@NonNull final File file, final long userId) throws ServiceException
	{
		return Try.of(() -> 
				{
					val user = userManager.findUser(userId);
					return user.map(u -> Try.of(() -> createFile(file,u)))
						.getOrElseThrow(() -> new ServiceException("UserId " + userId + " not found!"))
						.get();
				})
				.getOrElseThrow(ServiceException.defaultExceptionProvider);
	}

	@Override
	public File downloadFile(String path) throws ServiceException
	{
		return Try.of(() -> 
				{
					val fsFile = fs.findFile(path);
					val dataSource = fsFile.map(f -> fs.createDataSource(f));
					return fsFile.filter(f -> f.isCompleted())
							.flatMap(f -> 
									dataSource.map(d -> FileMapper.INSTANCE.toFile(f,new DataHandler(d))))
							.getOrElseThrow(() -> new ServiceException("File " + path + " not found!"));
				})
				.getOrElseThrow(ServiceException.defaultExceptionProvider);
	}

	@Override
	public List<String> getFiles() throws ServiceException
	{
		return Try.of(() -> fs.getFiles()).getOrElseThrow(ServiceException.defaultExceptionProvider);
	}

	@Override
	public FileInfo getFileInfo(String path) throws ServiceException
	{
		return Try.of(() -> 
				{
					val fsFile = fs.findFile(path);
					return fsFile.map(f -> FileInfoMapper.INSTANCE.toFileInfo(f))
							.getOrElseThrow(() -> new ServiceException("File " + path + " not found!"));
				})
				.getOrElseThrow(ServiceException.defaultExceptionProvider);
	}

	@Override
	public void deleteFile(final String path, final Boolean force) throws ServiceException
	{
		Try.of(() -> 
				{
					val fsFile = fs.findFile(path);
					val deleted = fsFile.map(f -> fs.deleteFile(fsFile.get(),force != null && force))
							.getOrElseThrow(() -> new ServiceException("File " + path + " not found!"));
					if (!deleted)
						throw new ServiceException("Unable to delete " + path + "!");
					return null;
				})
				.getOrElseThrow(ServiceException.defaultExceptionProvider);
	}

	private String createFile(final File file, final User user) throws IOException
	{
		return fs.createFile(file.getName(),file.getContentType(),file.getSha256Checksum(),file.getStartDate(),file.getEndDate(),user.getId(),file.getContent().getInputStream())
			.getVirtualPath();
	}
}
