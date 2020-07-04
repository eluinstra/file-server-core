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
import java.io.OutputStream;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.Optional;
import java.util.function.Function;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.transaction.annotation.Transactional;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NonNull;
import lombok.val;
import lombok.experimental.FieldDefaults;

@Builder
@FieldDefaults(level=AccessLevel.PRIVATE, makeFinal=true)
@AllArgsConstructor
@Transactional(transactionManager = "dataSourceTransactionManager")
public class FileSystem
{
	public static final Function<String,File> getFile = path -> Paths.get(path).toFile();
	@NonNull
	FSFileDAO fsFileDAO;
	@NonNull
	SecurityManager securityManager;
	@NonNull
	String baseDir;
	int filenameLength;

	public FSFile createFile(
			@NonNull final String virtualPath,
			@NonNull final String filename,
			@NonNull final String contentType,
			final String sha256checksum,
			final Instant startDate,
			final Instant endDate,
			@NonNull final Long clientId,
			@NonNull final InputStream content) throws IOException
	{
		val realPath = createRandomFile();
		val file = getFile.apply(realPath);
		write(content,file);
		val calculatedSha256Checksum = calculateSha256Checksum(file);
		if (validateChecksum(sha256checksum,calculatedSha256Checksum))
		{
			val md5Checksum = calculateMd5Checksum(file);
			val result = FSFile.builder()
					.virtualPath(virtualPath)
					.realPath(realPath)
					.filename(filename)
					.contentType(contentType)
					.md5checksum(md5Checksum)
					.sha256checksum(calculatedSha256Checksum)
					.startDate(startDate)
					.endDate(endDate)
					.clientId(clientId)
					.build();
			fsFileDAO.insertFile(result);
			return result;
		}
		else
			throw new IOException("Checksum error for file " + virtualPath + ". Checksum of the file uploaded (" + calculatedSha256Checksum + ") is not equal to the provided checksum (" + sha256checksum + ")");
	}
	
	public FSFile createFile(
			@NonNull final String virtualPath,
			@NonNull final String filename,
			@NonNull final String contentType,
			final String sha256checksum,
			@NonNull final Long clientId,
			@NonNull final InputStream content) throws IOException
	{
		val realPath = createRandomFile();
		val file = getFile.apply(realPath);
		write(content,file);
		val calculatedSha256Checksum = calculateSha256Checksum(file);
		if (validateChecksum(sha256checksum,calculatedSha256Checksum))
		{
			val md5Checksum = calculateMd5Checksum(file);
			val result = FSFile.builder()
					.virtualPath(virtualPath)
					.realPath(realPath)
					.filename(filename)
					.contentType(contentType)
					.md5checksum(md5Checksum)
					.sha256checksum(calculatedSha256Checksum)
					.clientId(clientId)
					.build();
			fsFileDAO.insertFile(result);
			return result;
		}
		else
			throw new IOException("Checksum error for file " + virtualPath + ". Checksum of the file uploaded (" + calculatedSha256Checksum + ") is not equal to the provided checksum (" + sha256checksum + ")");
	}

	private String createRandomFile() throws IOException
	{
		while (true)
		{
			val filename = RandomStringUtils.randomNumeric(filenameLength);
			val result = Paths.get(baseDir,filename);
			if (!result.toFile().exists())
				return result.toString();
		}
	}

	private void write(final InputStream input, final File file) throws FileNotFoundException, IOException
	{
		try (val output = new FileOutputStream(file))
		{
			IOUtils.copyLarge(input,output);
		}
	}

	public void write(@NonNull final FSFile fsFile, @NonNull final OutputStream output) throws IOException
	{
		val file = fsFile.getFile();
		if (!file.exists())
			throw new FileNotFoundException(fsFile.getVirtualPath());
		try (val input = new FileInputStream(file))
		{
			IOUtils.copyLarge(input,output);
		}
	}

	public void write(@NonNull final FSFile fsFile, @NonNull final OutputStream output, final long first, final long length) throws IOException
	{
		val file = fsFile.getFile();
		if (!file.exists())
			throw new FileNotFoundException(fsFile.getVirtualPath());
		try (val input = new FileInputStream(file))
		{
			IOUtils.copyLarge(input,output,first,length);
		}
	}

	private String calculateSha256Checksum(final File file) throws FileNotFoundException, IOException
	{
		try (val is = new FileInputStream(file))
		{
			return DigestUtils.sha256Hex(is);
		}
	}

	private String calculateMd5Checksum(File file) throws FileNotFoundException, IOException
	{
		try (val is = new FileInputStream(file))
		{
			return DigestUtils.md5Hex(is);
		}
	}

	private boolean validateChecksum(final String checksum, final String calculatedChecksum)
	{
		return StringUtils.isEmpty(checksum) || checksum.equalsIgnoreCase(calculatedChecksum);
	}

	public boolean existsFile(@NonNull final String virtualPath)
	{
		//TODO
		return fsFileDAO.findFileByVirtualPath(virtualPath).isPresent();
	}

	public Optional<FSFile> findFile(@NonNull final String virtualPath)
	{
		return fsFileDAO.findFileByVirtualPath(virtualPath);
	}

	public Optional<FSFile> findFile(@NonNull final byte[] clientCertificate, @NonNull final String virtualPath) throws FileNotFoundException
	{
		val result = fsFileDAO.findFileByVirtualPath(virtualPath);
		return result.filter(r -> securityManager.isAuthorized(clientCertificate,r) && isValidTimeFrame(result.get()));
	}

	private boolean isValidTimeFrame(final FSFile fsFile)
	{
		val now = Instant.now();
		return fsFile.getPeriod() == null || (fsFile.getPeriod().getStartDate() != null && fsFile.getPeriod().getStartDate().compareTo(now) <= 0
				&& fsFile.getPeriod().getEndDate() != null && fsFile.getPeriod().getEndDate().compareTo(now) > 0);
	}

	public boolean deleteFile(@NonNull final FSFile fsFile, final boolean force)
	{
		val result = fsFile.getFile().delete();
		if (force || result)
			fsFileDAO.deleteFile(fsFile.getVirtualPath());
		return force || result;
	}

}
