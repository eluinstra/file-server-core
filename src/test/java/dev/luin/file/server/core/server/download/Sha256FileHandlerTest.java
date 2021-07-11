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
package dev.luin.file.server.core.server.download;

import static java.util.function.Function.identity;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.stream.Stream;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import dev.luin.file.server.core.file.FSFile;
import dev.luin.file.server.core.file.Sha256Checksum;
import lombok.val;

@TestInstance(Lifecycle.PER_CLASS)
public class Sha256FileHandlerTest
{
	@TestFactory
	Stream<DynamicTest> handleOk()
	{
		return Stream.of(
				"12345678901234567890123456789012",
				"1234567890123456789012345678901234567890123456789012345678901234")
			.map(input -> dynamicTest("Input: " + input,() ->
			{
				val downloadRequest = mock(DownloadRequest.class);
				val downloadResponse = mock(DownloadResponse.class);
				val fsFile = mock(FSFile.class);
				when(fsFile.getSha256Checksum()).thenReturn(new Sha256Checksum(input));
				val handler = new Sha256FileHandler(fsFile);
				assertThatNoException().isThrownBy(() -> handler.handle(downloadRequest)
						.getOrElseThrow(identity())
						.apply(downloadResponse)
						.getOrElseThrow(identity()));
				verify(downloadResponse).setStatusOk();
				verify(downloadResponse).setHeader("Content-Type","text/plain");
				verify(downloadResponse).setHeader("Content-Length",String.valueOf(input.length()));
				verify(downloadResponse).write(input);
			}));
	}
}
