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

import io.vavr.control.Option;
import io.vavr.control.Try;
import lombok.NonNull;

public class LongHeaderValue
{
	public static Try<Option<Long>> getOptional(String value, long minValue, long maxValue)
	{
		return parseOption(value)
				.filterTry(v -> validateOptional(v,minValue,maxValue),() -> new IllegalArgumentException());
	}

	private static Try<Option<Long>> parseOption(String s)//, long minValue, long maxValue)
	{
		return Try.of(() -> IHeaderValue.parseValue(s))
				.mapTry(v -> v.map(x -> Long.valueOf(x)));
	}

	private static boolean validateOptional(Option<Long> value, long minValue, long maxValue)
	{
		return value.isEmpty() || value.exists(v -> minValue <= v && v <= maxValue);
	}

	public static Try<Long> get(@NonNull String value, long minValue, long maxValue)
	{
		return parse(value).filterTry(v -> validate(v,minValue,maxValue),() -> new IllegalArgumentException());
	}

	private static Try<Long> parse(String s)//, long minValue, long maxValue)
	{
		return IHeaderValue.parseValue(s)
				.toTry(() -> new NullPointerException())
				.mapTry(v -> Long.valueOf(v));
	}

	private static boolean validate(Long v, long minValue, long maxValue)
	{
		return minValue <= v && v <= maxValue;
	}
}
