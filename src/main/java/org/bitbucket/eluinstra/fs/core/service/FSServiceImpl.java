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

import org.apache.commons.lang3.RandomStringUtils;
import org.bitbucket.eluinstra.fs.core.dao.ClientDAO;
import org.bitbucket.eluinstra.fs.core.file.FileSystem;
import org.bitbucket.eluinstra.fs.core.service.model.File;
import org.bitbucket.eluinstra.fs.core.service.model.FileInfo;
import org.bitbucket.eluinstra.fs.core.service.model.FileMapper;
import org.springframework.transaction.annotation.Transactional;

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
	ClientDAO clientDAO;
	@NonNull
	FileSystem fs;
	int urlLength;

	@Override
	public String uploadFile(@NonNull final File file, final long clientId) throws FSServiceException
	{
		try
		{
			val client = clientDAO.findClient(clientId);
			if (client.isPresent())
			{
				val virtualPath = generateUniqueURL();
				fs.createFile(virtualPath,file.getFilename(),file.getContentType(),file.getChecksum(),file.getStartDate(),file.getEndDate(),client.get().getId(),file.getContent().getInputStream());
				return virtualPath;
			}
			else
				throw new FSServiceException("ClientId " + clientId + " not found!");
		}
		catch (Exception e)
		{
			throw new FSServiceException(e);
		}
	}

	private String generateUniqueURL()
	{
		while (true)
		{
			val result = RandomStringUtils.randomAlphanumeric(urlLength);
			if (!fs.findFile(result).isPresent())
				return "/" + result.toString();
		}
	}

	@Override
	public FileInfo getFileInfo(String path) throws FSServiceException
	{
		val fsFile = fs.findFile(path);
		return fsFile.map(f -> FileMapper.ISTANCE.fsFileToFileInfo(f)).orElse(null);
	}

	@Override
	public void deleteFile(@NonNull final String path, final Boolean force) throws FSServiceException
	{
		try
		{
			val fsFile = fs.findFile(path);
			if (fsFile.isPresent())
			{
				if (!fs.deleteFile(fsFile.get(),force != null && force))
					throw new FSServiceException("Unable to delete " + path + "!");
			}
			else
				throw new FSServiceException(path + " not found!");
		}
		catch (Exception e)
		{
			throw new FSServiceException(e);
		}
	}

}
