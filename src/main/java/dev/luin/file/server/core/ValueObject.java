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

import static org.apache.commons.lang3.Validate.inclusiveBetween;
import static org.apache.commons.lang3.Validate.isTrue;
import static org.apache.commons.lang3.Validate.matchesPattern;

import io.vavr.Function1;
import io.vavr.Function2;
import io.vavr.Function3;
import io.vavr.control.Try;

public interface ValueObject<T>
{
	T getValue();

	static Function3<Long,Long,String,String> inclusiveBetween = (start,end,value) -> Try.success(value)
			.andThen(v -> inclusiveBetween(start,end,v.length()))
			.get();
	static Function2<String,String,String> matchesPattern = (pattern,value) -> Try.success(value)
			.andThen(v -> matchesPattern(v,pattern))
			.get();
	static Function1<Long,Long> isPositive = v -> { isTrue(v >= 0); return v; };
	static Function2<Long,Long,Long> isGreaterThen = (m, v) -> { isTrue(v >= m); return v; };
	static Function1<String,Long> toLong = Long::parseLong;
	static Function1<String,String> toUpperCase = v -> v.toUpperCase();
}
