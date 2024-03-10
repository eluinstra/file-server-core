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
package dev.luin.file.server.core.server.download.header;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import lombok.val;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
enum HttpDate
{
	IMF_FIXDATE("EEE, dd MMM yyyy HH:mm:ss z"), RFC_850("EEEE, dd-MMM-yy HH:mm:ss z"), ANSI_C("EEE MMM  d HH:mm:ss yyyy");

	private static final Locale LOCALE = Locale.ENGLISH;
	private static final TimeZone TIME_ZONE = TimeZone.getTimeZone("GMT");
	@NonNull
	String pattern;

	HttpDate(@NonNull final String pattern)
	{
		this.pattern = pattern;
	}

	public DateFormat getDateFormat()
	{
		val result = new SimpleDateFormat(pattern, LOCALE);
		result.setTimeZone(TIME_ZONE);
		return result;
	}
}
