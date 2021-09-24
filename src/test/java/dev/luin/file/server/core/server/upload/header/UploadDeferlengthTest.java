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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import dev.luin.file.server.core.server.upload.UploadRequest;
import lombok.val;

@TestInstance(Lifecycle.PER_CLASS)
public class UploadDeferlengthTest
{
	private static final String UPLOAD_DEFER_LENGTH = "Upload-Defer-Length";

	@Test
	void testValidIsDefined()
	{
		val mock = mock(UploadRequest.class);
		when(mock.getHeader(UPLOAD_DEFER_LENGTH)).thenReturn("1");
		assertTrue(UploadDeferLength.isDefined(mock));
	}

	@ParameterizedTest
	@MethodSource
	void testInvalidIsDefined(String value)
	{
		val mock = mock(UploadRequest.class);
		when(mock.getHeader(UPLOAD_DEFER_LENGTH)).thenReturn(value);
		assertFalse(UploadDeferLength.isDefined(mock));
	}

	private static Stream<Arguments> testInvalidIsDefined()
	{
		return Stream.of(
				Arguments.arguments((String)null),
				Arguments.arguments("0"),
				Arguments.arguments("10"));
	}
}
