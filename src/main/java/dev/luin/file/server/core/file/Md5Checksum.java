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
public class Md5Checksum implements ValueObject<String>
{
	@NonNull
	String value;

	public static Md5Checksum of(@NonNull final File file)
	{
		try (val is = new FileInputStream(file))
		{
			return new Md5Checksum(DigestUtils.md5Hex(is));
		}
		catch (IOException e)
		{
			throw new IllegalStateException(e);
		}
	}

	public Md5Checksum(@NonNull final String checksum)
	{
		value = Try.success(checksum)
				.andThen(v -> inclusiveBetween(32,32,v.length()))
				.map(v -> v.toUpperCase())
				.andThen(v -> matchesPattern(v,"^[0-9A-F]*$"))
				.get();
	}
}
