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

import static io.vavr.control.Option.some;
import static org.assertj.core.api.Assertions.assertThat;

import dev.luin.file.server.core.file.ContentType;
import dev.luin.file.server.core.file.Filename;
import java.util.stream.Stream;
import org.apache.commons.codec.binary.Base64;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

@TestInstance(Lifecycle.PER_CLASS)
public class UploadMetadataTest
{
	@ParameterizedTest
	@MethodSource
	void testValidUploadMetadata(String value)
	{
		assertThat(new UploadMetadata(value)).matches(
				m -> (m.getContentType().equals(ContentType.TEXT) || m.getContentType().equals(new ContentType("application/octet-stream")))
						&& (m.getFilename() == null || m.getFilename().equals(new Filename("test.txt"))));
	}

	private static String toString(String key, String value)
	{
		return key + " " + Base64.encodeBase64String(value.getBytes());
	}

	private static Stream<String> testValidUploadMetadata()
	{
		return Stream.of(
				(String)null,
				"",
				toString("Content-Type", "text/plain") + "," + toString("filename", "test.txt"),
				toString("Content-Type", "text/plain"),
				toString("filename", "test.txt"));
	}

	@Test
	void testValidUploadMetadata1()
	{
		assertThat(new UploadMetadata(toString("Content-Type", "text/plain") + "," + toString("filename", "test.txt") + "," + toString("test", "A"))).matches(
				m -> m.getContentType().equals(ContentType.TEXT) && m.getFilename().equals(new Filename("test.txt")) && m.getParameter("test").equals(some("A")),
				"Content-Type text/plain,filename test.txt, test A");
	}

}
