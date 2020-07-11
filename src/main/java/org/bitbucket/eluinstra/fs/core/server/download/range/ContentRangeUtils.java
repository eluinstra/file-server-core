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
package org.bitbucket.eluinstra.fs.core.server.download.range;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import io.vavr.collection.CharSeq;
import io.vavr.collection.List;
import io.vavr.collection.Seq;
import io.vavr.control.Option;
import io.vavr.control.Try;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.val;
import lombok.experimental.FieldDefaults;

public class ContentRangeUtils
{
	@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
	@AllArgsConstructor
	@Getter
	public enum ContentRangeHeader
	{
		ACCEPT_RANGES("Accept-Ranges"), CONTENT_RANGE("Content-Range"), IF_RANGE("If-Range"), RANGE("Range");
		
		String name;
	}

	@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
	@Getter
	private enum HTTPDate
	{
		IMF_FIXDATE(new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z",Locale.ENGLISH)),
		RFC_850(new SimpleDateFormat("EEEE, dd-MMM-yy HH:mm:ss z",Locale.ENGLISH)),
		ANSI_C(new SimpleDateFormat("EEE MMM  d HH:mm:ss yyyy",Locale.ENGLISH));

		DateFormat dateFormat;

		HTTPDate(@NonNull final DateFormat dateFormat)
		{
			dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
			this.dateFormat = dateFormat;
		}
	}

	private ContentRangeUtils()
	{
	}

	public static boolean isValid(final long fileLength, @NonNull final Seq<ContentRange> ranges)
	{
		return ranges.exists(r -> r.getFirst(fileLength) < fileLength);
	}

	public static Seq<ContentRange> filterValidRanges(final long fileLength, @NonNull final Seq<ContentRange> ranges)
	{
		return ranges.filter(r -> r.getFirst(fileLength) < fileLength);
	}

	public static int getHashCode(final long date)
	{
		return new Date(date).hashCode();
	}

	public static boolean validateIfRangeHeader(final String header, final long lastModified)
	{
		
		if (header == null)
			return true;
		else if (header.startsWith("\""))
		{
			val hashCode = new Integer(getHashCode(lastModified)).toString();
			val etag = header.substring(1, header.length() - 1);
			return hashCode.equals(etag);
		}
		else
			return Try.of(() -> lastModified <= getTime(header)).getOrElse(false);
	}

	public static long getTime(@NonNull final String header) throws ParseException
	{
		return Try.of(() -> HTTPDate.IMF_FIXDATE.dateFormat.parse(header).getTime())
				.orElse(Try.of(() -> HTTPDate.RFC_850.dateFormat.parse(header).getTime()))
				.orElse(Try.of(() -> HTTPDate.ANSI_C.dateFormat.parse(header).getTime()))
				.get();
	}

	public static Seq<ContentRange> parseRangeHeader(final String header)
	{
		if (header != null && header.startsWith("bytes"))
		{
			val byteRanges = header.substring("bytes=".length());
			val ranges = CharSeq.of(byteRanges).split(",");
			return ranges.flatMap(r -> createContentRange(r));
		}
		else
			return List.empty();
	}
	
	private static Option<ContentRange> createContentRange(@NonNull final CharSeq range)
	{
		val parts = range.split("-",2);
		return parts.headOption().flatMap(f -> ContentRange.of(f,parts.tail().headOption().getOrNull()));
	}

	public static String createContentRangeHeader(final long fileLength)
	{
		return "bytes */" + fileLength;
	}

	public static String createContentRangeHeader(@NonNull final ContentRange contentRange, final long fileLength)
	{
		return "bytes " + contentRange.getFirst(fileLength) + "-" + contentRange.getLast(fileLength) + "/" + fileLength;
	}
}
