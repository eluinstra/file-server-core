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
package dev.luin.file.server.core.server.download;

import dev.luin.file.server.core.ValueObject;
import dev.luin.file.server.core.file.VirtualPath;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.Value;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class VirtualPathWithExtension implements ValueObject<VirtualPath>
{
	@NonNull
	VirtualPath value;
	@NonNull
	Extension extension;

	public VirtualPathWithExtension(final String path)
	{
		extension = Extension.of(path);
		value = new VirtualPath(extension.getPath(path));
	}
}
