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
package dev.luin.file.server.core.server.upload.header;

import static dev.luin.file.server.core.ValueObject.inclusiveBetween;
import static dev.luin.file.server.core.ValueObject.isNotNull;
import static dev.luin.file.server.core.ValueObject.matchesPattern;
import static dev.luin.file.server.core.ValueObject.safeToLong;
import static dev.luin.file.server.core.server.upload.UploadException.invalidContentLength;
import static io.vavr.control.Try.failure;
import static io.vavr.control.Try.success;

import dev.luin.file.server.core.ValueObject;
import dev.luin.file.server.core.file.Length;
import dev.luin.file.server.core.server.upload.UploadException;
import dev.luin.file.server.core.server.upload.UploadRequest;
import io.vavr.control.Option;
import io.vavr.control.Try;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.Value;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ContentLength implements ValueObject<Long>
{
	public static final String HEADER_NAME = "Content-Length";
	public static final ContentLength ZERO = new ContentLength(0L);

	@NonNull
	Long value;
	
	public static Try<Option<ContentLength>> of(@NonNull final UploadRequest request)
	{
		return of(request.getHeader(HEADER_NAME));
	}

	static Try<Option<ContentLength>> of(String contentLength)
	{
		return contentLength == null
				? success(Option.none())
				: validateAndTransform(contentLength)
						.map(ContentLength::new)
						.toTry(UploadException::invalidContentLength)
						.map(Option::some);
	}

	private static Try<Long> validateAndTransform(String contentLength)
	{
		return success(contentLength)
				.flatMap(isNotNull())
				.flatMap(inclusiveBetween(0L,19L))
				.flatMap(matchesPattern("^[0-9]+$"))
				.flatMap(safeToLong())
				/*.flatMap(isPositive())*/;
	}

	public static Try<UploadRequest> equalsEmptyOrZero(UploadRequest request)
	{
		return success(request)
				.flatMap(ContentLength::of)
				.filterTry(contentLength -> contentLength.getOrElse(ZERO).equals(ZERO),contentLength -> invalidContentLength())
				.map(contentLength -> request);
	}

	public Try<ContentLength> validate(@NonNull final UploadOffset uploadOffset, final Length length)
	{
		return (length != null) && (uploadOffset.getValue() + value > length.getValue())
				? failure(invalidContentLength())
				: success(this);
	}

	public Length toLength()
	{
		return new Length(value);
	}
}
