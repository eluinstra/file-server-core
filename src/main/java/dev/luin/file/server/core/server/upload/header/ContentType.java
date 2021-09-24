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
import static io.vavr.control.Try.failure;
import static io.vavr.control.Try.success;

import org.apache.commons.lang3.StringUtils;

import dev.luin.file.server.core.server.upload.UploadException;
import dev.luin.file.server.core.server.upload.UploadRequest;
import io.vavr.control.Try;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ContentType
{
	private static final String HEADER_NAME = "Content-Type";
	private static final String VALUE = "application/offset+octet-stream";

	public static Try<UploadRequest> validate(@NonNull final UploadRequest request)
	{
		return validate(request.getHeader(HEADER_NAME)).map(value -> request);
	}

	static Try<String> validate(final String value)
	{
		return success(value)
			.flatMap(isNotNull())
			.flatMap(inclusiveBetween(0L,127L + 80L + 20L))
			.flatMap(ContentType::parseValue)
			.flatMap(matchesPattern("^.{1,63}/.{1,63}$"))
			.filter(VALUE::equals)
			.toTry(() -> UploadException.invalidContentType());
	}

	private static Try<String> parseValue(final String value)
	{
		return value == null
				? failure(UploadException.missingContentType())
				: success(value)
						.map(v -> v.split(";")[0])
						.map(String::trim)
						.filterTry(StringUtils::isNotEmpty,() -> UploadException.missingContentType());
	}
}
