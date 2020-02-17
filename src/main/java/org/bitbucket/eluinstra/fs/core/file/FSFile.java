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

import javax.xml.bind.annotation.XmlElement;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

//@Builder
@AllArgsConstructor(access=AccessLevel.PACKAGE)
@Getter
@EqualsAndHashCode
@ToString
@FieldDefaults(level=AccessLevel.PRIVATE, makeFinal=true)
public class FSFile
{
	@NonNull
	@XmlElement(required=true)
	String virtualPath;
	@NonNull
	@Getter(value=AccessLevel.PACKAGE)
	String realPath;
	@NonNull
	@XmlElement(required=true)
	String filename;
	@NonNull
	@XmlElement(required=true)
	String contentType;
	@NonNull
	@XmlElement(required=true)
	String md5checksum;
	@NonNull
	@XmlElement(required=true)
	String sha256checksum;
	@NonNull
	@XmlElement(required=true)
	Period period;
	@XmlElement(required=true)
	long clientId;
//TODO: fix
//	@Getter(lazy=true, value=AccessLevel.PACKAGE)
//	File file = FileSystem.getFile.apply(realPath);

	File getFile()
	{
		return FileSystem.getFile.apply(realPath);
	}

	public long getFileLength()
	{
		//return ((File)((AtomicReference<Object>)file).get()).length();
		return getFile().length();
	}

	public long getFileLastModified()
	{
		//return ((File)((AtomicReference<Object>)file).get()).lastModified();
		return getFile().lastModified();
	}
}
