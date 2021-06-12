package dev.luin.file.server.core.file;

import java.math.BigInteger;

import org.apache.commons.lang3.Validate;

import dev.luin.file.server.core.ValueObject;
import dev.luin.file.server.core.server.download.header.Range;
import io.vavr.control.Try;
import lombok.NonNull;
import lombok.Value;

@Value
public class Length implements ValueObject<Long>
{
	@NonNull
	Long value;

	public Length(final int fileLength)
	{
		this((long)fileLength);
	}

	public Length(@NonNull final Long fileLength)
	{
		value = Try.success(fileLength)
				.andThenTry(v -> Validate.isTrue(v.compareTo(0L) >= 0))
				.get();
	}

	public boolean containsFirstPosition(@NonNull final Range range)
	{
		return range.getFirst(this) < value;
	}

	public BigInteger toBigInteger()
	{
		return BigInteger.valueOf(value);
	}

	public String getStringValue()
	{
		return value.toString();
	}
}
