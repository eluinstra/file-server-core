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

import static dev.luin.file.server.core.ValueObject.*;
import static io.vavr.control.Try.success;

import dev.luin.file.server.core.ValueObject;
import io.vavr.Function1;
import io.vavr.control.Try;
import lombok.NonNull;
import lombok.Value;

@Value
public class ContentType implements ValueObject<String>
{
	public static final ContentType BINARY = new ContentType("application/octet-stream");
	public static final ContentType TEXT = new ContentType("text/plain");
	public static final ContentType DEFAULT = BINARY;

	@NonNull
	String value;

	public ContentType(final String contentType)
	{
		value = validateAndTransform(contentType).get();
	}

	private static Try<String> validateAndTransform(final String contentType)
	{
		return success(contentType)
				.flatMap(isNotNull())
				.flatMap(inclusiveBetween(0L,127L + 80L + 20L))
				.flatMap(parseValue())
				.flatMap(matchesPattern("^.{1,63}/.{1,63}$"));
	}

	private static Function1<String,Try<String>> parseValue()
	{
		return value -> success(value.split(";")[0].trim());
	}

	public boolean isBinary()
	{
		return !value.matches("^(text/.*|.*/xml)$");
	}
}
