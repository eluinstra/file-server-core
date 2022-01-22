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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import dev.luin.file.server.core.file.FSFile;
import dev.luin.file.server.core.file.Md5Checksum;
import lombok.val;

@TestInstance(Lifecycle.PER_CLASS)
public class Md5FileHandlerTest
{
	private static final String CHECKSUM = "12345678901234567890123456789012";

	@Test
	void handleOk()
	{
		val downloadRequest = mock(DownloadRequest.class);
		val downloadResponse = mock(DownloadResponse.class);
		val fsFile = mock(FSFile.class);
		when(fsFile.getMd5Checksum()).thenReturn(new Md5Checksum(CHECKSUM));
		val handler = new Md5FileHandler(fsFile);
		assertThatNoException().isThrownBy(() -> handler.handle(downloadRequest).get()
				.apply(downloadResponse).get());
		verify(downloadResponse).setStatusOk();
		verify(downloadResponse).setHeader("Content-Type","text/plain");
		verify(downloadResponse).setHeader("Content-Length",String.valueOf(CHECKSUM.length()));
		verify(downloadResponse).write(CHECKSUM);
	}
}
