package dev.luin.file.server.core.service.user;

import static org.apache.commons.lang3.Validate.*;

import dev.luin.file.server.core.ValueObject;
import io.vavr.control.Try;
import lombok.NonNull;
import lombok.Value;

@Value
public class Username implements ValueObject<String>
{
	@NonNull
	String value;

	public Username(@NonNull String username)
	{
		value = Try.success(username)
				.andThen(v -> inclusiveBetween(5,32,v.length(),"Username length must be between 5 and 32 characters"))
				.andThen(v -> matchesPattern(v,"^[0-9a-zA-Z\\.-_]$","Illegal username"))
				.get();
	}
}
