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
package dev.luin.file.server.core;

import static io.vavr.control.Try.failure;
import static io.vavr.control.Try.success;

import java.util.regex.Pattern;

import io.vavr.Function1;
import io.vavr.control.Try;

public interface ValueObject<T>
{
	T getValue();

	static Function1<String,Try<String>> isNotNull()
	{
		return object -> object == null ? failure(toIllegalArgumentException("Value is null")) : success(object);
	}

	static Function1<String,Try<String>> inclusiveBetween(Long min, Long max)
	{
		return value -> min <= value.length() && value.length() <= max ? success(value) : failure(toIllegalArgumentException("Length is not between %d and %d",min, max));
	}

	static Function1<String,Try<String>> matchesPattern(String pattern)
	{
		return value -> Pattern.matches(pattern,value) ? success(value) : failure(toIllegalArgumentException("Value does not match %s",pattern));
	}

	static Function1<Long,Try<Long>> isGreaterThenOrEqualTo(Long minValue)
	{
		return value -> value >= minValue ? success(value) : failure(toIllegalArgumentException("Value is less than %d",minValue));
	}

	static Function1<Long,Try<Long>> isPositive()
	{
		return isGreaterThenOrEqualTo(0L);
	}

	static Function1<String,Long> toLong()
	{
		return Long::parseLong;
	}

	static Function1<String,Try<Long>> safeToLong()
	{
		return value -> Function1.lift(toLong()).apply(value).map(Try::success).getOrElse(failure(toIllegalArgumentException("Invalid number")));
	}

	static Function1<String,String> toUpperCase()
	{
		return String::toUpperCase;
	}

	static IllegalArgumentException toIllegalArgumentException(String string, final Object...args)
	{
		return new IllegalArgumentException(String.format(string,args));
	}

}
