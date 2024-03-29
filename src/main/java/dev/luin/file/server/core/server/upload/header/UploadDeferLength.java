/*
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

import static dev.luin.file.server.core.ValueObject.inclusiveBetween;

import dev.luin.file.server.core.server.upload.UploadRequest;
import io.vavr.control.Option;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UploadDeferLength
{
	public static final String HEADER_NAME = "Upload-Defer-Length";

	public static boolean isDefined(@NonNull final UploadRequest request)
	{
		return isDefined(request.getHeader(HEADER_NAME));
	}

	static boolean isDefined(final String value)
	{
		return Option.of(value)
				.toTry()
				.flatMap(inclusiveBetween(1L,1L))
				.exists("1"::equals);
	}
}
