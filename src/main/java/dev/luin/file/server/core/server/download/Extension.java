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
package dev.luin.file.server.core.server.download;

import io.vavr.collection.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;


@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
public enum Extension
{
	NONE(""), MD5(".md5"), SHA256(".sha256");
	
	@NonNull
	String extension;

	public static Extension create(@NonNull final String path)
	{
		return List.of(values())
			.filter(e -> e != NONE && path.endsWith(e.extension))
			.getOrElse(NONE);
	}

	public String getPath(@NonNull final String path)
	{
		return path.endsWith(extension) ? path.substring(0,path.length() - extension.length()) : path;
	}
}
