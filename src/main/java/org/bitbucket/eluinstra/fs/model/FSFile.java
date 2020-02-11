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
	private String virtualPath;
	@NonNull
	private String realPath;
	@NonNull
	private String contentType;
	@NonNull
	private Period period;
	private final long clientId;
	@Getter(lazy=true)
	private final File file = Paths.get(realPath).toFile();
}
