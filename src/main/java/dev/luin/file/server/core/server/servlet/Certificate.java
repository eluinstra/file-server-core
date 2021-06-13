package dev.luin.file.server.core.server.servlet;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import dev.luin.file.server.core.ValueObject;
import lombok.NonNull;
import lombok.Value;
import lombok.val;

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
		try
		{
			val cf = CertificateFactory.getInstance("X509");
			return new Certificate((X509Certificate)cf.generateCertificate(certificate));
		}
		catch (CertificateException e)
		{
			throw new IllegalStateException(e);
		}
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
