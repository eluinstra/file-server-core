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
package dev.luin.file.server.core.server.download.range;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneOffset;
import java.util.Date;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import lombok.AccessLevel;
import lombok.val;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level=AccessLevel.PRIVATE)
@TestInstance(value = Lifecycle.PER_CLASS)
public class ContentRangeTest
{
	long expectedTime = Date.from(LocalDateTime.of(1994, Month.NOVEMBER, 6, 8, 49, 37).toInstant(ZoneOffset.UTC)).getTime();

	@Test
	public void testHTTPDate_IMF_FIXDATE() throws ParseException
	{
		val actualTime = ContentRangeUtils.getTime("Sun, 06 Nov 1994 08:49:37 GMT");
		assertEquals(expectedTime,actualTime);
	}

	@Test
	public void testHTTPDate_RFC_850() throws ParseException
	{
		val actualTime = ContentRangeUtils.getTime("Sunday, 06-Nov-94 08:49:37 GMT");
		assertEquals(expectedTime,actualTime);
	}

	@Test
	public void testHTTPDate_ANSI_C() throws ParseException
	{
		val actualTime = ContentRangeUtils.getTime("Sun Nov  6 08:49:37 1994");
		assertEquals(expectedTime,actualTime);
	}

}
