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

import javax.servlet.http.HttpServletRequest;

import dev.luin.file.server.core.ValueObject;
import dev.luin.file.server.core.file.FileLength;
import dev.luin.file.server.core.server.upload.UploadException;
import io.vavr.control.Option;
import io.vavr.control.Try;
import lombok.NonNull;
import lombok.Value;

@Value
public class ContentLength implements ValueObject<Long>
{
	public static final String HEADER_NAME = "Content-Length";
	Long value;

	public static Option<ContentLength> of(HttpServletRequest request)
	{
		return of(request.getHeader(HEADER_NAME));
	}

	static Option<ContentLength> of(String value)
	{
		return Option.of(value).map(v -> new ContentLength(v));
	}

	@SuppressWarnings("unchecked")
	public ContentLength(@NonNull final String contentLength)
	{
		value = Try.success(contentLength)
				.andThen(v -> inclusiveBetween(0,19,v.length()))
				.andThen(v -> matchesPattern(v,"^[0-9]*$"))
				.map(Long::parseLong)
//				.andThen(v -> isTrue(0 <= v && v <= Long.MAX_VALUE))
				.mapFailure(Case($(),UploadException::invalidContentLength))
				.get();
	}

	public void assertEquals(long expectedValue)
	{
		if (!value.equals(expectedValue))
			throw UploadException.invalidContentLength();
	}

	public void validate(@NonNull UploadOffset uploadOffset, FileLength fileLength)
	{
		if (fileLength != null)
			if (uploadOffset.getValue() + value > fileLength.getValue())
				throw UploadException.invalidContentLength();
	}

	public FileLength toFileLength()
	{
		return new FileLength(value);
	}
}
