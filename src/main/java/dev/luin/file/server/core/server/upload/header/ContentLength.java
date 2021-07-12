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
import io.vavr.Function1;
import io.vavr.control.Either;
import io.vavr.control.Option;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.Value;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ContentLength implements ValueObject<Long>
{
	public static final ContentLength ZERO = new ContentLength(0L);
	private static final Function1<String,Either<String,String>> checkLength = inclusiveBetween.apply(0L,19L);
	private static final Function1<String,Either<String,String>> checkPattern = matchesPattern.apply("^[0-9]+$");
	private static final Function1<String,Either<String,Long>> validateAndTransform =
			contentLength -> Either.<String,String>right(contentLength)
					.flatMap(isNotNull)
					.flatMap(checkLength)
					.flatMap(checkPattern)
					.flatMap(v -> safeToLong.apply(v)
							.map(Either::<String,Long>right)
							.getOrElse(Either.left("Invalid number")))
					/*.flatMap(isPositive)*/;
	public static final String HEADER_NAME = "Content-Length";
	@NonNull
	Long value;
	
	public static Either<UploadException,ContentLength> of(@NonNull final UploadRequest request)
	{
		return Option.of(request.getHeader(HEADER_NAME))
				.toEither(UploadException::missingContentLength)
				.flatMap(ContentLength::of);
	}

	static Either<UploadException,ContentLength> of(String contentLength)
	{
		return validateAndTransform.apply(contentLength)
				.map(ContentLength::new)
				.toEither(UploadException::invalidContentLength);
	}

	public static Either<UploadException,UploadRequest> equalsZero(UploadRequest request)
	{
		return Either.<UploadException,UploadRequest>right(request)
				.flatMap(ContentLength::of)
				.filterOrElse(contentLength -> contentLength.equals(ZERO),contentLength -> UploadException.invalidContentLength())
				.map(contentLength -> request);
	}

	public Either<UploadException,ContentLength> validate(@NonNull final UploadOffset uploadOffset, final Length length)
	{
		return (length != null) && (uploadOffset.getValue() + value > length.getValue())
				? Either.left(UploadException.invalidContentLength())
				: Either.right(this);
	}

	public Length toLength()
	{
		return new Length(value);
	}
}
