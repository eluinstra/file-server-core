package dev.luin.file.server.core.file;

import static org.apache.commons.lang3.Validate.isTrue;

import dev.luin.file.server.core.ValueObject;
import io.vavr.control.Try;
import lombok.Value;

@Value
public class UserId implements ValueObject<Long>
{
	Long value;

	public UserId(Long userId)
	{
		value = Try.success(userId)
				.andThen(v -> isTrue(v > 0))
				.get();
	}
}
