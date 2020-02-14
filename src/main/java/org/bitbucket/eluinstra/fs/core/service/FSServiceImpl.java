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

import org.bitbucket.eluinstra.fs.core.dao.ClientDAO;
import org.bitbucket.eluinstra.fs.core.file.FSFile;
import org.bitbucket.eluinstra.fs.core.file.FileSystem;
import org.bitbucket.eluinstra.fs.core.file.Period;
import org.bitbucket.eluinstra.fs.core.service.model.File;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;
import lombok.experimental.FieldDefaults;

@RequiredArgsConstructor
@FieldDefaults(level=AccessLevel.PRIVATE, makeFinal=true)
public class FSServiceImpl implements FSService
{
	@NonNull
	ClientDAO clientDAO;
	@NonNull
	FileSystem fs;

	@Override
	public FSFile uploadFile(@NonNull final String clientName, @NonNull final File file) throws FSServiceException
	{
		try
		{
			val client = clientDAO.findClient(clientName);
			if (client.isPresent())
			{
				val period = new Period(file.getStartDate(),file.getEndDate());
				return fs.createFile(file.getPath(),file.getContentType(),file.getChecksum(),period,client.get().getId(),file.getFile().getInputStream());
			}
			else
				throw new FSServiceException("client " + clientName + " not found!");
		}
		catch (Exception e)
		{
			throw new FSServiceException(e);
		}
	}

	@Override
	public void deleteFile(@NonNull final String url, final Boolean force) throws FSServiceException
	{
		try
		{
			val fsFile = fs.findFile(url);
			if (fsFile.isPresent())
			{
				if (!fs.deleteFile(fsFile.get(),force != null && force))
					throw new FSServiceException("Unable to delete " + url + "!");
			}
			else
				throw new FSServiceException(url + " not found!");
		}
		catch (Exception e)
		{
			throw new FSServiceException(e);
		}
	}

}
