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

import java.io.IOException;
import java.util.List;

import javax.activation.DataHandler;
import javax.activation.DataSource;

import org.bitbucket.eluinstra.fs.core.file.FileSystem;
import org.bitbucket.eluinstra.fs.core.service.model.File;
import org.bitbucket.eluinstra.fs.core.service.model.FileInfo;
import org.bitbucket.eluinstra.fs.core.service.model.FileInfoMapper;
import org.bitbucket.eluinstra.fs.core.service.model.FileMapper;
import org.bitbucket.eluinstra.fs.core.service.model.User;
import org.bitbucket.eluinstra.fs.core.user.UserManager;
import org.springframework.transaction.annotation.Transactional;

import io.vavr.control.Try;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.val;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level=AccessLevel.PRIVATE, makeFinal=true)
@AllArgsConstructor
@Transactional(transactionManager = "dataSourceTransactionManager")
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
						.get()
						.getOrElseThrow(() -> new ServiceException("UserId " + userId + " not found!"));
				})
				.getOrElseThrow(ServiceException.exceptionProvider);
	}

	@Override
	public File downloadFile(String path) throws ServiceException
	{
		return Try.of(() -> 
				{
					val fsFile = fs.findFile(path);
					DataSource dataSource = fs.createDataSource(fsFile.get());
					return fsFile.filter(f -> f.isCompleted())
							.map(f -> FileMapper.INSTANCE.toFile(f,new DataHandler(dataSource)))
							.getOrElseThrow(() -> new ServiceException("File " + path + " not found!"));
				})
				.getOrElseThrow(ServiceException.exceptionProvider);
	}

	@Override
	public List<String> getFiles() throws ServiceException
	{
		return Try.of(() -> fs.getFiles()).getOrElseThrow(ServiceException.exceptionProvider);
	}

	@Override
	public FileInfo getFileInfo(String path) throws ServiceException
	{
		return Try.of(() -> 
				{
					val fsFile = fs.findFile(path);
					return fsFile.map(f -> FileInfoMapper.INSTANCE.toFileInfo(f)).getOrNull();
				})
				.getOrElseThrow(ServiceException.exceptionProvider);
	}

	@Override
	public void deleteFile(final String path, final Boolean force) throws ServiceException
	{
		Try.of(() -> 
				{
					val fsFile = fs.findFile(path);
					if (!fsFile.map(f -> fs.deleteFile(fsFile.get(),force != null && force)).getOrElseThrow(() -> new ServiceException(path + " not found!")))
						throw new ServiceException("Unable to delete " + path + "!");
					return null;
				})
				.getOrElseThrow(ServiceException.exceptionProvider);
	}

	private String createFile(final File file, final User user) throws IOException
	{
		return fs.createFile(file.getName(),file.getContentType(),file.getSha256Checksum(),file.getStartDate(),file.getEndDate(),user.getId(),file.getContent().getInputStream())
			.getVirtualPath();
	}
}
