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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;

import javax.activation.DataSource;

import org.apache.commons.io.IOUtils;

import dev.luin.file.server.core.server.download.header.Range;
import dev.luin.file.server.core.service.file.FileDataSource;
import io.vavr.control.Try;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Value;
import lombok.With;
import lombok.val;

@Builder(access = AccessLevel.PACKAGE)
@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class FSFile
{
	@NonNull
	VirtualPath virtualPath;
	@NonNull
	@Getter(value=AccessLevel.PACKAGE)
	Path path;
	Filename name;
	@NonNull
	ContentType contentType;
	@With
	Md5Checksum md5Checksum;
	@With
	Sha256Checksum sha256Checksum;
	@NonNull
	Timestamp timestamp;
	@NonNull
	TimeFrame validTimeFrame;
	@NonNull
	UserId userId;
	@With
	Length length;
	FileState state;

	private FSFile(@NonNull VirtualPath virtualPath, @NonNull Path path, Filename name, @NonNull ContentType contentType, Md5Checksum md5Checksum, Sha256Checksum sha256Checksum, @NonNull Timestamp timestamp, Instant startDate, Instant endDate, @NonNull UserId userId, Length length, FileState state)
	{
		this.virtualPath = virtualPath;
		this.path = path;
		this.name = name;
		this.contentType = contentType;
		this.md5Checksum = md5Checksum;
		this.sha256Checksum = sha256Checksum;
		this.timestamp = timestamp;
		this.validTimeFrame = new TimeFrame(startDate,endDate);
		this.userId = userId;
		this.length = length;
		this.state = state;
	}

	private File getFile()
	{
		return path.toFile();
	}

	public Length getFileLength()
	{
		return new Length(getFile().length());
	}

	public Instant getLastModified()
	{
		return Instant.ofEpochMilli(getFile().lastModified());
	}

	public boolean isBinary()
	{
		return getContentType().isBinary();
	}

	public boolean isCompleted()
	{
		return length.equals(getFileLength());
	}

	public boolean hasValidTimeFrame()
	{
		return validTimeFrame.isValid();
	}

	public DataSource toDataSource()
	{
		return new FileDataSource(getFile(),name,contentType);
	}

	FSFile append(@NonNull final InputStream input, final Length length)
	{
		val file = getFile();
		if (!file.exists() || isCompleted())
			throw new IllegalStateException("File not found");
		return Try.withResources(() -> new FileOutputStream(file,true))
			.of(o -> {
				//TODO if length == null then calculate maxLength using maxFileSize and file.length
				copy(input,o,length);
				if (isCompleted())
					return complete();
				else
					return this;
			})
			.get();
	}

	private void copy(final InputStream input, final FileOutputStream output, final Length length)
	{
		try
		{
			if (length != null)
				IOUtils.copyLarge(input,output,0,length.getValue());
			else
				IOUtils.copyLarge(input,output);
		}
		catch (IOException e)
		{
			throw new IllegalStateException(e);
		}
	}

	private FSFile complete()
	{
		val file = getFile();
		if (!file.exists())// || !fsFile.isCompleted())
			throw new IllegalStateException("File not found");
		val result = this
				.withSha256Checksum(Sha256Checksum.of(file))
				.withMd5Checksum(Md5Checksum.of(file));
		return result;
	}

	public long write(@NonNull final OutputStream output)
	{
		val file = getFile();
		if (!file.exists() || !isCompleted())
			throw new IllegalStateException("File not found");
		return Try.withResources(() -> new FileInputStream(file))
			.of(i -> IOUtils.copyLarge(i,output))
			.getOrElseThrow(t -> new IllegalStateException(t));
	}

	public long write(@NonNull final OutputStream output, @NonNull final Range range)
	{
		val file = getFile();
		if (!file.exists() || !isCompleted())
			throw new IllegalStateException("File not found");
		return Try.withResources(() -> new FileInputStream(file))
			.of(i -> IOUtils.copyLarge(i,output,range.getFirst(getFileLength()),range.getLength(getFileLength()).getValue()))
			.getOrElseThrow(t -> new IllegalStateException(t));
	}

	public boolean delete()
	{
		return Try.success(path)
				.mapTry(Files::deleteIfExists)
				.isSuccess();
	}

}
