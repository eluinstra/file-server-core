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
package dev.luin.file.server.core.file;

import static org.assertj.core.api.Assertions.assertThat;

import dev.luin.file.server.core.file.encryption.ChaChaService;
import dev.luin.file.server.core.file.encryption.EncryptionEngine;
import io.vavr.control.Try;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.security.NoSuchAlgorithmException;
import javax.crypto.NoSuchPaddingException;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

@TestInstance(Lifecycle.PER_CLASS)
class ChaChaServiceTest
{
	RandomFileGenerator randomFileGenerator = new RandomFileGenerator("/tmp", 0, 10);

	@Test
	void encryptFile() throws IOException, NoSuchAlgorithmException, NoSuchPaddingException
	{
		val in = createRandomFile();
		val encryptionService = new ChaChaService();
		val encrypted = randomFileGenerator.create().get();
		val secret = encryptionService.generateSecret();
		Try.withResources(() -> encryptionService.encryptionInputStream(new FileInputStream(in.getFile()), secret))
				.of(is -> is.transferTo(new FileOutputStream(encrypted.getFile())));
		val out = randomFileGenerator.create().get();
		Try.withResources(() -> encryptionService.decryptionInputStream(new FileInputStream(encrypted.getFile()), secret))
				.of(is -> is.transferTo(new FileOutputStream(out.getFile())));
		assertThat(Files.mismatch(in.getPath(), encrypted.getPath())).isNotEqualTo(-1);
		assertThat(Files.mismatch(in.getPath(), out.getPath())).isEqualTo(-1);
	}

	private RandomFile createRandomFile() throws IOException
	{
		val result = randomFileGenerator.create().get();
		val writer = new BufferedWriter(new FileWriter(result.getFile()));
		writer.append("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Morbi elementum tellus non varius volutpat.");
		writer.close();
		return result;
	}

	@Test
	void encryptFile1() throws IOException, NoSuchAlgorithmException, NoSuchPaddingException
	{
		val in = createRandomFile();
		val encryptionService = new ChaChaService();
		val encrypted = randomFileGenerator.create().get();
		val key = encryptionService.generateKey();
		val iv = encryptionService.generateIv();
		encryptionService.encrypt(new FileInputStream(in.getFile()), new FileOutputStream(encrypted.getFile()), key, iv);
		val out = randomFileGenerator.create().get();
		encryptionService.decrypt(
				new FileInputStream(encrypted.getFile()),
				new FileOutputStream(out.getFile()),
				key,
				EncryptionEngine.toIvUnsafe(EncryptionEngine.toString(iv)));
		assertThat(Files.mismatch(in.getPath(), encrypted.getPath())).isNotEqualTo(-1);
		assertThat(Files.mismatch(in.getPath(), out.getPath())).isEqualTo(-1);
	}
}
