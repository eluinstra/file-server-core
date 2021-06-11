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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneOffset;
import java.util.Date;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import dev.luin.file.server.core.file.Length;
import io.vavr.collection.List;
import io.vavr.control.Option;
import lombok.AccessLevel;
import lombok.val;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level=AccessLevel.PRIVATE)
@TestInstance(value = Lifecycle.PER_CLASS)
public class ContentRangeTest
{
	Option<Long> expectedTime = Option.of(Date.from(LocalDateTime.of(1994, Month.NOVEMBER, 6, 8, 49, 37).toInstant(ZoneOffset.UTC)).getTime());

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

	@Test
	public void testContentRange()
	{
		val fileLength = new Length(10000L);
		val range = ContentRangeUtils.parseRangeHeader("bytes=0-499").get(0);
		assertEquals(0L,range.getFirst(fileLength));
		assertEquals(499L,range.getLast(fileLength));
		assertEquals(new Length(500L),range.getLength(fileLength));
		val validRanges = ContentRangeUtils.filterValidRanges(fileLength,List.of(range));
		assertEquals(1,validRanges.size());
		assertEquals("bytes 0-499/10000",range.createContentRangeValue(fileLength));
	}

	@Test
	public void testContentRange1()
	{
		val fileLength = new Length(10000L);
		val range = ContentRangeUtils.parseRangeHeader("bytes=500-999").get(0);
		assertEquals(500L,range.getFirst(fileLength));
		assertEquals(999L,range.getLast(fileLength));
		assertEquals(new Length(500L),range.getLength(fileLength));
		val validRanges = ContentRangeUtils.filterValidRanges(fileLength,List.of(range));
		assertEquals(1,validRanges.size());
		assertEquals("bytes 500-999/10000",range.createContentRangeValue(fileLength));
	}

	@Test
	public void testContentRange2()
	{
		val fileLength = new Length(10000L);
		val range = ContentRangeUtils.parseRangeHeader("bytes=-500").get(0);
		assertEquals(9500L,range.getFirst(fileLength));
		assertEquals(9999L,range.getLast(fileLength));
		assertEquals(new Length(500L),range.getLength(fileLength));
		val validRanges = ContentRangeUtils.filterValidRanges(fileLength,List.of(range));
		assertEquals(1,validRanges.size());
		assertEquals("bytes 9500-9999/10000",range.createContentRangeValue(fileLength));
	}

	@Test
	public void testContentRange3()
	{
		val fileLength = new Length(10000L);
		val range = ContentRangeUtils.parseRangeHeader("bytes=9500-").get(0);
		assertEquals(9500L,range.getFirst(fileLength));
		assertEquals(9999L,range.getLast(fileLength));
		assertEquals(new Length(500L),range.getLength(fileLength));
		val validRanges = ContentRangeUtils.filterValidRanges(fileLength,List.of(range));
		assertEquals(1,validRanges.size());
		assertEquals("bytes 9500-9999/10000",range.createContentRangeValue(fileLength));
	}

	@Test
	public void testContentRange4()
	{
		val fileLength = new Length(10000L);
		val range = ContentRangeUtils.parseRangeHeader("bytes=0-0").get(0);
		assertEquals(0L,range.getFirst(fileLength));
		assertEquals(0L,range.getLast(fileLength));
		assertEquals(new Length(1L),range.getLength(fileLength));
		val validRanges = ContentRangeUtils.filterValidRanges(fileLength,List.of(range));
		assertEquals(1,validRanges.size());
		assertEquals("bytes 0-0/10000",range.createContentRangeValue(fileLength));
	}

	@Test
	public void testContentRange5()
	{
		val fileLength = new Length(10000L);
		val range = ContentRangeUtils.parseRangeHeader("bytes=-1").get(0);
		assertEquals(9999L,range.getFirst(fileLength));
		assertEquals(9999L,range.getLast(fileLength));
		assertEquals(new Length(1L),range.getLength(fileLength));
		val validRanges = ContentRangeUtils.filterValidRanges(fileLength,List.of(range));
		assertEquals(1,validRanges.size());
		assertEquals("bytes 9999-9999/10000",range.createContentRangeValue(fileLength));
	}

	@Test
	public void testContentRange6()
	{
		val fileLength = new Length(1L);
		val range = ContentRangeUtils.parseRangeHeader("bytes=0-1").get(0);
		assertEquals(0L,range.getFirst(fileLength));
		assertEquals(0L,range.getLast(fileLength));
		assertEquals(new Length(1L),range.getLength(fileLength));
		val validRanges = ContentRangeUtils.filterValidRanges(fileLength,List.of(range));
		assertEquals(1,validRanges.size());
		assertEquals("bytes 0-0/1",range.createContentRangeValue(fileLength));
	}

	@Test
	public void testContentRange7()
	{
		val fileLength = new Length(1000L);
		val range = ContentRangeUtils.parseRangeHeader("bytes=-1500").get(0);
		assertEquals(0L,range.getFirst(fileLength));
		assertEquals(999L,range.getLast(fileLength));
		assertEquals(fileLength,range.getLength(fileLength));
		val validRanges = ContentRangeUtils.filterValidRanges(fileLength,List.of(range));
		assertEquals(1,validRanges.size());
		assertEquals("bytes 0-999/1000",range.createContentRangeValue(fileLength));
	}

	@Test
	public void testContentRange8()
	{
		val range = ContentRangeUtils.parseRangeHeader("bytes=1500-").get(0);
		val validRanges = ContentRangeUtils.filterValidRanges(new Length(1000L),List.of(range));
		assertEquals(0,validRanges.size());
	}

	@Test
	public void testContentRange9()
	{
		assertEquals(0,ContentRangeUtils.parseRangeHeader("bytes=-").size());
	}

	@Test
	public void testContentRange10()
	{
		assertEquals(2,ContentRangeUtils.parseRangeHeader("bytes=0-0,-1").size());
	}

	@Test
	public void testContentRange11()
	{
		assertThrows(IllegalArgumentException.class,() -> ContentRangeUtils.parseRangeHeader("bytes=10-0"));
	}

}
