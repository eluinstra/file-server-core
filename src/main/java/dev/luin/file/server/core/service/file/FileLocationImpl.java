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
package dev.luin.file.server.core.service.file;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;

import javax.ws.rs.core.MediaType;

import dev.luin.file.server.core.file.ContentType;
import dev.luin.file.server.core.file.Filename;
import dev.luin.file.server.core.file.NewFSFile;
import dev.luin.file.server.core.file.Sha256Checksum;
import io.vavr.control.Option;
import io.vavr.control.Try;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor(staticName = "of")
public class FileLocationImpl implements NewFSFile
{
	@NonNull
	FileLocation file;
	@NonNull
	Path sharedFs;

	@Override
	public Filename getName()
	{
		return file.getName();
	}

	@Override
	public ContentType getContentType()
	{
		final java.nio.file.Path f = sharedFs.resolve(file.getName().getValue());
		final String contentType = Try.of(() -> Files.probeContentType(f))
			.getOrElse(MediaType.APPLICATION_OCTET_STREAM);

		return new ContentType(contentType);
	}

	@Override
	public Option<Sha256Checksum> getSha256Checksum()
	{
		return file.getSha256Checksum() == null ? Option.none() : Option.some(new Sha256Checksum(file.getSha256Checksum()));
	}

	@Override
	public Instant getStartDate()
	{
		return file.getStartDate();
	}

	@Override
	public Instant getEndDate()
	{
		return file.getEndDate();
	}

	@Override
	public InputStream getInputStream() throws IOException
	{
		final java.nio.file.Path f = sharedFs.resolve(file.getName().getValue());
		if (!f.toAbsolutePath().startsWith(sharedFs))
			throw new IOException("Illegal file access");
		
		return new FileInputStream(f.toFile());
	}

}
