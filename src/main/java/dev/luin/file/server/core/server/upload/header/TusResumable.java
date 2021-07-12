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
import dev.luin.file.server.core.server.upload.UploadException;
import dev.luin.file.server.core.server.upload.UploadRequest;
import dev.luin.file.server.core.server.upload.UploadResponse;
import io.vavr.Function1;
import io.vavr.control.Either;
import io.vavr.control.Option;
import lombok.NonNull;

public class TusResumable
{
	public static final String HEADER_NAME = "Tus-Resumable";
	public static final String VALUE = TusVersion.VALUE;
	private static final Function1<String,Either<String,String>> checkLength = ValueObject.inclusiveBetween.apply(0L,19L);

	public static Either<UploadException,UploadRequest> validate(@NonNull final UploadRequest request)
	{
		return validate(request.getHeader(HEADER_NAME)).map(value -> request);
	}

	static Either<UploadException,String> validate(final String value)
	{
		return Option.of(value)
			.toEither("Value is null")
			.flatMap(checkLength)
			.mapLeft(e -> UploadException.invalidTusVersion())
			.filter(VALUE::equals)
			.getOrElse(Either.<UploadException,String>left(UploadException.invalidTusVersion()));
	}

	public static void write(@NonNull final UploadResponse response)
	{
		response.setHeader(HEADER_NAME,VALUE);
	}
}
