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
package dev.luin.file.server.core.server.upload;

import java.util.function.Supplier;

import io.vavr.collection.List;
import io.vavr.control.Option;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
@Getter
public enum UploadMethod
{
	TUS_OPTIONS("OPTIONS"), FILE_INFO("HEAD"), CREATE_FILE("POST"), UPLOAD_FILE("PATCH"), DELETE_FILE("DELETE");
	
	@NonNull
	String httpMethod;
	
	public static Option<UploadMethod> getMethod(@NonNull final String method, @NonNull final Supplier<Option<String>> xHTTPMethodOverride)
	{
		return of(method)
				.map(m -> m.equals(CREATE_FILE) ? xHTTPMethodOverride.get().flatMap(UploadMethod::of).getOrElse(m) : m);
	}

	private static Option<UploadMethod> of(final String httpMethod)
	{
		return List.of(UploadMethod.values())
				.find(method -> method.httpMethod.equals(httpMethod));
	}
}
