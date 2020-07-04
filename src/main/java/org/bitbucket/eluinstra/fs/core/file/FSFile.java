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
import java.time.Instant;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@XmlRootElement(name="fsFile")
@XmlAccessorType(XmlAccessType.FIELD)
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@Setter(value = AccessLevel.PACKAGE)
public class FSFile
{
	@NonNull
	@XmlElement(required=true)
	String virtualPath;
	@NonNull
	@XmlTransient
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
	@XmlElement
	Period period;
	@XmlElement(required=true)
	long clientId;

	@Builder
	public FSFile(
			@NonNull String virtualPath,
			@NonNull String realPath,
			@NonNull String filename,
			@NonNull String contentType,
			@NonNull String md5checksum,
			@NonNull String sha256checksum,
			Instant startDate,
			Instant endDate,
			long clientId)
	{
		this.virtualPath = virtualPath;
		this.realPath = realPath;
		this.filename = filename;
		this.contentType = contentType;
		this.md5checksum = md5checksum;
		this.sha256checksum = sha256checksum;
		this.period = Period.of(startDate,endDate);
		this.clientId = clientId;
	}
	
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
