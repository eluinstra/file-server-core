package dev.luin.file.server.core.file;

import static org.apache.commons.lang3.Validate.inclusiveBetween;
import static org.apache.commons.lang3.Validate.matchesPattern;

import dev.luin.file.server.core.ValueObject;
import io.vavr.control.Try;
import lombok.NonNull;
import lombok.Value;

@Value
public class Filename implements ValueObject<String>
{
	@NonNull
	String value;

	public Filename(@NonNull final String filename)
	{
		value = Try.success(filename)
				.andThenTry(v -> inclusiveBetween(0,256,v.length()))
				.andThenTry(v -> matchesPattern(v,"^[^\\/:\\*\\?\"<>\\|]*$"))
				.get();
	}
}
