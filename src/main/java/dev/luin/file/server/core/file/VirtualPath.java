package dev.luin.file.server.core.file;

import static org.apache.commons.lang3.Validate.inclusiveBetween;
import static org.apache.commons.lang3.Validate.matchesPattern;

import dev.luin.file.server.core.ValueObject;
import io.vavr.control.Try;
import lombok.NonNull;
import lombok.Value;

@Value
public class VirtualPath implements ValueObject<String>
{
	@NonNull
	String value;

	public VirtualPath(@NonNull String virtualPath)
	{
		value = Try.success(virtualPath)
				.andThen(v -> inclusiveBetween(2,256,v.length()))
				.andThen(v -> matchesPattern(v,"^/[a-zA-Z0-9]+$"))
				.get();
	}
}
