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

import java.io.File;
import java.io.FileInputStream;

import org.apache.commons.codec.digest.DigestUtils;

import dev.luin.file.server.core.ValueObject;
import io.vavr.Function1;
import io.vavr.control.Either;
import io.vavr.control.Try;
import lombok.NonNull;
import lombok.Value;

@Value
public class Sha256Checksum implements ValueObject<String>
{
	private static final Function1<String,Either<String,String>> checkLength = inclusiveBetween.apply(32L,64L);
	private static final Function1<String,Either<String,String>> checkPattern = matchesPattern.apply("^[0-9A-F]*$");
	private static final Function1<String,Either<String,String>> validate = 
			checksum -> Either.<String,String>right(checksum).flatMap(checkLength).map(toUpperCase).flatMap(checkPattern);
	@NonNull
	String value;

	public static Sha256Checksum of(@NonNull final File file)
	{
		return Try.withResources(() -> new FileInputStream(file))
			.of(is -> new Sha256Checksum(DigestUtils.sha256Hex(is)))
			.getOrElseThrow(t -> new IllegalStateException(t));
	}

	public Sha256Checksum(@NonNull final String checksum)
	{
		value = validate.apply(checksum)
				.getOrElseThrow(s -> new IllegalArgumentException(s));
	}
}
