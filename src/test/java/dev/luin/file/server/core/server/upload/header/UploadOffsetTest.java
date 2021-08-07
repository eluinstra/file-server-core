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
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

import javax.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import dev.luin.file.server.core.server.upload.UploadException;
import io.vavr.collection.Stream;

@TestInstance(Lifecycle.PER_CLASS)
public class UploadOffsetTest
{
	@TestFactory
	Stream<DynamicTest> testValidUploadOffset()
	{
		return Stream.of(
				"0",
				"1",
				"1000000000000000000",
				"9223372036854775807")
				.map(v -> dynamicTest("UploadOffset=" + v,() -> assertThat(UploadOffset.of(v))
						.hasValueSatisfying(offset -> offset.getValue().toString().equals(v))));
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

	@TestFactory
	Stream<DynamicTest> testInvalidContentLength()
	{
		return Stream.of(
				"",
				"A",
				"12345678901234567890",
				"9223372036854775808",
				repeat("9",4000))
				.map(v -> dynamicTest("UploadOffset=" + v,() -> assertThat(UploadOffset.of(v))
						.failBecauseOf(UploadException.class)
						.satisfies(e -> assertInvalidUploadOffset((UploadException)e.getCause()))
				));
	}

	private void assertInvalidUploadOffset(UploadException result)
	{
		assertThat(result.toHttpException().getStatusCode()).isEqualTo(HttpServletResponse.SC_CONFLICT);
		assertThat(result.toHttpException().getMessage()).isNull();
	}

}
