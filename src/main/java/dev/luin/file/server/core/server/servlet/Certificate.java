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
package dev.luin.file.server.core.server.servlet;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import dev.luin.file.server.core.ValueObject;
import io.vavr.control.Try;
import lombok.NonNull;
import lombok.Value;

@Value
public class Certificate implements ValueObject<X509Certificate>
{
	@NonNull
	X509Certificate value;

	public static Certificate of(@NonNull final byte[] certificate)
	{
		return of(new ByteArrayInputStream(certificate));
	}

	public static Certificate of(@NonNull final String certificate)
	{
		return of(new ByteArrayInputStream(certificate.getBytes(Charset.defaultCharset())));
	}

	public static Certificate of(@NonNull final InputStream certificate)
	{
		return Try.of(() -> CertificateFactory.getInstance("X509"))
			.mapTry(factory -> new Certificate((X509Certificate)factory.generateCertificate(certificate)))
			.getOrElseThrow(t -> new IllegalStateException(t));
	}

	public byte[] getEncoded()
	{
		try
		{
			return value.getEncoded();
		}
		catch (CertificateEncodingException e)
		{
			throw new IllegalStateException(e);
		}
	}
}
