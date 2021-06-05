package dev.luin.file.server.core.file;

import static org.apache.commons.lang3.Validate.inclusiveBetween;
import static org.apache.commons.lang3.Validate.matchesPattern;

import dev.luin.file.server.core.ValueObject;
import io.vavr.control.Option;
import lombok.Value;

@Value
public class VirtualPath implements ValueObject<String>
{
	String value;

	public VirtualPath(String virtualPath)
	{
		value = Option.of(virtualPath)
				.toTry()
				.andThenTry(v -> inclusiveBetween(2,256,v.length()))
				.andThenTry(v -> matchesPattern(v,"^/[a-zA-Z0-9]+$"))
				.get();
	}
}
