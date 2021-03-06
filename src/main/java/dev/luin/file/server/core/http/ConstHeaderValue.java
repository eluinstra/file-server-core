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
package dev.luin.file.server.core.http;

import io.vavr.collection.List;
import io.vavr.control.Option;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.Value;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ConstHeaderValue implements IHeaderValue
{
	@NonNull
	String value;

	public static Option<ConstHeaderValue> of(String value)
	{
		return value != null ? Option.of(new ConstHeaderValue(value)) : Option.none();
	}

	public static Option<ConstHeaderValue> of(String value, String...constants)
	{
		return IHeaderValue.parseValue(value).filter(v -> List.of(constants).exists(c -> c.equals(v))).map(v -> new ConstHeaderValue(v));
	}

	@Override
	public String toString()
	{
		return value;
	}
}
