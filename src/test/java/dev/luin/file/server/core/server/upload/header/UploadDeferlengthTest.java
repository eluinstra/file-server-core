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
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

import java.util.stream.Stream;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.mockito.Mockito;

import dev.luin.file.server.core.server.upload.UploadRequest;
import lombok.val;

@TestInstance(Lifecycle.PER_CLASS)
public class UploadDeferlengthTest
{
	private static final String UPLOAD_DEFER_LENGTH = "Upload-Defer-Length";

	@Test
	void testValidIsDefined()
	{
		val mock = Mockito.mock(UploadRequest.class);
		Mockito.when(mock.getHeader(UPLOAD_DEFER_LENGTH)).thenReturn("1");
		assertTrue(UploadDeferLength.isDefined(mock));
	}

	@TestFactory
	Stream<DynamicTest> testInvalidIsDefined()
	{
		val mock = Mockito.mock(UploadRequest.class);
		return Stream.of(
				null,
				"0",
				"10")
				.map(v -> dynamicTest("UploadDeferLength=" + v,() -> {
						Mockito.when(mock.getHeader(UPLOAD_DEFER_LENGTH)).thenReturn(v);
						assertFalse(UploadDeferLength.isDefined(mock));
				}));
	}
}
