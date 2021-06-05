package dev.luin.file.server.core.file;

import static org.apache.commons.lang3.Validate.*;

import dev.luin.file.server.core.ValueObjectOptional;
import io.vavr.control.Option;
import io.vavr.control.Try;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UserId implements ValueObjectOptional<Long>
{
	Option<Long> value;

	public UserId(Long userId)
	{
		value = Try.of(() -> Option.of(userId))
				.andThen(t -> t.peek(v -> isTrue(v > 0)))
				.get();
	}
}
