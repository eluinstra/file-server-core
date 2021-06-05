package dev.luin.file.server.core.file;

import static org.apache.commons.lang3.Validate.inclusiveBetween;
import static org.apache.commons.lang3.Validate.matchesPattern;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.commons.codec.digest.DigestUtils;

import dev.luin.file.server.core.ValueObjectOptional;
import io.vavr.control.Option;
import io.vavr.control.Try;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.val;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Md5Checksum implements ValueObjectOptional<String>
{
	Option<String> value;

	public static Md5Checksum of(File file)
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

	public Md5Checksum()
	{
		value = Option.none();
	}

	public Md5Checksum(String checksum)
	{
		value = Try.of (() -> Option.of(checksum))
				.andThen(t -> t.peek(v -> inclusiveBetween(32,32,v.length())))
				.andThen(t -> t.map(v -> v.toUpperCase()))
				.andThen(t -> t.peek(v -> matchesPattern(v,"^[0-9A-F]*$")))
				.get();
	}
}
