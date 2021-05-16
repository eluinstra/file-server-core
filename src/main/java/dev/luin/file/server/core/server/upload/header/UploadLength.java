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
import io.vavr.control.Option;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.val;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UploadLength extends TusHeader
{
	public static final String HEADER_NAME = "Upload-Length";

	public static Option<UploadLength> of(UploadRequest request)
	{
		val value = request.getHeader(HEADER_NAME);
		return value == null ? Option.<UploadLength>none() : of(value);
	}

	private static Option<UploadLength> of(String value)
	{
		val result = Option.of(LongHeaderValue.of(value,0,Long.MAX_VALUE)
						.map(v -> new UploadLength(v))
						.getOrElseThrow(() -> HttpException.invalidHeader(HEADER_NAME)));
		if (result.isDefined())
			result.filter(v -> v.getValue() <= TusMaxSize.getMaxSize()).getOrElseThrow(() -> HttpException.requestEntityTooLarge());
		return result;
	}

	@NonNull
	LongHeaderValue value;

	private UploadLength(LongHeaderValue value)
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
