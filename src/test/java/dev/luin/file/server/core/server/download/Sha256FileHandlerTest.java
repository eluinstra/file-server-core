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
package dev.luin.file.server.core.server.download;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import dev.luin.file.server.core.file.FSFile;
import dev.luin.file.server.core.file.Sha256Checksum;
import java.util.stream.Stream;
import lombok.NonNull;
import lombok.val;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@TestInstance(Lifecycle.PER_CLASS)
public class Sha256FileHandlerTest
{
	@ParameterizedTest
	@MethodSource
	void handleOk(@NonNull String input)
	{
		val downloadRequest = mock(DownloadRequest.class);
		val downloadResponse = mock(DownloadResponse.class);
		val fsFile = mock(FSFile.class);
		when(fsFile.getSha256Checksum()).thenReturn(new Sha256Checksum(input));
		val handler = new Sha256FileHandler(fsFile);
		assertThatNoException().isThrownBy(() -> handler.handle(downloadRequest).get().apply(downloadResponse).get());
		verify(downloadResponse).setStatusOk();
		verify(downloadResponse).setHeader("Content-Type", "text/plain");
		verify(downloadResponse).setHeader("Content-Length", String.valueOf(input.length()));
		verify(downloadResponse).write(input);
	}

	private static Stream<Arguments> handleOk()
	{
		return Stream.of(arguments("12345678901234567890123456789012"), arguments("1234567890123456789012345678901234567890123456789012345678901234"));
	}
}
