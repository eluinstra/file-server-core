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

import dev.luin.file.server.core.http.HttpException;
import dev.luin.file.server.core.http.LongHeaderValue;
import dev.luin.file.server.core.server.upload.UploadRequest;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UploadOffset extends TusHeader
{
	public static final String HEADER_NAME = "Upload-Offset";

	public static UploadOffset of(UploadRequest request)
	{
		return of(request.getHeader(HEADER_NAME));
	}

	private static UploadOffset of(final String value)
	{
		return LongHeaderValue.of(value,0,Long.MAX_VALUE).map(v -> new UploadOffset(v))
				.getOrElseThrow(() -> HttpException.invalidHeader(HEADER_NAME));
	}

	public static UploadOffset of(Long value)
	{
		return LongHeaderValue.of(value,0L,Long.MAX_VALUE).map(v -> new UploadOffset(v)).getOrElseThrow(() -> new IllegalArgumentException(value + " is not a valid " + HEADER_NAME + "!"));
	}

	@NonNull
	LongHeaderValue value;

	private UploadOffset(@NonNull LongHeaderValue value)
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
