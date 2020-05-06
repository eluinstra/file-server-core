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
package org.bitbucket.eluinstra.fs.core.server.range;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.TimeZone;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

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

	public static boolean isValid(final long fileLength, @NonNull final List<ContentRange> ranges)
	{
		return ranges.stream()
				.anyMatch(r -> r.getFirst(fileLength) < fileLength);
	}

	public static List<ContentRange> filterValidRanges(final long fileLength, @NonNull final List<ContentRange> ranges)
	{
		return Collections.unmodifiableList(ranges.stream()
				.filter(r -> r.getFirst(fileLength) < fileLength)
				.collect(Collectors.toList()));
	}

	public static int getHashCode(final long date)
	{
		return new Date(date).hashCode();
	}

	public static boolean validateIfRangeHeader(@NonNull final String header, final long lastModified)
	{
		
		if (header != null)
		{
			if (header.startsWith("\""))
			{
				val hashCode = new Integer(getHashCode(lastModified)).toString();
				val etag = header.substring(1, header.length() - 1);
				return hashCode.equals(etag);
			}
			else
			{
				try
				{
					val time = getTime(header);
					return lastModified <= time;
				}
				catch (ParseException e)
				{
				}
			}
		}
		return false;
	}

	public static long getTime(@NonNull final String header) throws ParseException
	{
		try
		{
			return HTTPDate.IMF_FIXDATE.dateFormat.parse(header).getTime();
		}
		catch (ParseException e)
		{
			try
			{
				return HTTPDate.RFC_850.dateFormat.parse(header).getTime();
			}
			catch (ParseException e1)
			{
				try
				{
					return HTTPDate.ANSI_C.dateFormat.parse(header).getTime();
				}
				catch (ParseException e2)
				{
					throw e;
				}
			}
		}
	}

	public static List<ContentRange> parseRangeHeader(final String header)
	{
		if (header != null && header.startsWith("bytes"))
		{
			val byteRanges = header.substring("bytes=".length());
			val ranges = StringUtils.split(byteRanges,",");
			return Collections.unmodifiableList(Arrays.stream(ranges)
				.map(r -> createContentRange(r))
				.filter(Optional::isPresent)
				.map(Optional::get)
				.collect(Collectors.toList()));
		}
		return Collections.emptyList();
	}
	
	private static Optional<ContentRange> createContentRange(@NonNull final String range)
	{
		val r = StringUtils.splitPreserveAllTokens(range,"-");
		val first = StringUtils.isEmpty(r[0]) ? null : Long.parseLong(r[0]);
		val last = StringUtils.isEmpty(r[1]) ? null : Long.parseLong(r[1]);
		val result = (first != null || last != null) ? ContentRange.of(first,last) : null;
		return Optional.ofNullable(result);
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
