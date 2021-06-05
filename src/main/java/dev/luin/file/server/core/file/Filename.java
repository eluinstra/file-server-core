package dev.luin.file.server.core.file;

import static org.apache.commons.lang3.Validate.inclusiveBetween;
import static org.apache.commons.lang3.Validate.matchesPattern;

import dev.luin.file.server.core.ValueObjectOptional;
import io.vavr.control.Option;
import io.vavr.control.Try;
import lombok.Value;

@Value
public class Filename implements ValueObjectOptional<String>
{
	Option<String> value;

	public Filename(String filename)
	{
		this(Option.of(filename));
	}

	public Filename(Option<String> filename)
	{
		value = Try.of(() -> filename)
				// \ / : * ? " < > |
				.andThenTry(t -> t.peek(v -> inclusiveBetween(0,256,v.length())))
				.andThenTry(t -> t.peek(v -> matchesPattern(v,"^[^\\/:\\*\\?\"<>\\|]*$")))
				.get();
	}
}
