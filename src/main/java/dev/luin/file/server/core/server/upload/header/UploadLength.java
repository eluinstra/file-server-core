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
import static dev.luin.file.server.core.ValueObject.safeToLong;
import static dev.luin.file.server.core.server.upload.UploadException.fileTooLarge;
import static dev.luin.file.server.core.server.upload.UploadException.missingUploadLength;
import static io.vavr.control.Option.none;
import static io.vavr.control.Try.failure;
import static io.vavr.control.Try.success;

import java.util.function.BooleanSupplier;

import dev.luin.file.server.core.ValueObject;
import dev.luin.file.server.core.file.Length;
import dev.luin.file.server.core.server.upload.UploadException;
import dev.luin.file.server.core.server.upload.UploadRequest;
import io.vavr.CheckedPredicate;
import io.vavr.control.Option;
import io.vavr.control.Try;
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

	public static Try<Option<UploadLength>> of(@NonNull final UploadRequest request, final TusMaxSize maxSize)
	{
		return of(request.getHeader(HEADER_NAME),maxSize,() -> UploadDeferLength.isDefined(request));
	}

	static Try<Option<UploadLength>> of(final String value, final TusMaxSize maxSize, @NonNull final BooleanSupplier isUploadDeferLengthDefined)
	{
		return value == null 
				? createResponse(isUploadDeferLengthDefined)
				:	validateAndTransform(value)
						.map(UploadLength::new)
						.toTry(UploadException::invalidContentLength)
						.filterTry(isValidUploadLength(maxSize),uploadLength -> fileTooLarge())
						.map(Option::some);
	}

	private static CheckedPredicate<UploadLength> isValidUploadLength(final TusMaxSize maxSize)
	{
		return uploadLength ->  maxSize == null ? true : uploadLength.getValue() <= maxSize.getValue();
	}

	private static Try<Option<UploadLength>> createResponse(final BooleanSupplier isUploadDeferLengthDefined)
	{
		return isUploadDeferLengthDefined.getAsBoolean()
				? success(none())
				: failure(missingUploadLength());
	}

	private static Try<Long> validateAndTransform(String uploadLength)
	{
		return success(uploadLength)
				.flatMap(isNotNull())
				.flatMap(inclusiveBetween(0L,19L))
				.flatMap(matchesPattern("^[0-9]*$"))
				.flatMap(safeToLong())
				/*.flatMap(isPositive())*/;
	}

	public Length toFileLength()
	{
		return new Length(value);
	}
}
