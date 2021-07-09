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
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

import javax.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import dev.luin.file.server.core.server.upload.UploadException;
import io.vavr.collection.Stream;
import lombok.val;

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
				.map(v -> dynamicTest("UploadOffset=" + v,() -> assertThatNoException().isThrownBy((() -> new UploadOffset(v)))));
	}

	@TestFactory
	Stream<DynamicTest> testInvalidContentLength()
	{
		return Stream.of(
				null,
				"",
				"A",
				"12345678901234567890",
				"9223372036854775808",
				repeat("9",4000))
				.map(v -> dynamicTest("UploadOffset=" + v,() -> {
						val result = catchThrowable(() -> new UploadOffset(v));
						assertThat(result).isInstanceOf(UploadException.class);
						assertInvalidUploadOffset((UploadException)result);
				}));
	}

	private void assertInvalidUploadOffset(UploadException result)
	{
		assertThat(result.toHttpException().getStatusCode()).isEqualTo(HttpServletResponse.SC_CONFLICT);
		assertThat(result.toHttpException().getMessage()).isNull();
	}

}
