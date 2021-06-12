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

import java.io.IOException;
import java.io.StringWriter;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import dev.luin.file.server.core.file.Length;
import io.vavr.collection.List;
import io.vavr.control.Try;
import lombok.AccessLevel;
import lombok.val;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level=AccessLevel.PRIVATE)
@TestInstance(value = Lifecycle.PER_CLASS)
public class ContentRangeTest
{
	@Test
	public void testContentRange()
	{
		val fileLength = new Length(10000L);
		val range = ContentRange.parseRangeHeader("bytes=0-499").get(0);
		assertEquals(0L,range.getFirst(fileLength));
		assertEquals(499L,range.getLast(fileLength));
		assertEquals(new Length(500L),range.getLength(fileLength));
		val validRanges = ContentRange.filterValidRanges(fileLength,List.of(range));
		assertEquals(1,validRanges.size());
		assertEquals("Content-Range: bytes 0-499/10000",writeFileLength(fileLength,range).toString());
	}

	private StringWriter writeFileLength(final Length fileLength, final Range range)
	{
		return Try.success(new StringWriter())
				.andThenTry(sw -> range.write(sw,fileLength))
				.getOrElseThrow(t -> new IllegalStateException());
	}

	@Test
	public void testContentRange1() throws IOException
	{
		val fileLength = new Length(10000L);
		val range = ContentRange.parseRangeHeader("bytes=500-999").get(0);
		assertEquals(500L,range.getFirst(fileLength));
		assertEquals(999L,range.getLast(fileLength));
		assertEquals(new Length(500L),range.getLength(fileLength));
		val validRanges = ContentRange.filterValidRanges(fileLength,List.of(range));
		assertEquals(1,validRanges.size());
		assertEquals("Content-Range: bytes 500-999/10000",writeFileLength(fileLength,range).toString());
	}

	@Test
	public void testContentRange2() throws IOException
	{
		val fileLength = new Length(10000L);
		val range = ContentRange.parseRangeHeader("bytes=-500").get(0);
		assertEquals(9500L,range.getFirst(fileLength));
		assertEquals(9999L,range.getLast(fileLength));
		assertEquals(new Length(500L),range.getLength(fileLength));
		val validRanges = ContentRange.filterValidRanges(fileLength,List.of(range));
		assertEquals(1,validRanges.size());
		assertEquals("Content-Range: bytes 9500-9999/10000",writeFileLength(fileLength,range).toString());
	}

	@Test
	public void testContentRange3()
	{
		val fileLength = new Length(10000L);
		val range = ContentRange.parseRangeHeader("bytes=9500-").get(0);
		assertEquals(9500L,range.getFirst(fileLength));
		assertEquals(9999L,range.getLast(fileLength));
		assertEquals(new Length(500L),range.getLength(fileLength));
		val validRanges = ContentRange.filterValidRanges(fileLength,List.of(range));
		assertEquals(1,validRanges.size());
		assertEquals("Content-Range: bytes 9500-9999/10000",writeFileLength(fileLength,range).toString());
	}

	@Test
	public void testContentRange4()
	{
		val fileLength = new Length(10000L);
		val range = ContentRange.parseRangeHeader("bytes=0-0").get(0);
		assertEquals(0L,range.getFirst(fileLength));
		assertEquals(0L,range.getLast(fileLength));
		assertEquals(new Length(1L),range.getLength(fileLength));
		val validRanges = ContentRange.filterValidRanges(fileLength,List.of(range));
		assertEquals(1,validRanges.size());
		assertEquals("Content-Range: bytes 0-0/10000",writeFileLength(fileLength,range).toString());
	}

	@Test
	public void testContentRange5()
	{
		val fileLength = new Length(10000L);
		val range = ContentRange.parseRangeHeader("bytes=-1").get(0);
		assertEquals(9999L,range.getFirst(fileLength));
		assertEquals(9999L,range.getLast(fileLength));
		assertEquals(new Length(1L),range.getLength(fileLength));
		val validRanges = ContentRange.filterValidRanges(fileLength,List.of(range));
		assertEquals(1,validRanges.size());
		assertEquals("Content-Range: bytes 9999-9999/10000",writeFileLength(fileLength,range).toString());
	}

	@Test
	public void testContentRange6()
	{
		val fileLength = new Length(1L);
		val range = ContentRange.parseRangeHeader("bytes=0-1").get(0);
		assertEquals(0L,range.getFirst(fileLength));
		assertEquals(0L,range.getLast(fileLength));
		assertEquals(new Length(1L),range.getLength(fileLength));
		val validRanges = ContentRange.filterValidRanges(fileLength,List.of(range));
		assertEquals(1,validRanges.size());
		assertEquals("Content-Range: bytes 0-0/1",writeFileLength(fileLength,range).toString());
	}

	@Test
	public void testContentRange7()
	{
		val fileLength = new Length(1000L);
		val range = ContentRange.parseRangeHeader("bytes=-1500").get(0);
		assertEquals(0L,range.getFirst(fileLength));
		assertEquals(999L,range.getLast(fileLength));
		assertEquals(fileLength,range.getLength(fileLength));
		val validRanges = ContentRange.filterValidRanges(fileLength,List.of(range));
		assertEquals(1,validRanges.size());
		assertEquals("Content-Range: bytes 0-999/1000",writeFileLength(fileLength,range).toString());
	}

	@Test
	public void testContentRange8()
	{
		val range = ContentRange.parseRangeHeader("bytes=1500-").get(0);
		val validRanges = ContentRange.filterValidRanges(new Length(1000L),List.of(range));
		assertEquals(0,validRanges.size());
	}

	@Test
	public void testContentRange9()
	{
		assertEquals(0,ContentRange.parseRangeHeader("bytes=-").size());
	}

	@Test
	public void testContentRange10()
	{
		assertEquals(2,ContentRange.parseRangeHeader("bytes=0-0,-1").size());
	}

	@Test
	public void testContentRange11()
	{
		assertThrows(IllegalArgumentException.class,() -> ContentRange.parseRangeHeader("bytes=10-0"));
	}

}
