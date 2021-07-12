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

import dev.luin.file.server.core.ValueObject;
import dev.luin.file.server.core.file.Length;
import dev.luin.file.server.core.server.upload.UploadException;
import dev.luin.file.server.core.server.upload.UploadRequest;
import dev.luin.file.server.core.server.upload.UploadResponse;
import io.vavr.Function1;
import io.vavr.control.Either;
import io.vavr.control.Option;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.Value;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UploadOffset implements ValueObject<Long>
{
	public static final String HEADER_NAME = "Upload-Offset";
	private static final Function1<String,Either<String,String>> checkLength = inclusiveBetween.apply(0L,19L);
	private static final Function1<String,Either<String,String>> checkPattern = matchesPattern.apply("^[0-9]+$");
	private static final Function1<String,Either<String,Long>> validateAndTransform =
			(uploadOffset) -> Either.<String,String>right(uploadOffset)
				.flatMap(isNotNull)
				.flatMap(checkLength)
				.flatMap(checkPattern)
				.flatMap(v -> safeToLong.apply(v)
						.map(Either::<String,Long>right)
						.getOrElse(Either.left("Invalid number")))
				/*.flatMap(isPositive)*/;
	@NonNull
	Long value;

	public static Either<UploadException,UploadOffset> of(@NonNull final UploadRequest request)
	{
		return of(request.getHeader(HEADER_NAME));
	}

	static Either<UploadException,UploadOffset> of(final String value)
	{
		return Option.of(value)
				.toEither(UploadException::missingUploadOffset)
				.flatMap(v -> validateAndTransform.apply(v).mapLeft(e -> UploadException.invalidUploadOffset()))
				.map(UploadOffset::new);
	}

	public static void write(@NonNull final UploadResponse response, @NonNull final Length length)
	{
		response.setHeader(HEADER_NAME,length.getStringValue());
	}

	public Either<UploadException,UploadOffset> validateFileLength(@NonNull final Length length)
	{
		return length.equals(new Length(value)) ? Either.right(this) : Either.left(UploadException.invalidUploadOffset());
	}
}
