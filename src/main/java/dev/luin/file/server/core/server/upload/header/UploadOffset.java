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
import static io.vavr.Predicates.instanceOf;
import static org.apache.commons.lang3.Validate.inclusiveBetween;
import static org.apache.commons.lang3.Validate.isTrue;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dev.luin.file.server.core.ValueObject;
import dev.luin.file.server.core.server.upload.UploadException;
import io.vavr.control.Option;
import lombok.Value;

@Value
public class UploadOffset implements ValueObject<Long>
{
	public static final String HEADER_NAME = "Upload-Offset";
	Long value;

	public static UploadOffset of(HttpServletRequest request)
	{
		return new UploadOffset(request.getHeader(HEADER_NAME));
	}

	public static void write(HttpServletResponse response, Long fileLength)
	{
		response.setHeader(HEADER_NAME,fileLength.toString());
	}

	@SuppressWarnings("unchecked")
	public UploadOffset(String uploadOffset)
	{
		value = Option.of(uploadOffset)
				.toTry(() -> UploadException.missingUploadOffset())
				.andThenTry(v -> inclusiveBetween(0,19,v.length()))
				.andThenTry(v -> isTrue(v.matches("[0-9]+")))
				.mapTry(v -> Long.parseLong(v))
//				.peek(v -> isTrue(0 <= v && v <= Long.MAX_VALUE))
				.mapFailure(
						Case($(instanceOf(UploadException.class)), t -> t),
						Case($(), t -> UploadException.invalidUploadOffset()))
				.get();
	}

	public void validateFileLength(long fileLength)
	{
		if (fileLength != value)
			throw UploadException.invalidUploadOffset();
	}
}
