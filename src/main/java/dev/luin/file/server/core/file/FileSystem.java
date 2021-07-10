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

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.function.Predicate;

import org.apache.commons.lang3.RandomStringUtils;

import dev.luin.file.server.core.Common;
import io.vavr.Function1;
import io.vavr.Function2;
import io.vavr.Function3;
import io.vavr.Tuple;
import io.vavr.control.Either;
import io.vavr.control.Option;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NonNull;
import lombok.val;
import lombok.experimental.FieldDefaults;

@Builder
@FieldDefaults(level=AccessLevel.PRIVATE, makeFinal=true)
public class FileSystem
{
	private static final Function2<NewFSFile,RandomFile,Either<IOException,RandomFile>> writeFile = (newFile,file) -> {
		try
		{
			file.write(newFile.getInputStream());
			return Either.right(file);
		}
		catch (IOException e)
		{
			return Either.left(e);
		}
	};

	@NonNull
	FSFileDAO fsFileDAO;
	@NonNull
	Function1<FSUser,Predicate<FSFile>> isAuthorized;
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
				.filter(isAuthorized.apply(user))
				.filter(FSFile::hasValidTimeFrame);
	}

	public List<VirtualPath> getFiles()
	{
		return fsFileDAO.selectFiles();
	}

	public Either<IOException,FSFile> createNewFile(@NonNull final NewFSFile newFile, @NonNull final FSUser user)
	{
		return RandomFile.create(baseDir,filenameLength)
				.flatMap(writeFile.apply(newFile))
				.map(randomFile -> Tuple.of(randomFile,Sha256Checksum.of(randomFile.getFile())))
				.filterOrElse(tuple -> tuple._2.equals(newFile.getSha256Checksum()),tuple -> new IOException("Checksum Error"))
				.map(tuple -> FSFile.builder()
						.virtualPath(createRandomVirtualPath())
						.path(tuple._1.getPath())
						.name(newFile.getName())
						.contentType(newFile.getContentType())
						.md5Checksum(Md5Checksum.of(tuple._1.getFile()))
						.sha256Checksum(tuple._2)
						.timestamp(new Timestamp())
						.validTimeFrame(new TimeFrame(newFile.getStartDate(),newFile.getEndDate()))
						.userId(user.getId())
						.length(tuple._1.getLength())
						.build());
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

	private boolean existsVirtualPath(final VirtualPath virtualPath)
	{
		return fsFileDAO.findFile(virtualPath).isEmpty();
	}

	public Either<IOException,FSFile> createEmptyFile(@NonNull final EmptyFSFile emptyFile, @NonNull final FSUser user)
	{
		return emptyFile.getLength()
				.mapLeft(Common.toIOException)
				.flatMap(createRandomFile().apply(emptyFile,user));
	}

	private Function3<EmptyFSFile,FSUser,Option<Length>,Either<IOException,FSFile>> createRandomFile()
	{
		return (emptyFile,user,length) -> RandomFile.create(baseDir,filenameLength)
				.map(file -> FSFile.builder()
						.virtualPath(createRandomVirtualPath())
						.path(file.getPath())
						.name(emptyFile.getName())
						.contentType(emptyFile.getContentType())
						.timestamp(new Timestamp())
						.userId(user.getId())
						.length(length.getOrNull())
						.build())
				.map(fsFileDAO::insertFile);
	}

	public Function3<InputStream,Length,FSFile,Either<IOException,FSFile>> appendToFile()
	{
		return (input,length,fsFile) -> fsFile.append(input,length)
				.peek(fsFileDAO::updateFile);
	}

	public Function2<Boolean,FSFile,Either<IOException,Boolean>> deleteFile()
	{
		return (force,file) -> file.delete()
				.peek(b -> {
					if (b || force)
						fsFileDAO.deleteFile(file.getVirtualPath());
				});
	}

}
