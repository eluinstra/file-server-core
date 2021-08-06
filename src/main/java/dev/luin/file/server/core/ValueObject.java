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

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.Pattern;

import io.vavr.Function1;
import io.vavr.Function2;
import io.vavr.Function3;
import io.vavr.control.Either;
import io.vavr.control.Option;
import io.vavr.control.Try;
import lombok.val;

public interface ValueObject<T>
{
	T getValue();

	static Either<String,String> inclusiveBetween(final long start, final long end, final String value)
	{
		val length = value.length();
		return start <= length && length <= end ? Either.right(value) : Either.left(String.format("Value length is not between %d and %d",start,end));
	}

	static Function1<String,Either<String,String>> isNotNull = o -> o == null ? Either.left("Value is null") : Either.right(o);
	static Function1<String,Try<String>> isNotEmpty = o -> o == null ? Try.failure(new IllegalArgumentException("Value is null")) : Try.success(o);
	static Function3<Long,Long,String,Either<String,String>> inclusiveBetween = Function3.of(ValueObject::inclusiveBetween);
	static Function2<String,String,Either<String,String>> matchesPattern = (pattern,value) -> Pattern.matches(pattern,value) ? Either.right(value) : Either.left(String.format("Value does not match %s",pattern));
	static Function1<Long,Either<String,Long>> isPositive = value -> value >= 0 ? Either.right(value) : Either.left("Value is not positive");
	static Function2<Long,Long,Either<String,Long>> isGreaterThenOrEqualTo = (minValue, value) -> value >= minValue ? Either.right(value) : Either.left(String.format("Value is not greater than or equal to %d",minValue));
	static Function1<String,Long> toLong = Long::parseLong;
	static Function1<String,Option<Long>> safeToLong = Function1.lift(toLong);
	static Function1<String,String> toUpperCase = v -> v.toUpperCase();
	static Consumer<RuntimeException> Throw = t -> { throw t; };
}
