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

import javax.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import dev.luin.file.server.core.server.upload.UploadException;
import io.vavr.collection.Stream;
import lombok.experimental.FieldDefaults;

@TestInstance(Lifecycle.PER_CLASS)
@FieldDefaults(makeFinal = true)
public class UploadLengthTest
{
	static TusMaxSize noMaxSize = null;
	private static final TusMaxSize maxSize = TusMaxSize.of(Long.MAX_VALUE);
	private static final TusMaxSize customMaxSize = TusMaxSize.of(1000L);
	Boolean uploadDeferLengthDefined = true;
	private static final Boolean uploadDeferLengthNotDefined = false;

	@ParameterizedTest
	@MethodSource
	void testValidContentLength(TusMaxSize maxSize, Boolean isUploadDeferLengthDefined, String value)
	{
		assertThat(UploadLength.of(value,maxSize,() -> isUploadDeferLengthDefined))
				.hasValueSatisfying(optional -> assertThat(optional).hasValueSatisfying(length -> length.getValue().toString().equals(value)));
	}

	private static Stream<Arguments> testValidContentLength()
	{
		return Stream.of(
				arguments(customMaxSize,uploadDeferLengthNotDefined,"0"),
				arguments(customMaxSize,uploadDeferLengthNotDefined,"1"),
				arguments(customMaxSize,uploadDeferLengthNotDefined,"1000"),
				arguments(maxSize,uploadDeferLengthNotDefined,"9223372036854775807"));
	}

	@Test
	void testEmptyContentLength()
	{
		assertThat(UploadLength.of(null,customMaxSize,() -> uploadDeferLengthDefined)).hasValueSatisfying(optional -> assertThat(optional).isEmpty());
	}

	@ParameterizedTest
	@MethodSource
	void testInvalidContentLength(TusMaxSize maxSize, Boolean isUploadDeferLengthDefined, String value)
	{
		assertThat(UploadLength.of(value,maxSize,() -> isUploadDeferLengthDefined))
				.failBecauseOf(UploadException.class)
				.satisfies(t -> assertInvalidContentLength((UploadException) t.getCause()));
	}

	private static Stream<Arguments> testInvalidContentLength()
	{
		return Stream.of(
				arguments(customMaxSize,uploadDeferLengthNotDefined,(String)null),
				arguments(customMaxSize,uploadDeferLengthNotDefined,""),
				arguments(customMaxSize,uploadDeferLengthNotDefined,"-1"),
				arguments(customMaxSize,uploadDeferLengthNotDefined,"10000000000000000000"),
				arguments(customMaxSize,uploadDeferLengthNotDefined,"ABC"),
				arguments(noMaxSize,uploadDeferLengthNotDefined,"9223372036854775808"),
				arguments(noMaxSize,uploadDeferLengthNotDefined,repeat("9",4000)));
	}

	private void assertInvalidContentLength(UploadException e)
	{
		assertThat(e.toHttpException().getStatusCode()).isEqualTo(HttpServletResponse.SC_BAD_REQUEST);
	}

	@ParameterizedTest
	@MethodSource
	void testContentLengthTooLarge(TusMaxSize maxSize, Boolean isUploadDeferLengthDefined, String value)
	{
		assertThat(UploadLength.of(value,maxSize,() -> isUploadDeferLengthDefined))
				.failBecauseOf(UploadException.class)
				.satisfies(t -> assertContentLengthTooLarge((UploadException) t.getCause()));
	}

	private static Stream<Arguments> testContentLengthTooLarge()
	{
		return Stream.of(
					arguments(customMaxSize,uploadDeferLengthNotDefined,"1001"),
					arguments(customMaxSize,uploadDeferLengthNotDefined,"9223372036854775807"));
	}

	private void assertContentLengthTooLarge(UploadException e)
	{
		assertThat(((UploadException) e).toHttpException().getStatusCode()).isEqualTo(HttpServletResponse.SC_REQUEST_ENTITY_TOO_LARGE);
	}

	@Test
	void testToLength()
	{
		assertThat(UploadLength.of("0",customMaxSize,() -> uploadDeferLengthNotDefined).map(v -> v.get()).map(UploadLength::toFileLength))
				.hasValueSatisfying(length -> assertThat(length.getValue()).isZero());
	}
}
