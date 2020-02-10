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
package org.bitbucket.eluinstra.fs;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;
import java.util.List;

import org.bitbucket.eluinstra.fs.model.ContentRange;
import org.bitbucket.eluinstra.fs.validation.ContentRangeParser;
import org.bitbucket.eluinstra.fs.validation.ContentRangeValidator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.mockito.MockitoAnnotations;

@TestInstance(value = Lifecycle.PER_CLASS)
public class ContentRangeTest
{
	@BeforeAll
	public void init() throws Exception
	{
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testContentRange()
	{
		long fileLength = 10000L;
		ContentRange range = ContentRangeParser.parseRangeHeader("bytes=0-499").get(0);
		assertEquals(0L,range.getFirst(fileLength));
		assertEquals(499L,range.getLast(fileLength));
		assertEquals(500L,range.getLength(fileLength));
		List<ContentRange> validRanges = ContentRangeValidator.filterValidRanges(fileLength,Arrays.asList(range));
		assertEquals(1,validRanges.size());
		assertEquals("bytes 0-499/10000",range.createContentRangeHeader(fileLength));
	}

	@Test
	public void testContentRange1()
	{
		long fileLength = 10000L;
		ContentRange range = ContentRangeParser.parseRangeHeader("bytes=500-999").get(0);
		assertEquals(500L,range.getFirst(fileLength));
		assertEquals(999L,range.getLast(fileLength));
		assertEquals(500L,range.getLength(fileLength));
		List<ContentRange> validRanges = ContentRangeValidator.filterValidRanges(fileLength,Arrays.asList(range));
		assertEquals(1,validRanges.size());
		assertEquals("bytes 500-999/10000",range.createContentRangeHeader(fileLength));
	}

	@Test
	public void testContentRange2()
	{
		long fileLength = 10000L;
		ContentRange range = ContentRangeParser.parseRangeHeader("bytes=-500").get(0);
		assertEquals(9500L,range.getFirst(fileLength));
		assertEquals(9999L,range.getLast(fileLength));
		assertEquals(500L,range.getLength(fileLength));
		List<ContentRange> validRanges = ContentRangeValidator.filterValidRanges(fileLength,Arrays.asList(range));
		assertEquals(1,validRanges.size());
		assertEquals("bytes 9500-9999/10000",range.createContentRangeHeader(fileLength));
	}

	@Test
	public void testContentRange3()
	{
		long fileLength = 10000L;
		ContentRange range = ContentRangeParser.parseRangeHeader("bytes=9500-").get(0);
		assertEquals(9500L,range.getFirst(fileLength));
		assertEquals(9999L,range.getLast(fileLength));
		assertEquals(500L,range.getLength(fileLength));
		List<ContentRange> validRanges = ContentRangeValidator.filterValidRanges(fileLength,Arrays.asList(range));
		assertEquals(1,validRanges.size());
		assertEquals("bytes 9500-9999/10000",range.createContentRangeHeader(fileLength));
	}

	@Test
	public void testContentRange4()
	{
		long fileLength = 10000L;
		ContentRange range = ContentRangeParser.parseRangeHeader("bytes=0-0").get(0);
		assertEquals(0L,range.getFirst(fileLength));
		assertEquals(0L,range.getLast(fileLength));
		assertEquals(1L,range.getLength(fileLength));
		List<ContentRange> validRanges = ContentRangeValidator.filterValidRanges(fileLength,Arrays.asList(range));
		assertEquals(1,validRanges.size());
		assertEquals("bytes 0-0/10000",range.createContentRangeHeader(fileLength));
	}

	@Test
	public void testContentRange5()
	{
		long fileLength = 10000L;
		ContentRange range = ContentRangeParser.parseRangeHeader("bytes=-1").get(0);
		assertEquals(9999L,range.getFirst(fileLength));
		assertEquals(9999L,range.getLast(fileLength));
		assertEquals(1L,range.getLength(fileLength));
		List<ContentRange> validRanges = ContentRangeValidator.filterValidRanges(fileLength,Arrays.asList(range));
		assertEquals(1,validRanges.size());
		assertEquals("bytes 9999-9999/10000",range.createContentRangeHeader(fileLength));
	}

	@Test
	public void testContentRange6()
	{
		long fileLength = 1L;
		ContentRange range = ContentRangeParser.parseRangeHeader("bytes=0-1").get(0);
		assertEquals(0L,range.getFirst(fileLength));
		assertEquals(0L,range.getLast(fileLength));
		assertEquals(1L,range.getLength(fileLength));
		List<ContentRange> validRanges = ContentRangeValidator.filterValidRanges(fileLength,Arrays.asList(range));
		assertEquals(1,validRanges.size());
		assertEquals("bytes 0-0/1",range.createContentRangeHeader(fileLength));
	}

	@Test
	public void testContentRange7()
	{
		long fileLength = 1000L;
		ContentRange range = ContentRangeParser.parseRangeHeader("bytes=-1500").get(0);
		assertEquals(0L,range.getFirst(fileLength));
		assertEquals(999L,range.getLast(fileLength));
		assertEquals(fileLength,range.getLength(fileLength));
		List<ContentRange> validRanges = ContentRangeValidator.filterValidRanges(fileLength,Arrays.asList(range));
		assertEquals(1,validRanges.size());
		assertEquals("bytes 0-999/1000",range.createContentRangeHeader(fileLength));
	}

	@Test
	public void testContentRange8()
	{
		long fileLength = 1000L;
		ContentRange range = ContentRangeParser.parseRangeHeader("bytes=1500-").get(0);
		List<ContentRange> validRanges = ContentRangeValidator.filterValidRanges(fileLength,Arrays.asList(range));
		assertEquals(0,validRanges.size());
	}

	@Test
	public void testContentRange9()
	{
		assertEquals(0,ContentRangeParser.parseRangeHeader("bytes=-").size());
	}

	@Test
	public void testContentRange10()
	{
		assertEquals(2,ContentRangeParser.parseRangeHeader("bytes=0-0,-1").size());
	}

	@Test
	public void testContentRange11()
	{
		assertThrows(IllegalArgumentException.class,() -> ContentRangeParser.parseRangeHeader("bytes=10-0"));
	}

}
