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
package dev.luin.fs.core.server.upload.header;

import dev.luin.fs.core.http.ConstHeaderValue;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TusExtension extends TusHeader
{
	private static final TusExtension DEFAULT = ConstHeaderValue.of("create").map(v -> new TusExtension(v)).get();

	public static TusExtension get()
	{
		return DEFAULT;
	}

	@NonNull
	ConstHeaderValue value;

	private TusExtension(ConstHeaderValue value)
	{
		super("Tus-Extension");
		this.value = value;
	}

	@Override
	public String toString()
	{
		return value.toString();
	}
}
