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
package dev.luin.file.server.core.server.upload;

import dev.luin.file.server.core.file.ContentType;
import dev.luin.file.server.core.file.EmptyFSFile;
import dev.luin.file.server.core.file.Length;
import dev.luin.file.server.core.server.upload.header.TusMaxSize;
import dev.luin.file.server.core.server.upload.header.UploadLength;
import dev.luin.file.server.core.server.upload.header.UploadMetadata;
import io.vavr.control.Option;
import dev.luin.file.server.core.file.Filename;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.val;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor(staticName = "of")
public class EmptyFSFileImpl implements EmptyFSFile
{
	@NonNull
	UploadRequest uploadRequest;
	TusMaxSize tusMaxSize;

	@Override
	public Filename getName()
	{
		val uploadMetadata = UploadMetadata.of(uploadRequest);
		return uploadMetadata.getFilename();
	}

	@Override
	public ContentType getContentType()
	{
		val uploadMetadata = UploadMetadata.of(uploadRequest);
		return uploadMetadata.getContentType();
	}

	@Override
	public Option<Length> getLength()
	{
		return UploadLength.of(uploadRequest,tusMaxSize).map(v -> v.toFileLength());
	}

}
