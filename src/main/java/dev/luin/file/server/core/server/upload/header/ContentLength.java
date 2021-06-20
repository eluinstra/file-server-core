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
import lombok.NonNull;
import lombok.Value;

@Value
public class ContentLength implements ValueObject<Long>
{
	public static final ContentLength ZERO = new ContentLength(0L);
	public static final Function1<ContentLength,Either<UploadException,ContentLength>> equalsZero = v -> v.equals(ZERO) ? Either.right(v) : Either.left(UploadException.invalidContentLength());
	private static final Function1<String,Either<String,String>> checkLength = inclusiveBetween.apply(0L,19L);
	private static final Function1<String,Either<String,String>> checkPattern = matchesPattern.apply("^[0-9]+$");
	private static final Function1<String,Either<String,Long>> validateAndTransform =
			(contentLength) -> Either.<String,String>right(contentLength).flatMap(checkLength).flatMap(checkPattern).map(toLong)/*.flatMap(isPositive)*/;
	public static final String HEADER_NAME = "Content-Length";
	@NonNull
	Long value;
	
	public static Option<ContentLength> of(@NonNull final UploadRequest request)
	{
		return Option.of(request.getHeader(HEADER_NAME)).map(v -> new ContentLength(v));
	}

	public static Either<UploadException,ContentLength> fromNullable(@NonNull final UploadRequest request)
	{
		return Option.of(request.getHeader(HEADER_NAME))
				.toEither(() -> UploadException.missingContentLength())
				.map(ContentLength::new);
	}

	public static Either<UploadException,UploadRequest> equalsZero(UploadRequest request)
	{
		return Either.<UploadException,UploadRequest>right(request)
				.flatMap(ContentLength::fromNullable)
				.filterOrElse(v -> v.equals(ZERO),s -> UploadException.invalidContentLength())
				.map(v -> request);
	}

	ContentLength(@NonNull String contentLength)
	{
		value = validateAndTransform.apply(contentLength)
				.getOrElseThrow(UploadException::invalidContentLength);
	}

	private ContentLength(@NonNull Long contentLength)
	{
		value = contentLength;
	}

	public void validate(@NonNull final UploadOffset uploadOffset, final Length length)
	{
		if (length != null)
			if (uploadOffset.getValue() + value > length.getValue())
				throw UploadException.invalidContentLength();
	}

	public Length toLength()
	{
		return new Length(value);
	}
}
