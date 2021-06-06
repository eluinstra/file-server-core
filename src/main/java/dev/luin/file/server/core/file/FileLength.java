package dev.luin.file.server.core.file;

import java.math.BigInteger;

import org.apache.commons.lang3.Validate;

import dev.luin.file.server.core.ValueObject;
import dev.luin.file.server.core.server.download.range.ContentRange;
import io.vavr.control.Try;
import lombok.NonNull;
import lombok.Value;

@Value
public class FileLength implements ValueObject<Long>
{
	Long value;

	public FileLength(@NonNull final Long fileLength)
	{
		value = Try.success(fileLength)
				.andThenTry(v -> Validate.isTrue(v.compareTo(0L) >= 0))
				.get();
	}

	public boolean containsFirstPosition(final ContentRange range)
	{
		return range.getFirst(this) < value;
	}

	public BigInteger toBigInteger()
	{
		return BigInteger.valueOf(value);
	}

	public String print()
	{
		return value.toString();
	}
}
