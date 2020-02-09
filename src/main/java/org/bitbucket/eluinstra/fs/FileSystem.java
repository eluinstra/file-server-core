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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Date;
import java.util.Optional;

import javax.servlet.ServletOutputStream;

import org.apache.commons.io.IOUtils;
import org.bitbucket.eluinstra.fs.dao.FSDAO;
import org.bitbucket.eluinstra.fs.model.FSFile;
import org.bitbucket.eluinstra.fs.model.ContentRange;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class FileSystem
{
	@AllArgsConstructor
	public class SecurityManager
	{
		private FSDAO fsDAO;

		public boolean isAuthorized(byte[] clientCertificate, FSFile file)
		{
			return fsDAO.isAuthorized(clientCertificate,file.getId());
		}

	}

	private FSDAO fsDAO;
	private SecurityManager securityManager;

	public void createFile(FSFile fsFile, byte[] content)
	{
		
	}

	public FSFile findFile(byte[] clientCertificate, String path) throws FileNotFoundException
	{
		Optional<FSFile> fSFile = fsDAO.findFile(path);
		if (fSFile.isPresent() && securityManager.isAuthorized(clientCertificate,fSFile.get()) && isValidTimeFrame(fSFile.get()))
			return fSFile.get();
		throw new FileNotFoundException(path);
	}

	private boolean isValidTimeFrame(FSFile fsFile)
	{
		Date now = new Date();
		return fsFile.getStartDate().getTime() <= now.getTime() && fsFile.getEndDate().getTime() > now.getTime();
	}

}
