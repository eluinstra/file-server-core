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

import static org.apache.commons.lang3.Validate.inclusiveBetween;

import javax.servlet.http.HttpServletRequest;

import io.vavr.control.Option;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UploadDeferLength
{
	public static final String HEADER_NAME = "Upload-Defer-Length";

	public static boolean isDefined(HttpServletRequest request)
	{
		return isDefined(request.getHeader(HEADER_NAME));
	}

	static boolean isDefined(String value)
	{
		return Option.of(value)
				.toTry()
				.andThen(v -> inclusiveBetween(0,19,v.length()))
				.filter("1"::equals)
				.isSuccess();
	}
}
