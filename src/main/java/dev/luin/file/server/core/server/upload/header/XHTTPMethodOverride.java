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
import static org.apache.commons.lang3.Validate.matchesPattern;

import dev.luin.file.server.core.server.upload.UploadRequest;
import io.vavr.control.Option;
import lombok.NonNull;

public class XHTTPMethodOverride
{
	private static final String HEADER_NAME = "X-HTTP-Method-Override";

	public static Option<String> get(@NonNull final UploadRequest request)
	{
		return get(request.getHeader(HEADER_NAME));
	}

	private static Option<String> get(final String value)
	{
		return Option.of(value)
				.toTry()
				.andThenTry(v -> inclusiveBetween(0,20,v.length()))
				.andThenTry(v -> matchesPattern(v,"^[A-Z]*$"))
				.toOption();
	}
}
