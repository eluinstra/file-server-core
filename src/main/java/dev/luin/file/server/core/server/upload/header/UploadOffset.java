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

import static dev.luin.file.server.core.server.upload.UploadException.invalidUploadOffset;
import static dev.luin.file.server.core.ValueObject.*;
import static io.vavr.control.Try.failure;
import static io.vavr.control.Try.success;

import dev.luin.file.server.core.ValueObject;
import dev.luin.file.server.core.file.Length;
import dev.luin.file.server.core.server.upload.UploadException;
import dev.luin.file.server.core.server.upload.UploadRequest;
import dev.luin.file.server.core.server.upload.UploadResponse;
import io.vavr.control.Option;
import io.vavr.control.Try;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.Value;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UploadOffset implements ValueObject<Long>
{
	public static final String HEADER_NAME = "Upload-Offset";

	@NonNull
	Long value;

	public static Try<UploadOffset> of(@NonNull final UploadRequest request)
	{
		return of(request.getHeader(HEADER_NAME));
	}

	static Try<UploadOffset> of(final String value)
	{
		return Option.of(value)
				.toTry(UploadException::missingUploadOffset)
				.flatMap(v -> validateAndTransform(v).toTry(() -> invalidUploadOffset()))
				.map(UploadOffset::new);
	}

	private static Try<Long> validateAndTransform(String uploadOffset)
	{
		return success(uploadOffset)
				.flatMap(isNotNull())
				.flatMap(inclusiveBetween(0L,19L))
				.flatMap(matchesPattern("^[0-9]+$"))
				.flatMap(safeToLong())
				/*.flatMap(isPositive())*/;
	}

	public static void write(@NonNull final UploadResponse response, @NonNull final Length length)
	{
		response.setHeader(HEADER_NAME,length.getStringValue());
	}

	public Try<UploadOffset> validateFileLength(@NonNull final Length length)
	{
		return length.equals(new Length(value)) ? success(this) : failure(invalidUploadOffset());
	}
}
