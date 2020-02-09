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
package org.bitbucket.eluinstra.fs.model;

import java.io.File;
import java.nio.file.Paths;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

//@Builder
//@AllArgsConstructor
@RequiredArgsConstructor
@EqualsAndHashCode
@ToString
public class FSFile
{
	@Getter
	private Long id;
	@NonNull
	@Getter
	private String virtualPath;
	@NonNull
	@Getter
	private String realPath;
	@NonNull
	@Getter
	private String contentType;
	@NonNull
	@Getter
	private Period period;
	private File file;

	public File getFile()
	{
		if (file == null)
			file = Paths.get(realPath).toFile();
		return file;
	}
}
