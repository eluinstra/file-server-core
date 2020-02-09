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
package org.bitbucket.eluinstra.fs;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.Optional;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.bitbucket.eluinstra.fs.dao.FSDAO;
import org.bitbucket.eluinstra.fs.model.FSFile;
import org.bitbucket.eluinstra.fs.model.Period;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FileSystem
{
	@RequiredArgsConstructor
	public class SecurityManager
	{
		@NonNull
		private FSDAO fsDAO;

		public boolean isAuthorized(@NonNull byte[] clientCertificate, @NonNull FSFile file)
		{
			return fsDAO.isAuthorized(clientCertificate,file.getVirtualPath());
		}

	}

	@NonNull
	private FSDAO fsDAO;
	@NonNull
	private SecurityManager securityManager;
	@NonNull
	private String rootDirectory;
	private int filenameLength;

	public FSFile createFile(@NonNull String virtualPath, @NonNull String contentType, @NonNull Period period, @NonNull Long clientId) throws IOException
	{
		String realPath = createRandomFile();
		FSFile result = new FSFile(virtualPath,realPath,contentType,period,clientId);
		fsDAO.insertFile(result);
		return result;
	}
	
	public void writeFile(@NonNull InputStream inputStream, @NonNull FSFile fsFile) throws FileNotFoundException, IOException
	{
		IOUtils.copyLarge(inputStream,new FileOutputStream(fsFile.getFile()));
	}

	private String createRandomFile() throws IOException
	{
		Path result = null; 
		do
		{
			String filename = RandomStringUtils.randomNumeric(filenameLength);
			result = Paths.get(rootDirectory,filename);
		}
		while (result.toFile().exists());
		return result.toString();
	}

	public FSFile findFile(@NonNull byte[] clientCertificate, @NonNull String path) throws FileNotFoundException
	{
		Optional<FSFile> result = fsDAO.findFile(path);
		if (result.isPresent()
				&& securityManager.isAuthorized(clientCertificate,result.get())
				&& isValidTimeFrame(result.get()))
			return result.get();
		throw new FileNotFoundException(path);
	}

	private boolean isValidTimeFrame(@NonNull FSFile fsFile)
	{
		Date now = new Date();
		return fsFile.getPeriod().getStartDate().getTime() <= now.getTime()
				&& fsFile.getPeriod().getEndDate().getTime() > now.getTime();
	}

	public boolean deleteFile(@NonNull FSFile fsFile, boolean force)
	{
		boolean result = fsFile.getFile().delete();
		if (force || result)
			fsDAO.deleteFile(fsFile.getVirtualPath());
		return force || result;
	}

}
