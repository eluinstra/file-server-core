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
package dev.luin.file.server.core.file.encryption;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import lombok.val;

public class ChaChaService implements EncryptionEngine
{
	private static final String ALGORITHM = "ChaCha20";
	private static final String TRANSFORMATION = "ChaCha20-Poly1305/None/NoPadding";
	private static final int KEY_LENGTH = 256;
	private static final int IV_LENGTH = 12;
	KeyGenerator keyGenerator;

	public ChaChaService() throws NoSuchAlgorithmException, NoSuchPaddingException
	{
		keyGenerator = KeyGenerator.getInstance(ALGORITHM);
		keyGenerator.init(KEY_LENGTH, SecureRandom.getInstanceStrong());
	}

	@Override
	public EncryptionSecret generateSecret()
	{
		return new EncryptionSecret(keyGenerator.generateKey().getEncoded(), generateIv1());
	}

	public byte[] generateIv1()
	{
		val result = new byte[IV_LENGTH];
		new SecureRandom().nextBytes(result);
		return result;
	}

	@Override
	public SecretKey generateKey()
	{
		return keyGenerator.generateKey();
	}

	@Override
	public IvParameterSpec generateIv()
	{
		val iv = new byte[IV_LENGTH];
		new SecureRandom().nextBytes(iv);
		return new IvParameterSpec(iv);
	}

	@Override
	public CipherInputStream encryptionInputStream(InputStream in, EncryptionSecret secret)
	{
		try
		{
			val cipher = Cipher.getInstance(TRANSFORMATION);
			cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(secret.getKey(), ALGORITHM), new IvParameterSpec(secret.getIv()));
			return new CipherInputStream(in, cipher);
		}
		catch (InvalidKeyException | InvalidAlgorithmParameterException | NoSuchAlgorithmException | NoSuchPaddingException e)
		{
			throw new IllegalStateException(e);
		}
	}

	public void encrypt(InputStream in, OutputStream out, SecretKey secretKey, IvParameterSpec iv)
	{
		try
		{
			val cipher = Cipher.getInstance(TRANSFORMATION);
			cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(secretKey.getEncoded(), ALGORITHM), iv);
			try (CipherOutputStream cipherOut = new CipherOutputStream(out, cipher))
			{
				in.transferTo(cipherOut);
			}
		}
		catch (InvalidKeyException | InvalidAlgorithmParameterException | IOException | NoSuchAlgorithmException | NoSuchPaddingException e)
		{
			throw new IllegalStateException(e);
		}
	}

	@Override
	public CipherInputStream decryptionInputStream(InputStream in, EncryptionSecret secret)
	{
		try
		{
			val cipher = Cipher.getInstance(TRANSFORMATION);
			cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(secret.getKey(), ALGORITHM), new IvParameterSpec(secret.getIv()));
			return new CipherInputStream(in, cipher);
		}
		catch (InvalidKeyException | InvalidAlgorithmParameterException | NoSuchAlgorithmException | NoSuchPaddingException e)
		{
			throw new IllegalStateException(e);
		}
	}

	public void decrypt(InputStream in, OutputStream out, SecretKey secretKey, IvParameterSpec iv)
	{
		try
		{
			val cipher = Cipher.getInstance(TRANSFORMATION);
			cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(secretKey.getEncoded(), ALGORITHM), iv);
			try (CipherInputStream cipherIn = new CipherInputStream(in, cipher))
			{
				cipherIn.transferTo(out);
			}
		}
		catch (InvalidKeyException | InvalidAlgorithmParameterException | IOException | NoSuchAlgorithmException | NoSuchPaddingException e)
		{
			throw new IllegalStateException(e);
		}
	}
}
