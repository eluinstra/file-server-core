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

import static dev.luin.file.server.core.server.upload.UploadException.fileTooLarge;
import static dev.luin.file.server.core.server.upload.UploadException.missingUploadLength;
import static io.vavr.control.Either.left;
import static io.vavr.control.Either.right;
import static io.vavr.control.Option.none;

import java.util.function.Supplier;

import dev.luin.file.server.core.ValueObject;
import dev.luin.file.server.core.file.Length;
import dev.luin.file.server.core.server.upload.UploadException;
import dev.luin.file.server.core.server.upload.UploadRequest;
import io.vavr.control.Either;
import io.vavr.control.Option;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.Value;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UploadLength implements ValueObject<Long>
{
	public static final String HEADER_NAME = "Upload-Length";

	@NonNull
	Long value;

	public static Either<UploadException,Option<UploadLength>> of(@NonNull final UploadRequest request, final TusMaxSize maxSize)
	{
		return of(request.getHeader(HEADER_NAME),maxSize,() -> UploadDeferLength.isDefined(request));
	}

	static Either<UploadException,Option<UploadLength>> of(final String value, final TusMaxSize maxSize, @NonNull final Supplier<Boolean> isUploadDeferLengthDefined)
	{
		return value == null 
				? (isUploadDeferLengthDefined.get()
						? right(none())
						: left(missingUploadLength()))
				:	validateAndTransform(value)
						.map(UploadLength::new)
						.toEither(UploadException::invalidContentLength)
						.filterOrElse(uploadLength -> (maxSize == null ? true : uploadLength.getValue() <= maxSize.getValue()),uploadLength -> fileTooLarge())
						.map(Option::some);
	}

	private static Either<String, Long> validateAndTransform(String uploadLength)
	{
		return Either.<String, String>right(uploadLength)
				.flatMap(isNotNull)
				.flatMap(inclusiveBetween.apply(0L,19L))
				.flatMap(matchesPattern.apply("^[0-9]*$"))
				.flatMap(v -> safeToLong.apply(v)
						.map(Either::<String,Long>right)
						.getOrElse(left("Invalid number")))
				/*.flatMap(isPositive)*/;
	}

	public Length toFileLength()
	{
		return new Length(value);
	}
}
