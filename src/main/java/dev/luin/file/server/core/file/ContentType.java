package dev.luin.file.server.core.file;

import org.apache.commons.lang3.StringUtils;

import dev.luin.file.server.core.ValueObject;
import io.vavr.control.Option;
import lombok.NonNull;
import lombok.Value;

@Value
public class ContentType implements ValueObject<String>
{
	@NonNull
	String value;

	public ContentType(@NonNull final String contentType)
	{
		value = Option.of(contentType)
				.flatMap(this::parseValue)
				.get();
	}

	private Option<String> parseValue(final String s)
	{
		return s != null ? Option.of(s.split(";")[0].trim()).filter(StringUtils::isNotEmpty) : Option.none();
	}

	public boolean isBinary()
	{
		return !value.matches("^(text/.*|.*/xml)$");
	}
}
