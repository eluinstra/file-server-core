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
package org.bitbucket.eluinstra.fs.core.http;

import io.vavr.control.Option;
import io.vavr.control.Try;
import lombok.NonNull;
import lombok.Value;

@Value
public class LongHeaderValue implements IHeaderValue
{
	@NonNull
	Long value;

	public static Option<LongHeaderValue> of(String value)
	{
		return parse(value,Long.MIN_VALUE,Long.MAX_VALUE).map(p -> new LongHeaderValue(p));
	}

	public static Option<LongHeaderValue> of(String value, long minValue, long maxValue)
	{
		return parse(value,minValue,maxValue).map(p -> new LongHeaderValue(p));
	}

	@Override
	public String toString()
	{
		return value != null ? value.toString() : null;
	}

	private static Option<Long> parse(String s, long minValue, long maxValue)
	{
		return Try.of(() ->IHeaderValue.parseValue(s).map(v -> Long.valueOf(v))).getOrElseGet(t -> Option.none());
	}
}
