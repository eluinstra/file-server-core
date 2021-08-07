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
package dev.luin.file.server.core;

import static io.vavr.control.Try.failure;
import static io.vavr.control.Try.success;

import java.util.function.Consumer;
import java.util.regex.Pattern;

import io.vavr.Function1;
import io.vavr.Function2;
import io.vavr.Function3;
import io.vavr.control.Try;
import lombok.val;

public interface ValueObject<T>
{
	T getValue();

	static Try<String> inclusiveBetween(final long start, final long end, final String value)
	{
		val length = value.length();
		return start <= length && length <= end ? success(value) : failure(toIllegalArgumentException("Length is not between %d and %d",start, end));
	}

	static IllegalArgumentException toIllegalArgumentException(String string, final Object...args)
	{
		return new IllegalArgumentException(String.format(string,args));
	}

	static Function1<String,Try<String>> isNotNull = o -> o == null ? failure(new IllegalArgumentException("Value is null")) : success(o);
	static Function3<Long,Long,String,Try<String>> inclusiveBetween = Function3.of(ValueObject::inclusiveBetween);
	static Function2<String,String,Try<String>> matchesPattern = (pattern,value) -> Pattern.matches(pattern,value) ? success(value) : failure(toIllegalArgumentException("Value does not match %s",pattern));
	static Function2<Long,Long,Try<Long>> isGreaterThenOrEqualTo = (minValue, value) -> value >= minValue ? success(value) : failure(toIllegalArgumentException("Value is less than %d",minValue));
	static Function1<Long,Try<Long>> isPositive = isGreaterThenOrEqualTo.apply(0L);
	static Function1<String,Long> toLong = Long::parseLong;
	static Function1<String,Try<Long>> safeToLong = v -> Function1.lift(toLong).apply(v).map(Try::success).getOrElse(failure(new IllegalArgumentException("Invalid number")));
	static Function1<String,String> toUpperCase = v -> v.toUpperCase();
	static Consumer<RuntimeException> Throw = t -> { throw t; };
}
