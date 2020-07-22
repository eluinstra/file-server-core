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
package dev.luin.fs.core.server.upload.header;

import javax.servlet.http.HttpServletRequest;

import dev.luin.fs.core.http.StringHeaderValue;
import io.vavr.control.Option;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class XHTTPMethodOverride extends TusHeader
{
	private static final String HEADER_NAME = "X-HTTP-Method-Override";

	public static Option<XHTTPMethodOverride> of(HttpServletRequest request)
	{
		return request.getHeader(HEADER_NAME) == null
				? Option.<XHTTPMethodOverride>none()
				: StringHeaderValue.of(request.getHeader(HEADER_NAME)).map(v -> new XHTTPMethodOverride(v));
	}

	@NonNull
	StringHeaderValue value;

	public XHTTPMethodOverride(@NonNull StringHeaderValue value)
	{
		super(HEADER_NAME);
		this.value = value;
	}

	@Override
	public String toString()
	{
		return value.toString();
	}
}
