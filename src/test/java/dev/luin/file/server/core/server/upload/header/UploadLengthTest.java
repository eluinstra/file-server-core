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
import io.vavr.Tuple;
import io.vavr.collection.Stream;
import lombok.experimental.FieldDefaults;

@TestInstance(Lifecycle.PER_CLASS)
@FieldDefaults(makeFinal = true)
public class UploadLengthTest
{
	TusMaxSize noMaxSize = null;
	TusMaxSize maxSize = TusMaxSize.of(Long.MAX_VALUE);
	TusMaxSize customMaxSize = TusMaxSize.of(1000L);
	Boolean uploadDeferLengthDefined = true;
	Boolean uploadDeferLengthNotDefined = false;

	@TestFactory
	Stream<DynamicTest> testValidContentLength()
	{
		return Stream.of(
					Tuple.of(customMaxSize,uploadDeferLengthNotDefined,"0"),
					Tuple.of(customMaxSize,uploadDeferLengthNotDefined,"1"),
					Tuple.of(customMaxSize,uploadDeferLengthNotDefined,"1000"),
					Tuple.of(maxSize,uploadDeferLengthNotDefined,"9223372036854775807")
				)
				.map(v -> dynamicTest(
						"UploadLength=" + v,
						() -> assertThat(UploadLength.of(v._3,v._1,() -> v._2))
								.hasValueSatisfying(optional -> assertThat(optional).hasValueSatisfying(length -> length.getValue().toString().equals(v._3)))
				));
	}

	@Test
	void testEmptyContentLength()
	{
		assertThat(UploadLength.of(null,customMaxSize,() -> uploadDeferLengthDefined)).hasValueSatisfying(optional -> assertThat(optional).isEmpty());
	}

	@TestFactory
	Stream<DynamicTest> testInvalidContentLength()
	{
		return Stream.of(
					Tuple.of(customMaxSize,uploadDeferLengthNotDefined,(String)null),
					Tuple.of(customMaxSize,uploadDeferLengthNotDefined,""),
					Tuple.of(customMaxSize,uploadDeferLengthNotDefined,"-1"),
					Tuple.of(customMaxSize,uploadDeferLengthNotDefined,"10000000000000000000"),
					Tuple.of(customMaxSize,uploadDeferLengthNotDefined,"ABC"),
					Tuple.of(noMaxSize,uploadDeferLengthNotDefined,"9223372036854775808"),
					Tuple.of(noMaxSize,uploadDeferLengthNotDefined,repeat("9",4000))
				)
				.map(value -> dynamicTest(
						"UploadLength=" + value,
						() -> assertThat(UploadLength.of(value._3,value._1,() -> value._2))
								.failBecauseOf(UploadException.class)
								.satisfies(t -> assertInvalidContentLength((UploadException) t.getCause()))
				));
	}

	private void assertInvalidContentLength(UploadException e)
	{
		assertThat(e.toHttpException().getStatusCode()).isEqualTo(HttpServletResponse.SC_BAD_REQUEST);
	}

	@TestFactory
	Stream<DynamicTest> testContentLengthTooLarge()
	{
		return Stream.of(
					Tuple.of(customMaxSize,uploadDeferLengthNotDefined,"1001"),
					Tuple.of(customMaxSize,uploadDeferLengthNotDefined,"9223372036854775807")
				)
				.map(value -> dynamicTest(
						"UploadLength=" + value,
						() -> assertThat(UploadLength.of(value._3,value._1,() -> value._2))
								.failBecauseOf(UploadException.class)
								.satisfies(t -> assertContentLengthTooLarge((UploadException) t.getCause()))
				));
	}

	private void assertContentLengthTooLarge(UploadException e)
	{
		assertThat(((UploadException) e).toHttpException().getStatusCode()).isEqualTo(HttpServletResponse.SC_REQUEST_ENTITY_TOO_LARGE);
	}

	@Test
	void testToLength()
	{
		assertThat(UploadLength.of("0",customMaxSize,() -> uploadDeferLengthNotDefined).map(v -> v.get()).map(UploadLength::toFileLength))
				.hasValueSatisfying(length -> assertThat(length.getValue()).isEqualTo(0));
	}
}
