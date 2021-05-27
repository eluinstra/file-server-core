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

import java.util.function.Supplier;

import javax.servlet.http.HttpServletRequest;

import dev.luin.file.server.core.http.LongHeaderValue;
import dev.luin.file.server.core.http.StringHeaderValue;
import dev.luin.file.server.core.server.upload.UploadException;
import io.vavr.control.Option;
import lombok.AccessLevel;
import lombok.val;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UploadLength
{
	public static final String HEADER_NAME = "Upload-Length";

	public static Option<Long> get(HttpServletRequest request)
	{
		return get(request.getHeader(HEADER_NAME),() -> UploadDeferLength.isDefined(request));
	}

	private static Option<Long> get(String value, Supplier<Boolean> isUploadDeferLengthDefined)
	{
		val result = Option.of(value)
				.flatMap(v -> StringHeaderValue.get(v))
				.flatMap(v -> LongHeaderValue.getOptional(v,0,Long.MAX_VALUE).get())
				.onEmpty(() -> {
					if (!isUploadDeferLengthDefined.get())
						throw UploadException.missingUploadLength();
				});
				if (result.isDefined())
					result.filter(v -> TusMaxSize.getValue().map(m -> v <= m).getOrElse(true))
						.getOrElseThrow(() -> UploadException.fileTooLarge());
				return result;
	}
	
//	public static Try<Option<Long>> getOptional(HttpServletRequest request)
//	{
//		return get(request.getHeader(HEADER_NAME))
//				.recoverWith(NullPointerException.class, x -> {
//					if (!UploadDeferLength.isDefined(request))
//						return Try.failure(UploadException.missingUploadLength());
//					else
//						return Try.success(Option.none());
//				});
//	}
//	
//	public static Try<Option<Long>> get(String value)
//	{
//		return Option.of(value)
//				.flatMap(v -> IHeaderValue.parseValue(v))
//				.toTry(() -> new NullPointerException())
//				.flatMap(v -> LongHeaderValue.getOptional(v,1,Long.MAX_VALUE));
//	}
}
