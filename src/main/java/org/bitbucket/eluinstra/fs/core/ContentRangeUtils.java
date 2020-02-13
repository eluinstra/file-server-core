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
package org.bitbucket.eluinstra.fs.core;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.TimeZone;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.bitbucket.eluinstra.fs.core.model.ContentRange;

import lombok.Getter;
import lombok.NonNull;

public class ContentRangeUtils
{
	public enum ContentRangeHeader
	{
		ACCEPT_RANGES("Accept-Ranges"), CONTENT_RANGE("Content-Range"), IF_RANGE("If-Range"), RANGE("Range");
		
		@Getter
		private String name;

		private ContentRangeHeader(String name)
		{
			this.name = name;
		}
	}

	private enum HTTPDate
	{
		IMF_FIXDATE(new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z",Locale.ENGLISH)),
		RFC_850(new SimpleDateFormat("EEEE, dd-MMM-yy HH:mm:ss z",Locale.ENGLISH)),
		ANSI_C(new SimpleDateFormat("EEE MMM  d HH:mm:ss yyyy",Locale.ENGLISH));

		@Getter
		private DateFormat dateFormat;

		HTTPDate(DateFormat dateFormat)
		{
			dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
			this.dateFormat = dateFormat;
		}
	}

	private ContentRangeUtils()
	{
	}

	public static boolean isValid(long fileLength, @NonNull List<ContentRange> ranges)
	{
		return ranges.stream()
				.anyMatch(r -> r.getFirst(fileLength) < fileLength);
	}

	public static List<ContentRange> filterValidRanges(long fileLength, @NonNull List<ContentRange> ranges)
	{
		return ranges.stream()
				.filter(r -> r.getFirst(fileLength) < fileLength)
				.collect(Collectors.toList());
	}

	public static int getHashCode(long date)
	{
		return new Date(date).hashCode();
	}

	public static boolean validateIfRangeHeader(String header, long lastModified)
	{
		
		if (header != null)
		{
			if (header.startsWith("\""))
			{
				String hashCode = new Integer(getHashCode(lastModified)).toString();
				String etag = header.substring(1, header.length() - 1);
				return hashCode.equals(etag);
			}
			else
			{
				try
				{
					long time = getTime(header);
					return lastModified <= time;
				}
				catch (ParseException e)
				{
				}
			}
		}
		return false;
	}

	public static long getTime(String header) throws ParseException
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

	public static List<ContentRange> parseRangeHeader(@NonNull String header)
	{
		List<ContentRange> result = new ArrayList<>();
		if (header != null && header.startsWith("bytes"))
		{
			header = header.substring("bytes=".length());
			String[] ranges = StringUtils.split(header,",");
			result = Arrays.stream(ranges)
				.map(r -> createContentRange(r))
				.filter(Optional::isPresent)
				.map(Optional::get)
				.collect(Collectors.toList());
		}
		return result;
	}
	
	private static Optional<ContentRange> createContentRange(@NonNull String range)
	{
		String[] r = StringUtils.splitPreserveAllTokens(range,"-");
		Long first = StringUtils.isEmpty(r[0]) ? null : Long.parseLong(r[0]);
		Long last = StringUtils.isEmpty(r[1]) ? null : Long.parseLong(r[1]);
		ContentRange result = (first != null || last != null) ? new ContentRange(first,last) : null;
		return Optional.ofNullable(result);
	}

}
