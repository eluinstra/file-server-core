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
package dev.luin.file.server.core.server.upload.header;

import static org.apache.commons.lang3.StringUtils.repeat;
import static org.assertj.core.api.Assertions.*;
//import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertNull;
//import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

import javax.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.mockito.Mockito;

import dev.luin.file.server.core.file.Length;
import dev.luin.file.server.core.server.upload.UploadException;
import dev.luin.file.server.core.server.upload.UploadRequest;
import io.vavr.Tuple;
import io.vavr.collection.Stream;
import io.vavr.control.Either;
import lombok.val;

@TestInstance(Lifecycle.PER_CLASS)
public class ContentLengthTest
{
	@TestFactory
	Stream<DynamicTest> testValidContentLength()
	{
		return Stream.of(
				"0",
				"1",
				"1000000000000000000",
				"9223372036854775807")
				.map(v -> dynamicTest("ContentLength=" + v,() -> assertThatNoException().isThrownBy((() -> new ContentLength(v)))));
	}

	@Test
	void testEmptyContentLength()
	{
		assertThatThrownBy(() -> new ContentLength((String)null)).hasCause(new NullPointerException());
	}

	@TestFactory
	Stream<DynamicTest> testInvalidContentLength()
	{
		return Stream.of(
				"",
				"A",
				"10000000000000000000",
				"9223372036854775808",
				repeat("9", 4000))
				.map(v -> dynamicTest("ContentLength=" + v,() -> {
						val result = catchThrowable(() -> new ContentLength(v));
						assertThat(result).hasCause(UploadException.invalidContentLength());
						assertInvalidContentLength((UploadException)result);
				}));
	}

	private void assertInvalidContentLength(final UploadException result)
	{
		assertThat(result.toHttpException().getStatusCode()).isEqualTo(HttpServletResponse.SC_BAD_REQUEST);
		assertThat(result.toHttpException().getMessage()).isNull();
	}

	@Test
	void testValidAssertEquals()
	{
		val mock = Mockito.mock(UploadRequest.class);
		Mockito.when(mock.getHeader("Content-Length")).thenReturn("0");
		assertThat(ContentLength.equalsZero(mock)).isEqualTo(Either.right(mock));
	}

	@Test
	void testInvalidAssertEquals()
	{
		val mock = Mockito.mock(UploadRequest.class);
		Mockito.when(mock.getHeader("Content-Length")).thenReturn("1");
		assertThat(ContentLength.equalsZero(mock)).isEqualTo(Either.left(UploadException.invalidContentLength()));
	}

	@TestFactory
	Stream<DynamicTest> testValidValidate()
	{
		return Stream.of(
				null,
				new Length(100L))
				.map(v -> dynamicTest("FileLength=" + v,() -> assertThatNoException().isThrownBy((() -> new ContentLength("0").validate(new UploadOffset("1"),v)))));
	}

	@TestFactory
	Stream<DynamicTest> testInvalidValidate()
	{
		return Stream.of(
				Tuple.of("100","1"),
				Tuple.of("1","100"))
				.map(v -> dynamicTest("ContentLength=" + v._1 + ", UploadOffset=" + v._2,() -> {
						val result = catchThrowable(() -> new ContentLength(v._1).validate(new UploadOffset(v._2),new Length(100L)));
						assertThat(result).hasCause(UploadException.invalidUploadOffset());
						assertInvalidContentLength((UploadException)result);
				}));
	}

	@Test
	void testToFileLength()
	{
		assertThat(new ContentLength("0").toLength().getValue()).isEqualTo(0);
	}
}
