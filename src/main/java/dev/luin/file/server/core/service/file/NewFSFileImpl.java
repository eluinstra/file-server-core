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
package dev.luin.file.server.core.service.file;

import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;

import dev.luin.file.server.core.file.ContentType;
import dev.luin.file.server.core.file.Filename;
import dev.luin.file.server.core.file.NewFSFile;
import dev.luin.file.server.core.file.Sha256Checksum;
import io.vavr.control.Option;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor(staticName = "of")
public class NewFSFileImpl implements NewFSFile
{
	@NonNull
	NewFile file;

	@Override
	public Filename getName()
	{
		return new Filename(file.getContent().getName());
	}

	@Override
	public ContentType getContentType()
	{
		return new ContentType(file.getContent().getContentType());
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
		return file.getContent().getInputStream();
	}

}
