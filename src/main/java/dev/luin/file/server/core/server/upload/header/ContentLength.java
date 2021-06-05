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

import dev.luin.file.server.core.ValueObjectOptional;
import dev.luin.file.server.core.file.FileLength;
import dev.luin.file.server.core.server.upload.UploadException;
import io.vavr.control.Option;
import io.vavr.control.Try;
import lombok.Value;

@Value
public class ContentLength implements ValueObjectOptional<Long>
{
	public static final String HEADER_NAME = "Content-Length";
	Option<Long> value;

	public static ContentLength of(HttpServletRequest request)
	{
		return new ContentLength(request.getHeader(HEADER_NAME));
	}

	@SuppressWarnings("unchecked")
	public ContentLength(String contentLength)
	{
		value = Try.success(Option.of(contentLength))
				.andThenTry(t -> t.peek(v -> inclusiveBetween(0,19,v.length())))
				.andThenTry(t -> t.peek(v -> matchesPattern(v,"^[0-9]*$")))
				.mapTry(t -> t.map(Long::parseLong))
//				.andThenTry(t -> t.peek(v -> isTrue(0 <= v && v <= Long.MAX_VALUE)))
				.mapFailure(Case($(),UploadException::invalidContentLength))
				.get();
	}

	public void assertEquals(long expectedValue)
	{
		Option.of(value)
				.toTry(UploadException::missingContentLength)
				.filterTry(Option.of(expectedValue)::equals,UploadException::invalidContentLength)
				.get();
	}

	public void validate(UploadOffset uploadOffset, FileLength fileLength)
	{
		filter(v -> fileLength != null)
				.filter(v -> uploadOffset.getValue() + v <= fileLength.getOrElse(0L))
				.getOrElseThrow(UploadException::invalidContentLength);
	}

	public FileLength toFileLength()
	{
		return new FileLength(value);
	}
}
