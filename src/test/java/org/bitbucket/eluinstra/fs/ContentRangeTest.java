/**
 * Copyright 2011 Clockwork
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

import org.bitbucket.eluinstra.fs.model.ContentRange;
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
		ContentRange range = new ContentRange(0L,499L);
		assertEquals(0L,range.getFirst(fileLength));
		assertEquals(499L,range.getLast(fileLength));
		assertEquals(500L,range.getLength(fileLength));
	}

	@Test
	public void testContentRange1()
	{
		long fileLength = 10000L;
		ContentRange range = new ContentRange(500L,999L);
		assertEquals(500L,range.getFirst(fileLength));
		assertEquals(999L,range.getLast(fileLength));
		assertEquals(500L,range.getLength(fileLength));
	}

	@Test
	public void testContentRange2()
	{
		long fileLength = 10000L;
		ContentRange range = new ContentRange(null,500L);
		assertEquals(9500L,range.getFirst(fileLength));
		assertEquals(9999L,range.getLast(fileLength));
		assertEquals(500L,range.getLength(fileLength));
	}

	@Test
	public void testContentRange3()
	{
		long fileLength = 10000L;
		ContentRange range = new ContentRange(9500L,null);
		assertEquals(9500L,range.getFirst(fileLength));
		assertEquals(9999L,range.getLast(fileLength));
		assertEquals(500L,range.getLength(fileLength));
	}

	@Test
	public void testContentRange4()
	{
		long fileLength = 1L;
		ContentRange range = new ContentRange(0L,0L);
		assertEquals(0L,range.getFirst(fileLength));
		assertEquals(0L,range.getLast(fileLength));
		assertEquals(1L,range.getLength(fileLength));
	}

	@Test
	public void testContentRange5()
	{
		long fileLength = 1L;
		ContentRange range = new ContentRange(0L,10L);
		assertEquals(0L,range.getFirst(fileLength));
		assertEquals(0L,range.getLast(fileLength));
		assertEquals(1L,range.getLength(fileLength));
	}

	@Test
	public void testContentRange6()
	{
		long fileLength = 1000L;
		ContentRange range = new ContentRange(null,1500L);
		assertEquals(0L,range.getFirst(fileLength));
		assertEquals(999L,range.getLast(fileLength));
		assertEquals(fileLength,range.getLength(fileLength));
	}

}
