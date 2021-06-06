package dev.luin.file.server.core.file;

import java.time.Instant;

import dev.luin.file.server.core.ValueObject;
import lombok.Value;

@Value
public class Timestamp implements ValueObject<Instant>
{
	Instant value;

	public Timestamp()
	{
		this(Instant.now());
	}

	public Timestamp(Instant timestamp)
	{
		value = timestamp;
	}
}
