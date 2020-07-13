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

import org.bitbucket.eluinstra.fs.core.dao.UserDAO;
import org.bitbucket.eluinstra.fs.core.file.FileSystem;
import org.bitbucket.eluinstra.fs.core.service.model.File;
import org.bitbucket.eluinstra.fs.core.service.model.FileInfo;
import org.bitbucket.eluinstra.fs.core.service.model.FileInfoMapper;
import org.bitbucket.eluinstra.fs.core.service.model.FileMapper;
import org.bitbucket.eluinstra.fs.core.service.model.User;
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
public class FSServiceImpl implements FSService
{
	@NonNull
	UserDAO userDAO;
	@NonNull
	FileSystem fs;

	@Override
	public String uploadFile(@NonNull final File file, final long userId) throws FSServiceException
	{
		val user = userDAO.findUser(userId);
		return user.map(u -> Try.of(() -> createFile(file,u)))
			.get()
			.getOrElseThrow(() -> new FSServiceException("UserId " + userId + " not found!"));
	}

	@Override
	public File downloadFile(String path) throws FSServiceException
	{
		val fsFile = fs.findFile(path);
		DataSource dataSource = fs.createDataSource(fsFile.get());
		return fsFile.filter(f -> f.isCompletedFile()).map(f -> FileMapper.INSTANCE.toFile(f,new DataHandler(dataSource))).getOrElseThrow(() -> new FSServiceException("File " + path + " not found!"));
	}

	@Override
	public List<String> getFiles() throws FSServiceException
	{
		return fs.getFiles();
	}

	@Override
	public FileInfo getFileInfo(String path) throws FSServiceException
	{
		val fsFile = fs.findFile(path);
		return fsFile.map(f -> FileInfoMapper.INSTANCE.toFileInfo(f)).getOrNull();
	}

	@Override
	public void deleteFile(final String path, final Boolean force) throws FSServiceException
	{
		val fsFile = fs.findFile(path);
		if (!fsFile.map(f -> fs.deleteFile(fsFile.get(),force != null && force)).getOrElseThrow(() -> new FSServiceException(path + " not found!")))
			throw new FSServiceException("Unable to delete " + path + "!");
	}

	private String createFile(final File file, final User user) throws IOException
	{
		return fs.createFile(file.getName(),file.getContentType(),file.getSha256Checksum(),file.getStartDate(),file.getEndDate(),user.getId(),file.getContent().getInputStream())
			.getVirtualPath();
	}
}
