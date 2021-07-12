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
package dev.luin.file.server.core.server.upload.header;

import static dev.luin.file.server.core.ValueObject.inclusiveBetween;
import static dev.luin.file.server.core.ValueObject.isNotNull;
import static dev.luin.file.server.core.ValueObject.matchesPattern;

import org.apache.commons.lang3.StringUtils;

import dev.luin.file.server.core.server.upload.UploadException;
import dev.luin.file.server.core.server.upload.UploadRequest;
import io.vavr.Function1;
import io.vavr.control.Either;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ContentType
{
	private static final Function1<String,Either<String,String>> checkLength = inclusiveBetween.apply(0L,127L + 80L + 20L);
	private static final Function1<String,Either<String,String>> checkPattern = matchesPattern.apply("^.{1,63}/.{1,63}$");
	private static final String HEADER_NAME = "Content-Type";
	private static final String VALUE = "application/offset+octet-stream";

	public static Either<UploadException,UploadRequest> validate(@NonNull final UploadRequest request)
	{
		return validate(request.getHeader(HEADER_NAME)).map(value -> request);
	}

	static Either<UploadException,String> validate(final String value)
	{
		return Either.<UploadException,String>right(value)
			.flatMap(v -> isNotNull.apply(v)
					.flatMap(checkLength)
					.mapLeft(e -> UploadException.invalidContentType()))
			.flatMap(ContentType::parseValue)
			.flatMap(v -> checkPattern.apply(v)
					.mapLeft(e -> UploadException.invalidContentType()))
			.filterOrElse(VALUE::equals,v -> UploadException.invalidContentType());
	}

	private static Either<UploadException,String> parseValue(final String value)
	{
		return value == null
				? Either.left(UploadException.missingContentType())
				: Either.<UploadException,String>right(value)
					.map(v -> v.split(";")[0])
					.map(String::trim)
					.filterOrElse(StringUtils::isNotEmpty,v -> UploadException.missingContentType());
	}
}
