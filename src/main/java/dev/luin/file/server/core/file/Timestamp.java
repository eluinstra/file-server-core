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
package dev.luin.file.server.core.file;

import java.time.Instant;

import dev.luin.file.server.core.ValueObject;
import lombok.NonNull;
import lombok.Value;

@Value
public class Timestamp implements ValueObject<Instant>
{
	@NonNull
	Instant value;

	public Timestamp()
	{
		this(Instant.now());
	}

	public Timestamp(@NonNull Instant timestamp)
	{
		value = timestamp;
	}
}
