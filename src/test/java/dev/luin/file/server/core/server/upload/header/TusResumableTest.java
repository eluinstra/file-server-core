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

import static org.assertj.vavr.api.VavrAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.stream.Stream;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import dev.luin.file.server.core.server.upload.UploadException;
import dev.luin.file.server.core.server.upload.UploadResponse;
import lombok.val;

@TestInstance(Lifecycle.PER_CLASS)
public class TusResumableTest
{
	@Test
	void testValidTusResumable()
	{
		assertDoesNotThrow(() -> TusResumable.validate("1.0.0"));
	}

	@TestFactory
	Stream<DynamicTest> testInvalidTusResumable()
	{
		return Stream.of(
				null,
				"",
				"1")
				.map(input -> dynamicTest("Input: " + input,() -> assertThat(TusResumable.validate(input))
						.failBecauseOf(UploadException.class)));
//		UploadException.class
	}

	@Test
	void testWrite()
	{
		val mock = mock(UploadResponse.class);
		TusResumable.write(mock);
		verify(mock).setHeader("Tus-Resumable","1.0.0");
	}
}
