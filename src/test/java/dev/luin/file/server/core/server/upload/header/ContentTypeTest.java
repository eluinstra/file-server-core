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

import static org.assertj.vavr.api.VavrAssertions.assertThat;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

import dev.luin.file.server.core.server.upload.UploadException;
import jakarta.servlet.http.HttpServletResponse;
import java.util.stream.Stream;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

@TestInstance(Lifecycle.PER_CLASS)
public class ContentTypeTest
{
	@Test
	void testValidContentType()
	{
		assertThat(ContentType.validate("application/offset+octet-stream")).contains("application/offset+octet-stream");
	}

	@TestFactory
	Stream<DynamicTest> testInvalidContentType()
	{
		return Stream.of((String)null, "", "text/xml")
				.map(
						input -> dynamicTest(
								"Input: " + input,
								() -> assertThat(ContentType.validate(input)).failBecauseOf(UploadException.class)
										.matches(e -> assertInvalidContentType((UploadException)e.getCause()))));
	}

	private boolean assertInvalidContentType(UploadException e)
	{
		return e.toHttpException().getStatusCode() == HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE;
	}
}
