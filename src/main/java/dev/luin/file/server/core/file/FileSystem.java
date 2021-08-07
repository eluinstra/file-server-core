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

import static dev.luin.file.server.core.file.RandomFile.createRandomPathSupplier;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.function.Predicate;

import org.apache.commons.lang3.RandomStringUtils;

import io.vavr.Function1;
import io.vavr.Tuple;
import io.vavr.control.Option;
import io.vavr.control.Try;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NonNull;
import lombok.val;
import lombok.experimental.FieldDefaults;

@Builder
@FieldDefaults(level=AccessLevel.PRIVATE, makeFinal=true)
public class FileSystem
{
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

	public Try<FSFile> createNewFile(@NonNull final NewFSFile newFile, @NonNull final FSUser user)
	{
		return RandomFile.create(createRandomPathSupplier(baseDir,filenameLength))
				.flatMap(writeFile(newFile))
				.map(randomFile -> Tuple.of(randomFile,Sha256Checksum.of(randomFile.getFile())))
				.filterTry(tuple -> newFile.getSha256Checksum().map(checksum -> tuple._2.equals(checksum)).getOrElse(true),tuple -> new IOException("Checksum Error"))
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
						.build())
				.map(fsFileDAO::insertFile);
	}
	
	private static final Function1<RandomFile,Try<RandomFile>> writeFile(NewFSFile newFile)
	{
		return file -> Try.success(file)
			.flatMapTry(f -> f.write(newFile.getInputStream())
			.map(x -> file));
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

	public Try<FSFile> createEmptyFile(@NonNull final EmptyFSFile emptyFile, @NonNull final FSUser user)
	{
		return emptyFile.getLength()
				.flatMap(createRandomFile(emptyFile,user));
	}

	private Function1<Option<Length>,Try<FSFile>> createRandomFile(EmptyFSFile emptyFile, FSUser user)
	{
		return length -> RandomFile.create(createRandomPathSupplier(baseDir,filenameLength))
				.map(file -> FSFile.builder()
						.virtualPath(createRandomVirtualPath())
						.path(file.getPath())
						.name(emptyFile.getName())
						.contentType(emptyFile.getContentType())
						.timestamp(new Timestamp())
						.validTimeFrame(TimeFrame.EMPTY_TIME_FRAME)
						.userId(user.getId())
						.length(length.getOrNull())
						.build())
				.map(fsFileDAO::insertFile);
	}

	public Function1<FSFile,Try<FSFile>> appendToFile(InputStream input, Length length)
	{
		return fsFile -> fsFile.append(input,length)
				.peek(fsFileDAO::updateFile);
	}

	public Function1<FSFile,Try<Boolean>> deleteFile(Boolean force)
	{
		return (file) -> file.delete()
				.peek(succeeded -> {
					if (succeeded || force)
						fsFileDAO.deleteFile(file.getVirtualPath());
				});
	}

}
