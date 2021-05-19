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
import javax.validation.constraints.NotNull;

import dev.luin.file.server.core.http.ConstHeaderValue;
import dev.luin.file.server.core.server.upload.UploadException;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;

public @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
class TusResumable extends TusHeader
{
	private static final String HEADER_NAME = "Tus-Resumable";
	private static final String DEFAULT_VALUE = TusVersion.get().toString();
	private static final TusResumable DEFAULT = ConstHeaderValue.of(DEFAULT_VALUE).map(v -> new TusResumable(v)).get();

	public static TusResumable get()
	{
		return DEFAULT;
	}

	public static void validate(HttpServletRequest request)
	{
		validate(request.getHeader(HEADER_NAME));
	}

	private static void validate(String value)
	{
		ConstHeaderValue.of(value,DEFAULT_VALUE).map(v -> new TusResumable(v))
				.getOrElseThrow(() -> UploadException.invalidTusVersion(TusVersion.get()));
	}

	@NotNull
	ConstHeaderValue value;

	private TusResumable(@NonNull ConstHeaderValue value)
	{
		super(HEADER_NAME);
		this.value = value;
	}

	public Tuple2<String,String> asTuple()
	{
		return Tuple.of(DEFAULT.getName(),DEFAULT.toString());
	}

	@Override
	public String toString()
	{
		return value.toString();
	}
}
