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

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

//@Builder
@AllArgsConstructor
@Getter
@EqualsAndHashCode
@ToString
public class FSFile
{
	@NonNull
	@XmlElement(required=true)
	private String virtualPath;
	@NonNull
	//TODO make inaccessible?
	private String realPath;
	@NonNull
	@XmlElement(required=true)
	private String contentType;
	@NonNull
	@XmlElement(required=true)
	private String checksum;
	@NonNull
	@XmlElement(required=true)
	private Period period;
	@XmlElement(required=true)
	private final long clientId;
	@Getter(lazy=true)
	//TODO make inaccessible?
	private final File file = FileSystem.getFile.apply(realPath);
}
