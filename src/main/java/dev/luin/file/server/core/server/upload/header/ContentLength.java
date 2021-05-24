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

import javax.servlet.http.HttpServletRequest;

import dev.luin.file.server.core.http.LongHeaderValue;
import dev.luin.file.server.core.server.upload.UploadException;
import io.vavr.control.Option;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.val;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ContentLength extends TusHeader
{
	public static final String HEADER_NAME = "Content-Length";

	public static void hasValueZeroValidation(HttpServletRequest request)
	{
		of(request)
				.toTry(() -> UploadException.missingContentLength())
				.filterTry(l -> l.getValue() == 0, () -> UploadException.invalidContentLength())
				.get();
	}

	public static Option<ContentLength> of(HttpServletRequest request)
	{
		val value = request.getHeader(HEADER_NAME);
		return value == null ? Option.none() : of(value);
	}

	private static Option<ContentLength> of(@NonNull String value)
	{
		return Option.of(LongHeaderValue.of(value,0,Long.MAX_VALUE)
				.map(v -> new ContentLength(v))
				.getOrElseThrow(() -> UploadException.invalidContentLength()));
	}

	@NonNull
	LongHeaderValue value;

	private ContentLength(@NonNull LongHeaderValue value)
	{
		super(HEADER_NAME);
		this.value = value;
	}

	public Long getValue()
	{
		return value.getValue();
	}

	@Override
	public String toString()
	{
		return value.toString();
	}
}
