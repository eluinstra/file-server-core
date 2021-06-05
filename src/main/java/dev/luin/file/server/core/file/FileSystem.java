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
package dev.luin.file.server.core.file;

import java.io.InputStream;
import java.time.Instant;
import java.util.List;

import org.apache.commons.lang3.RandomStringUtils;

import io.vavr.control.Option;
import io.vavr.control.Try;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NonNull;
import lombok.val;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Builder
@FieldDefaults(level=AccessLevel.PRIVATE, makeFinal=true)
@AllArgsConstructor
public class FileSystem
{
	@NonNull
	FSFileDAO fsFileDAO;
	@NonNull
	SecurityManager securityManager;
	int virtualPathLength;
	@NonNull
	String baseDir;
	int filenameLength;

	public Option<FSFile> findFile(@NonNull final VirtualPath virtualPath)
	{
		return fsFileDAO.findFile(virtualPath);
	}

	public Option<FSFile> findFile(@NonNull final FSUser user, @NonNull final VirtualPath virtualPath)
	{
		return fsFileDAO.findFile(virtualPath)
				.filter(r -> securityManager.isAuthorized(user,r) && r.hasValidTimeFrame());
	}

	public List<VirtualPath> getFiles()
	{
		return fsFileDAO.selectFiles();
	}

	public FSFile createNewFile(@NonNull NewFSFile newFile, @NonNull final UserId userId)
	{
		val randomFile = RandomFile.create(baseDir,filenameLength)
				.andThenTry(f -> f.write(newFile.getInputStream()))
				.get();
		val calculatedSha256Checksum = Sha256Checksum.of(randomFile.getFile());
		if (calculatedSha256Checksum.validate(newFile.getSha256Checksum()))
		{
			val result = FSFile.builder()
					.virtualPath(createRandomVirtualPath())
					.path(randomFile.getPath())
					.name(newFile.getName())
					.contentType(newFile.getContentType())
					.md5Checksum(Md5Checksum.of(randomFile.getFile()))
					.sha256Checksum(calculatedSha256Checksum)
					.timestamp(Instant.now())
					.validTimeFrame(new TimeFrame(newFile.getStartDate(),newFile.getEndDate()))
					.userId(userId)
					.length(randomFile.getLength())
					.build();
			fsFileDAO.insertFile(result);
			return result;
		}
		else
			throw new IllegalStateException("Checksum error for file " + newFile.getName() + ". Checksum of the file uploaded (" + calculatedSha256Checksum + ") is not equal to the provided checksum (" + newFile.getSha256Checksum() + ")");
	}
	
	private VirtualPath createRandomVirtualPath()
	{
		while (true)
		{
			val result = new VirtualPath("/" + RandomStringUtils.randomAlphanumeric(virtualPathLength));
			if (existsVirtualPath(result))
				return result;
		}
	}

	private boolean existsVirtualPath(VirtualPath virtualPath)
	{
		return fsFileDAO.findFile(virtualPath).isEmpty();
	}

	public FSFile createEmptyFile(@NonNull final EmptyFSFile emptyFile, @NonNull final UserId userId)
	{
		val randomFile = RandomFile.create(baseDir,filenameLength).get();
		val result = FSFile.builder()
				.virtualPath(createRandomVirtualPath())
				.path(randomFile.getPath())
				.name(emptyFile.getName())
				.contentType(emptyFile.getContentType())
				.timestamp(Instant.now())
				.userId(userId)
				.length(emptyFile.getLength())
				.build();
		fsFileDAO.insertFile(result);
		return result;
	}

	public FSFile appendToFile(@NonNull final FSFile fsFile, @NonNull final InputStream input, final FileLength length)
	{
		val result = fsFile.append(input,length);
		if (result.isCompleted())
			fsFileDAO.updateFile(result);
		return result;
	}

	public boolean deleteFile(@NonNull final FSFile fsFile, final boolean force)
	{
		val result = Try.of(() -> fsFile.delete()).onFailure(t -> log.error("",t));
		if (force || result.isSuccess())
			fsFileDAO.deleteFile(fsFile.getVirtualPath());
		return force || result.getOrElse(false);
	}

}
