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
package dev.luin.file.server.core.file;

import dev.luin.file.server.core.ValueObject;
import io.vavr.Function1;
import io.vavr.control.Either;
import lombok.NonNull;
import lombok.Value;

@Value
public class ContentType implements ValueObject<String>
{
	public static final ContentType BINARY = new ContentType("application/octet-stream");
	public static final ContentType TEXT = new ContentType("text/plain");
	public static final ContentType DEFAULT = BINARY;
	private final Function1<String,Either<String,String>> checkLength = inclusiveBetween.apply(0L,127L + 80L + 20L);
	private final Function1<String,Either<String,String>> checkPattern = matchesPattern.apply("^.{1,63}/.{1,63}$");
	private final Function1<String,String> parseValue = value -> value.split(";")[0].trim();
	@NonNull
	String value;

	public ContentType(final String contentType)
	{
		value = Either.<String,String>right(contentType)
				.flatMap(isNotNull)
				.flatMap(checkLength)
				.flatMap(v -> Either.right(parseValue.apply(v)))
				.flatMap(checkPattern)
				.getOrElseThrow(e -> new IllegalArgumentException(e));
	}

	public boolean isBinary()
	{
		return !value.matches("^(text/.*|.*/xml)$");
	}
}
