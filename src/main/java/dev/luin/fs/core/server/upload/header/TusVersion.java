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
import io.vavr.Tuple;
import io.vavr.Tuple2;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TusVersion extends TusHeader
{
	private static final TusVersion DEFAULT = ConstHeaderValue.of("1.0.0").map(v -> new TusVersion(v)).get();

	public static TusVersion get()
	{
		return DEFAULT;
	}

	@NonNull
	ConstHeaderValue value;

	private TusVersion(@NonNull ConstHeaderValue value)
	{
		super("Tus-Version");
		this.value = value;
	}

	public Tuple2<String,String> asTuple()
	{
		return Tuple.of(DEFAULT.getName(),DEFAULT.toString());
	}

	@Override
	public String toString()
	{
		return value.toString();
	}
}
