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
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import dev.luin.file.server.core.server.upload.UploadException;
import dev.luin.file.server.core.server.upload.UploadResponse;
import lombok.NonNull;
import lombok.val;

@TestInstance(Lifecycle.PER_CLASS)
public class TusResumableTest
{
	@Test
	void testValidTusResumable()
	{
		assertDoesNotThrow(() -> TusResumable.validate("1.0.0"));
	}

	@ParameterizedTest
	@MethodSource
	void testInvalidTusResumable(@NonNull String input)
	{
		assertThat(TusResumable.validate(input))
				.failBecauseOf(UploadException.class);
	}

	private static Stream<Arguments> testInvalidTusResumable()
	{
		return Stream.of(
				arguments((String)null),
				arguments(""),
				arguments("1"));
	}

	@Test
	void testWrite()
	{
		val mock = mock(UploadResponse.class);
		TusResumable.write(mock);
		verify(mock).setHeader("Tus-Resumable","1.0.0");
	}
}
