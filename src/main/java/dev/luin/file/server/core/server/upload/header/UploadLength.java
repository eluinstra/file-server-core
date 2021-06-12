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

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static org.apache.commons.lang3.Validate.inclusiveBetween;
import static org.apache.commons.lang3.Validate.matchesPattern;

import java.util.function.Supplier;

import dev.luin.file.server.core.ValueObject;
import dev.luin.file.server.core.file.Length;
import dev.luin.file.server.core.server.upload.UploadException;
import dev.luin.file.server.core.server.upload.UploadRequest;
import io.vavr.control.Option;
import io.vavr.control.Try;
import lombok.NonNull;
import lombok.Value;

@Value
public class UploadLength implements ValueObject<Long>
{
	public static final String HEADER_NAME = "Upload-Length";
	@NonNull
	Long value;

	public static Option<UploadLength> of(@NonNull final UploadRequest request, final TusMaxSize maxSize)
	{
		return of(request.getHeader(HEADER_NAME),maxSize,() -> UploadDeferLength.isDefined(request));
	}

	static Option<UploadLength> of(final String value, final TusMaxSize maxSize, @NonNull final Supplier<Boolean> isUploadDeferLengthDefined)
	{
		return Option.of(value)
				.map(v -> new UploadLength(v))
				.onEmpty(() -> {
					if (!isUploadDeferLengthDefined.get())
						throw UploadException.missingUploadLength();
				})
				.filter(v -> (maxSize == null ? true : v.getValue() <= maxSize.getValue()))
				.onEmpty(UploadException::fileTooLarge);
	}

	@SuppressWarnings("unchecked")
	private UploadLength(@NonNull final String uploadLength)
	{
		value = Try.success(uploadLength)
				.andThen(v -> inclusiveBetween(0,19,v.length()))
				.andThen(v -> matchesPattern(v,"^[0-9]*$"))
				.map(v -> Long.parseLong(v))
//				.andThen(v -> isTrue(0 <= v && v <= Long.MAX_VALUE))
				.mapFailure(Case($(),UploadException::invalidContentLength))
				.get();
	}

	public Length toFileLength()
	{
		return new Length(value);
	}
}
