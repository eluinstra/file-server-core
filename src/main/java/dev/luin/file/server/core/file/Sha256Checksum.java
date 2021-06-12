package dev.luin.file.server.core.file;

import static org.apache.commons.lang3.Validate.inclusiveBetween;
import static org.apache.commons.lang3.Validate.matchesPattern;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.commons.codec.digest.DigestUtils;

import dev.luin.file.server.core.ValueObject;
import io.vavr.control.Try;
import lombok.NonNull;
import lombok.Value;
import lombok.val;

@Value
public class Sha256Checksum implements ValueObject<String>
{
	@NonNull
	String value;

	public static Sha256Checksum of(@NonNull final File file)
	{
		try (val is = new FileInputStream(file))
		{
			return new Sha256Checksum(DigestUtils.sha256Hex(is));
		}
		catch (IOException e)
		{
			throw new IllegalStateException(e);
		}
	}

	public Sha256Checksum(@NonNull final String checksum)
	{
		value = Try.success(checksum)
				.andThen(v -> inclusiveBetween(32,64,v.length()))
				.map(v -> v.toUpperCase())
				.andThen(v -> matchesPattern(v,"^[0-9A-F]*$"))
				.get();
	}
	
	public boolean validate(@NonNull final Sha256Checksum checksum)
	{
		return this.equals(checksum);
	}
}
