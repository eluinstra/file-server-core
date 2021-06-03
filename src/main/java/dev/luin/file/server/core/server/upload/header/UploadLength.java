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
import static org.apache.commons.lang3.Validate.inclusiveBetween;
import static org.apache.commons.lang3.Validate.isTrue;

import java.util.function.Supplier;

import javax.servlet.http.HttpServletRequest;

import dev.luin.file.server.core.ValueObjectOptional;
import dev.luin.file.server.core.server.upload.UploadException;
import io.vavr.control.Option;
import io.vavr.control.Try;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.val;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UploadLength implements ValueObjectOptional<Long>
{
	public static final String HEADER_NAME = "Upload-Length";
	Option<Long> value;

	public static UploadLength of(HttpServletRequest request, TusMaxSize maxSize)
	{
		return new UploadLength(request.getHeader(HEADER_NAME),maxSize,() -> UploadDeferLength.isDefined(request));
	}

	@SuppressWarnings("unchecked")
	public UploadLength(String uploadLength, TusMaxSize maxSize, Supplier<Boolean> isUploadDeferLengthDefined)
	{
		val value = Try.success(Option.of(uploadLength))
				.andThenTry(t -> t.peek(v -> inclusiveBetween(0,19,v.length())))
				.andThenTry(t -> t.peek(v -> isTrue(v.matches("^[0-9]*$"))))
				.mapTry(t -> t.map(v -> Long.parseLong(v)))
//				.andThenTry(t -> t.peek(v -> isTrue(0 <= v && v <= Long.MAX_VALUE)))
				.mapFailure(Case($(), t -> UploadException.invalidContentLength()))
				.get()
				.onEmpty(() -> {
					if (!isUploadDeferLengthDefined.get())
						throw UploadException.missingUploadLength();
				});
		if (value.isDefined())
			value.filter(v -> maxSize.map(m -> v <= m).getOrElse(true))
				.getOrElseThrow(() -> UploadException.fileTooLarge());
		this.value = value;
	}

	@Override
	public Option<Long> getValue()
	{
		return value;
	}
}
