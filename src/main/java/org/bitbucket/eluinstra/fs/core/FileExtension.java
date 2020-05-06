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
package org.bitbucket.eluinstra.fs.core;

import java.util.Arrays;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
public enum FileExtension
{
	NONE(""), MD5(".md5"), SHA256(".sha256");
	
	@NonNull
	String extension;

	public static FileExtension getExtension(String path)
	{
		return Arrays.stream(FileExtension.values())
			.filter(e -> e != NONE && path.endsWith(e.extension))
			.findFirst().orElse(NONE);
	}

	public String getPath(String path)
	{
		return path.endsWith(extension) ? path.substring(0,path.length() - extension.length()) : path;
	}

	public String getContentType()
	{
		return "text/plain";
	}
}
