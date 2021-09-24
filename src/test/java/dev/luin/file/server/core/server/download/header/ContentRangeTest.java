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

import static io.vavr.control.Try.success;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;
import java.io.StringWriter;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import dev.luin.file.server.core.file.Length;
import io.vavr.collection.List;
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
		assertThat(range.getFirst(fileLength)).isZero();
		assertThat(range.getLast(fileLength)).isEqualTo(499);
		assertThat(range.getLength(fileLength)).isEqualTo(new Length(500L));
		val validRanges = ContentRange.filterValidRanges(fileLength,List.of(range));
		assertThat(validRanges.size()).isEqualTo(1);
		assertThat(writeFileLength(fileLength,range).toString()).isEqualTo("Content-Range: bytes 0-499/10000");
	}

	private StringWriter writeFileLength(final Length fileLength, final Range range)
	{
		return success(new StringWriter())
				.andThenTry(sw -> range.write(sw,fileLength))
				.getOrElseThrow(t -> new IllegalStateException());
	}

	@Test
	public void testContentRange1() throws IOException
	{
		val fileLength = new Length(10000L);
		val range = ContentRange.parseRangeHeader("bytes=500-999").get(0);
		assertThat(range.getFirst(fileLength)).isEqualTo(500);
		assertThat(range.getLast(fileLength)).isEqualTo(999);
		assertThat(range.getLength(fileLength)).isEqualTo(new Length(500L));
		val validRanges = ContentRange.filterValidRanges(fileLength,List.of(range));
		assertThat(validRanges.size()).isEqualTo(1);
		assertThat(writeFileLength(fileLength,range).toString()).isEqualTo("Content-Range: bytes 500-999/10000");
	}

	@Test
	public void testContentRange2() throws IOException
	{
		val fileLength = new Length(10000L);
		val range = ContentRange.parseRangeHeader("bytes=-500").get(0);
		assertThat(range.getFirst(fileLength)).isEqualTo(9500);
		assertThat(range.getLast(fileLength)).isEqualTo(9999);
		assertThat(range.getLength(fileLength)).isEqualTo(new Length(500L));
		val validRanges = ContentRange.filterValidRanges(fileLength,List.of(range));
		assertThat(validRanges.size()).isEqualTo(1);
		assertThat(writeFileLength(fileLength,range).toString()).isEqualTo("Content-Range: bytes 9500-9999/10000");
	}

	@Test
	public void testContentRange3()
	{
		val fileLength = new Length(10000L);
		val range = ContentRange.parseRangeHeader("bytes=9500-").get(0);
		assertThat(range.getFirst(fileLength)).isEqualTo(9500);
		assertThat(range.getLast(fileLength)).isEqualTo(9999);
		assertThat(range.getLength(fileLength)).isEqualTo(new Length(500L));
		val validRanges = ContentRange.filterValidRanges(fileLength,List.of(range));
		assertThat(validRanges.size()).isEqualTo(1);
		assertThat(writeFileLength(fileLength,range).toString()).isEqualTo("Content-Range: bytes 9500-9999/10000");
	}

	@Test
	public void testContentRange4()
	{
		val fileLength = new Length(10000L);
		val range = ContentRange.parseRangeHeader("bytes=0-0").get(0);
		assertThat(range.getFirst(fileLength)).isZero();
		assertThat(range.getLast(fileLength)).isZero();
		assertThat(range.getLength(fileLength)).isEqualTo(new Length(1L));
		val validRanges = ContentRange.filterValidRanges(fileLength,List.of(range));
		assertThat(validRanges.size()).isEqualTo(1);
		assertThat(writeFileLength(fileLength,range).toString()).isEqualTo("Content-Range: bytes 0-0/10000");
	}

	@Test
	public void testContentRange5()
	{
		val fileLength = new Length(10000L);
		val range = ContentRange.parseRangeHeader("bytes=-1").get(0);
		assertThat(range.getFirst(fileLength)).isEqualTo(9999);
		assertThat(range.getLast(fileLength)).isEqualTo(9999);
		assertThat(range.getLength(fileLength)).isEqualTo(new Length(1L));
		val validRanges = ContentRange.filterValidRanges(fileLength,List.of(range));
		assertThat(validRanges.size()).isEqualTo(1);
		assertThat(writeFileLength(fileLength,range).toString()).isEqualTo("Content-Range: bytes 9999-9999/10000");
	}

	@Test
	public void testContentRange6()
	{
		val fileLength = new Length(1L);
		val range = ContentRange.parseRangeHeader("bytes=0-1").get(0);
		assertThat(range.getFirst(fileLength)).isZero();
		assertThat(range.getLast(fileLength)).isZero();
		assertThat(range.getLength(fileLength)).isEqualTo(new Length(1L));
		val validRanges = ContentRange.filterValidRanges(fileLength,List.of(range));
		assertThat(validRanges.size()).isEqualTo(1);
		assertThat(writeFileLength(fileLength,range).toString()).isEqualTo("Content-Range: bytes 0-0/1");
	}

	@Test
	public void testContentRange7()
	{
		val fileLength = new Length(1000L);
		val range = ContentRange.parseRangeHeader("bytes=-1500").get(0);
		assertThat(range.getFirst(fileLength)).isZero();
		assertThat(range.getLast(fileLength)).isEqualTo(999);
		assertThat(range.getLength(fileLength)).isEqualTo(fileLength);
		val validRanges = ContentRange.filterValidRanges(fileLength,List.of(range));
		assertThat(validRanges.size()).isEqualTo(1);
		assertThat(writeFileLength(fileLength,range).toString()).isEqualTo("Content-Range: bytes 0-999/1000");
	}

	@Test
	public void testContentRange8()
	{
		val range = ContentRange.parseRangeHeader("bytes=1500-").get(0);
		val validRanges = ContentRange.filterValidRanges(new Length(1000L),List.of(range));
		assertThat(validRanges.size()).isZero();
	}

	@Test
	public void testContentRange9()
	{
		assertThat(ContentRange.parseRangeHeader("bytes=-").size()).isZero();
	}

	@Test
	public void testContentRange10()
	{
		assertThat(ContentRange.parseRangeHeader("bytes=0-0,-1").size()).isEqualTo(2);
	}

	@Test
	public void testContentRange11()
	{
		assertThatThrownBy(() -> ContentRange.parseRangeHeader("bytes=10-0")).isInstanceOf(IllegalArgumentException.class);
	}

}
