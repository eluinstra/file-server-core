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
import lombok.NonNull;

@TestInstance(Lifecycle.PER_CLASS)
public class UploadOffsetTest
{
	@ParameterizedTest
	@MethodSource
	void testValidUploadOffset(@NonNull String value)
	{
		assertThat(UploadOffset.of(value))
				.hasValueSatisfying(offset -> offset.getValue().toString().equals(value));
	}

	private static Stream<Arguments> testValidUploadOffset()
	{
		return Stream.of(
				arguments("0"),
				arguments("1"),
				arguments("1000000000000000000"),
				arguments("9223372036854775807"));
	}

	@Test
	void testEmptyContentLength()
	{
		assertThat(UploadOffset.of((String)null))
				.failBecauseOf(UploadException.class)
				.satisfies(t -> assertEmptyContentLength((UploadException) t.getCause()));
	}

	private void assertEmptyContentLength(UploadException e)
	{
		assertThat(e.toHttpException().getStatusCode()).isEqualTo(HttpServletResponse.SC_BAD_REQUEST);
	}

	@ParameterizedTest
	@MethodSource
	void testInvalidContentLength(@NonNull String value)
	{
			assertThat(UploadOffset.of(value))
					.failBecauseOf(UploadException.class)
					.satisfies(e -> assertInvalidUploadOffset((UploadException)e.getCause()));
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

	private void assertInvalidUploadOffset(UploadException result)
	{
		assertThat(result.toHttpException().getStatusCode()).isEqualTo(HttpServletResponse.SC_CONFLICT);
		assertThat(result.toHttpException().getMessage()).isNull();
	}

}
