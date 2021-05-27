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

import static io.vavr.API.$;
import static io.vavr.API.Case;

import javax.servlet.http.HttpServletRequest;

import dev.luin.file.server.core.http.LongHeaderValue;
import dev.luin.file.server.core.server.upload.UploadException;
import io.vavr.control.Option;
import io.vavr.control.Try;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ContentLength
{
	public static final String HEADER_NAME = "Content-Length";

	public static Option<Long> get(HttpServletRequest request)
	{
		return getTry(request.getHeader(HEADER_NAME)).get();
	}

	private static Try<Option<Long>> getTry(String value)
	{
		return getTry(LongHeaderValue.getOptional(value,0,Long.MAX_VALUE));
	}

	@SuppressWarnings("unchecked")
	private static Try<Option<Long>> getTry(Try<Option<Long>> value)
	{
		return value.mapFailure(Case($(),t -> UploadException.invalidContentLength()));
	}

	public static void zeroValueValidation(HttpServletRequest request)
	{
		getZeroValue(request).get();
	}

	private static Try<Long> getZeroValue(HttpServletRequest request)
	{
		return getZeroValue(LongHeaderValue.getOptional(request.getHeader(HEADER_NAME),0,0));
	}

	@SuppressWarnings("unchecked")
	private static Try<Long> getZeroValue(Try<Option<Long>> value)
	{
		return value
				.mapFailure(Case($(),t -> UploadException.invalidContentLength()))
				.mapTry(v -> v.getOrElseThrow(() -> UploadException.missingContentLength()));
	}

}
