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
package org.bitbucket.eluinstra.fs.core.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.Optional;
import java.util.function.Function;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.bitbucket.eluinstra.fs.core.dao.FSDAO;

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

	public static final Function<String,File> getFile = path -> Paths.get(path).toFile();
	@NonNull
	private FSDAO fsDAO;
	@NonNull
	private SecurityManager securityManager;
	@NonNull
	private String rootDirectory;
	private int filenameLength;

	public FSFile createFile(@NonNull String virtualPath, @NonNull String contentType, String checksum, @NonNull Period period, @NonNull Long clientId, InputStream inputStream) throws IOException
	{
		String realPath = createRandomFile();
		File file = getFile.apply(realPath);
		write(inputStream,file);
		String calculatedChecksum = calculateChecksum(file);
		if (validateChecksum(checksum,calculatedChecksum))
		{
			FSFile result = new FSFile(virtualPath,realPath,contentType,checksum,period,clientId);
			fsDAO.insertFile(result);
			return result;
		}
		else
			throw new IOException("Checksum error for file " + virtualPath + ". Checksum of the file uploaded (" + calculatedChecksum + ") is not equal to the provided checksum (" + checksum + ")");
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

	private void write(@NonNull InputStream inputStream, @NonNull File file) throws FileNotFoundException, IOException
	{
		try (FileOutputStream output = new FileOutputStream(file))
		{
			IOUtils.copyLarge(inputStream,output);
		}
	}

	private String calculateChecksum(File file) throws FileNotFoundException, IOException
	{
		try (FileInputStream is = new FileInputStream(file))
		{
			return DigestUtils.sha256Hex(is);
		}
	}

	private boolean validateChecksum(@NonNull String checksum, String calculatedChecksum)
	{
		return StringUtils.isEmpty(checksum) || checksum.equalsIgnoreCase(calculatedChecksum);
	}

	public Optional<FSFile> findFile(@NonNull String virtualPath)
	{
		return fsDAO.findFileByVirtualPath(virtualPath);
	}

	public FSFile findFile(@NonNull byte[] clientCertificate, @NonNull String virtualPath) throws FileNotFoundException
	{
		Optional<FSFile> result = fsDAO.findFileByVirtualPath(virtualPath);
		if (result.isPresent()
				&& securityManager.isAuthorized(clientCertificate,result.get())
				&& isValidTimeFrame(result.get()))
			return result.get();
		throw new FileNotFoundException(virtualPath);
	}

	private boolean isValidTimeFrame(FSFile fsFile)
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
