package dev.luin.file.server.core.file;

import java.time.Instant;

import dev.luin.file.server.core.ValueObject;
import lombok.NonNull;
import lombok.Value;

@Value
public class Timestamp implements ValueObject<Instant>
{
	@NonNull
	Instant value;

	public Timestamp()
	{
		this(Instant.now());
	}

	public Timestamp(@NonNull Instant timestamp)
	{
		value = timestamp;
	}
}
