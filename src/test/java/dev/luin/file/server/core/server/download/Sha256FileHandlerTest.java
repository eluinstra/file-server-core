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

import static org.junit.jupiter.api.DynamicTest.dynamicTest;

import java.util.stream.Stream;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.mockito.Mockito;

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
			.map(input -> dynamicTest("Input: " + input,() -> {
				val downloadRequest = Mockito.mock(DownloadRequest.class);
				val downloadResponse = Mockito.mock(DownloadResponse.class);
				val fsFile = Mockito.mock(FSFile.class);
				Mockito.when(fsFile.getSha256Checksum()).thenReturn(new Sha256Checksum(input));
				Sha256FileHandler handler = new Sha256FileHandler(fsFile);
				handler.handle(downloadRequest,downloadResponse);
				Mockito.verify(downloadResponse).setStatusOk();
				Mockito.verify(downloadResponse).setHeader("Content-Type","text/plain");
				Mockito.verify(downloadResponse).setHeader("Content-Length",String.valueOf(input.length()));
				Mockito.verify(downloadResponse).write(input);
			}));
	}
}
