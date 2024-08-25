/*
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

import dev.luin.file.server.core.file.encryption.EncryptionSecret;
import dev.luin.file.server.core.file.encryption.EncryptionService;
import dev.luin.file.server.core.service.file.InputStreamDataSource;
import io.vavr.Function1;
import io.vavr.Tuple;
import io.vavr.Tuple3;
import io.vavr.control.Option;
import io.vavr.control.Try;
import jakarta.activation.DataSource;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import lombok.val;
import org.apache.commons.lang3.RandomStringUtils;

@Builder
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FileSystem
{
	@NonNull
	FSFileDAO fsFileDAO;
	@NonNull
	Function1<FSUser, Predicate<FSFile>> isAuthorized;
	int virtualPathLength;
	@NonNull
	RandomFileGenerator randomFileGenerator;
	@NonNull
	EncryptionService encryptionService;

	public Option<FSFile> findFile(@NonNull final VirtualPath virtualPath)
	{
		return fsFileDAO.findFile(virtualPath);
	}

	public Option<FSFile> findFile(@NonNull final FSUser user, @NonNull final VirtualPath virtualPath)
	{
		return fsFileDAO.findFile(virtualPath).filter(isAuthorized.apply(user)).filter(FSFile::hasValidTimeFrame);
	}

	public List<VirtualPath> getFiles()
	{
		return fsFileDAO.selectFiles();
	}

	public Try<FSFile> createEncryptedFile(@NonNull final NewFSFile newFile, @NonNull final FSUser user)
	{
		val algorithm = encryptionService.getDefaultAlgorithm();
		val secret = encryptionService.generateSecret(algorithm);
		return randomFileGenerator.create()
				.flatMap(encryptFile(newFile, secret))
				.map(
						tuple -> FSFile.builder()
								.virtualPath(createRandomVirtualPath())
								.path(tuple._1.getPath())
								.name(newFile.getName())
								.contentType(newFile.getContentType())
								.encryptionAlgorithm(algorithm)
								.encryptionSecret(secret)
								.md5Checksum(new Md5Checksum(tuple._2.digest()))
								.sha256Checksum(new Sha256Checksum(tuple._3.digest()))
								.timestamp(new Timestamp())
								.validTimeFrame(new TimeFrame(newFile.getStartDate(), newFile.getEndDate()))
								.userId(user.getId())
								.length(tuple._1.getLength())
								.build())
				.map(fsFileDAO::insertFile);
	}

	private final Function1<RandomFile, Try<Tuple3<RandomFile, MessageDigest, MessageDigest>>> encryptFile(NewFSFile newFile, EncryptionSecret secret)
	{
		try
		{
			val md5 = Md5Checksum.messageDigest();
			val sha256 = Sha256Checksum.messageDigest();
			return file -> Try.success(file)
					.flatMapTry(
							f -> f.write(encryptionService.encryptionInputStream(new DigestInputStream(new DigestInputStream(newFile.getInputStream(), md5), sha256), secret))
									.map(x -> Tuple.of(file, md5, sha256)));
		}
		catch (NoSuchAlgorithmException e)
		{
			throw new IllegalStateException(e);
		}
	}

	private VirtualPath createRandomVirtualPath()
	{
		while (true)
		{
			val result = new VirtualPath(RandomStringUtils.randomAlphanumeric(virtualPathLength));
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
		return emptyFile.getLength().flatMap(createRandomFile(emptyFile, user));
	}

	private Function1<Option<Length>, Try<FSFile>> createRandomFile(EmptyFSFile emptyFile, FSUser user)
	{
		return length -> randomFileGenerator.create()
				.map(
						file -> FSFile.builder()
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

	public Function1<FSFile, Try<FSFile>> appendToFile(InputStream input, Length length)
	{
		return fsFile -> fsFile.append(input, length).peek(fsFileDAO::updateFile);
	}

	public Function1<FSFile, Try<Boolean>> deleteFile(Boolean force)
	{
		return file -> file.delete().peek(succeeded ->
		{
			if (succeeded || force)
				fsFileDAO.deleteFile(file.getVirtualPath());
		});
	}

	public DataSource toDecryptedDataSource(FSFile f)
	{
		try
		{
			val in = encryptionService.decryptionInputStream(f.getEncryptionAlgorithm(), new FileInputStream(f.getFile()), f.getEncryptionSecret());
			return new InputStreamDataSource(in, f.getName(), f.getContentType());
		}
		catch (FileNotFoundException e)
		{
			throw new IllegalStateException(e);
		}
	}

	public Consumer<FSFile> decryptToFile(Path filename)
	{
		// TODO handle exceptions
		return f -> Try.withResources(() -> decryptionInputStream(f), () -> new FileOutputStream(filename.toFile())).of(InputStream::transferTo);
	}

	private InputStream decryptionInputStream(FSFile f) throws FileNotFoundException
	{
		return encryptionService.decryptionInputStream(f.getEncryptionAlgorithm(), new FileInputStream(f.getFile()), f.getEncryptionSecret());
	}

	public boolean validate(FSFile f)
	{
		try (val in = encryptionService.decryptionInputStream(f.getEncryptionAlgorithm(), new FileInputStream(f.getFile()), f.getEncryptionSecret()))
		{
			in.transferTo(OutputStream.nullOutputStream());
			return true;
		}
		catch (FileNotFoundException e)
		{
			throw new IllegalStateException(e);
		}
		catch (IOException e)
		{
			return false;
		}
	}
}
