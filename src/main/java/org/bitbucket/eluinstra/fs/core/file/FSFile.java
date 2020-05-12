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

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Value;

@Builder(access = AccessLevel.PACKAGE)
@Value
public class FSFile
{
	@NonNull
	String virtualPath;
	@NonNull
	@Getter(value=AccessLevel.PACKAGE)
	String realPath;
	@NonNull
	String filename;
	@NonNull
	String contentType;
	@NonNull
	String md5checksum;
	@NonNull
	String sha256checksum;
	Period period;
	long clientId;

	File getFile()
	{
		return FileSystem.getFile.apply(realPath);
	}

	public long getFileLength()
	{
		return getFile().length();
	}

	public long getFileLastModified()
	{
		return getFile().lastModified();
	}
}
