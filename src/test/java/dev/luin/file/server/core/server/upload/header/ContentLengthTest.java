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
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.vavr.api.VavrAssertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import dev.luin.file.server.core.server.upload.UploadException;
import dev.luin.file.server.core.server.upload.UploadRequest;
import io.vavr.collection.Stream;
import lombok.NonNull;
import lombok.val;

@TestInstance(Lifecycle.PER_CLASS)
public class ContentLengthTest
{
	@ParameterizedTest
	@MethodSource
	void testValidContentLength(@NonNull String value)
	{
		assertThat(ContentLength.of(value))
				.hasValueSatisfying(option -> assertThat(option)
						.hasValueSatisfying(c -> c.getValue().toString().equals(value)));
	}

	private static Stream<Arguments> testValidContentLength()
	{
		return Stream.of(
				arguments("0"),
				arguments("1"),
				arguments("1234567890123456789"),
				arguments("9223372036854775807"));
	}

	@Test
	void testEmptyContentLength()
	{
		assertThat(ContentLength.of((String)null))
			.hasValueSatisfying(option -> assertThat(option)
					.isEmpty());
	}

	@ParameterizedTest
	@MethodSource
	void testInvalidContentLength(@NonNull String value)
	{
		assertThat(ContentLength.of(value))
				.failBecauseOf(UploadException.class)
				.satisfies(e -> assertInvalidContentLength((UploadException)e.getCause()));
	}

	private static Stream<Arguments> testInvalidContentLength()
	{
		return Stream.of(
				arguments(""),
				arguments("A"),
				arguments("12345678901234567890"),
				arguments("9223372036854775808"),
				arguments(repeat("9",4000)));
	}

	private void assertInvalidContentLength(final UploadException e)
	{
		assertThat(e.toHttpException().getStatusCode()).isEqualTo(HttpServletResponse.SC_BAD_REQUEST);
		assertThat(e.toHttpException().getMessage()).isNull();
	}

	@ParameterizedTest
	@MethodSource
	void testValidAssertEquals(String value)
	{
		val mock = mock(UploadRequest.class);
		when(mock.getHeader("Content-Length")).thenReturn(value);
		assertThat(ContentLength.equalsEmptyOrZero(mock))
				.hasValueSatisfying(c -> assertThat(c).isEqualTo(mock));
	}

	private static Stream<Arguments> testValidAssertEquals()
	{
		return Stream.of(
				arguments((String)null),
				arguments("0"));
	}

	@ParameterizedTest
	@MethodSource
	void testInvalidAssertEquals(String value)
	{
		val mock = mock(UploadRequest.class);
		when(mock.getHeader("Content-Length")).thenReturn(value);
		assertThat(ContentLength.equalsEmptyOrZero(mock))
				.satisfies(e -> assertInvalidContentLength((UploadException) e.getCause()));
	}

	private static Stream<Arguments> testInvalidAssertEquals()
	{
		return Stream.of(
				arguments("1"),
				arguments("100"),
				arguments("A"),
				arguments(repeat("9",4000)));
	}
//	@TestFactory
//	Stream<DynamicTest> testValidValidate()
//	{
//		return Stream.of(
//				(Length)null,
//				new Length(100L))
//				.map(value -> dynamicTest("FileLength=" + value,() ->
//						assertThat(ContentLength.of("0").flatMap(c -> c.validate(UploadOffset.of("1").get(),value)))
//								.hasRightValueSatisfying(c -> assertThat(c.getValue()).isEqualTo(0))
//				));
//	}
//
//	@TestFactory
//	Stream<DynamicTest> testInvalidValidate()
//	{
//		return Stream.of(
//				Tuple.of("100","1"),
//				Tuple.of("1","100"))
//				.map(value -> dynamicTest("ContentLength=" + value._1 + ", UploadOffset=" + value._2,() ->
//						assertThat(ContentLength.of(value._1).flatMap(c -> c.validate(UploadOffset.of(value._2).get(),new Length(100L))))
//								.hasLeftValueSatisfying(this::assertInvalidContentLength)
//				));
//	}

	@Test
	void testToLength()
	{
		assertThat(ContentLength.of("0"))
				.hasValueSatisfying(option -> assertThat(option)
						.hasValueSatisfying(contentLength -> assertThat(contentLength.getValue()).isEqualTo(0)));
	}
}
