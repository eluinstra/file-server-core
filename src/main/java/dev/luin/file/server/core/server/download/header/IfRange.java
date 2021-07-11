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
package dev.luin.file.server.core.server.download.header;

import java.time.Instant;
import java.util.Date;

import dev.luin.file.server.core.ValueObject;
import dev.luin.file.server.core.server.download.DownloadException;
import dev.luin.file.server.core.server.download.DownloadRequest;
import io.vavr.control.Either;
import io.vavr.control.Option;
import io.vavr.control.Try;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.Value;
import lombok.val;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class IfRange implements ValueObject<Either<String,Date>>
{
	private final static String HEADER_NAME = "If-Range";
	@NonNull
	Either<String,Date> value;

	public static Either<DownloadException,Option<IfRange>> of(@NonNull final DownloadRequest request)
	{
		return of(request.getHeader(HEADER_NAME));
	}

	public static Either<DownloadException,Option<IfRange>> of(final String value)
	{
		if (value == null)
			return Either.right(Option.none());
		else if (value.startsWith("\""))
		{
			val eTag = value.substring(1, value.length() - 1);
			return Either.right(eTag.equals("*")
					? Option.none()
					: Option.some(new IfRange(Either.<String,Date>left(eTag)))
			);
		}
		else
			return getDate(value).map(d -> Option.some(new IfRange(Either.<String,Date>right(d))));
	}

	static Either<DownloadException,Date> getDate(@NonNull final String value)
	{
		return Try.of(() -> HttpDate.IMF_FIXDATE.getDateFormat().parse(value))
				.orElse(Try.of(() -> HttpDate.RFC_850.getDateFormat().parse(value)))
				.orElse(Try.of(() -> HttpDate.ANSI_C.getDateFormat().parse(value)))
				.toEither(DownloadException::invalidIfRange);
	}

	public boolean isValid(@NonNull final Instant lastModified)
	{
		if (value.isLeft())
		{
			val eTag = value.getLeft();
			val hashCode = new Integer(ETag.getHashCode(lastModified)).toString();
			return hashCode.equals(eTag);
		}
		else
		{
			val t = value.get().getTime();
			return lastModified.toEpochMilli() <= t;
		}
	}

}
