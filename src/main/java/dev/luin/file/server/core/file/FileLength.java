package dev.luin.file.server.core.file;

import org.apache.commons.lang3.Validate;

import dev.luin.file.server.core.ValueObjectOptional;
import dev.luin.file.server.core.server.download.range.ContentRange;
import io.vavr.control.Option;
import io.vavr.control.Try;
import lombok.Value;

@Value
public class FileLength implements ValueObjectOptional<Long>
{
	Option<Long> value;

	public FileLength(Long fileLength)
	{
		this(Option.of(fileLength));
	}

	public FileLength(Option<Long> fileLength)
	{
		value = Try.of(() -> fileLength)
				.andThenTry(t -> t.peek(v -> Validate.isTrue(v.compareTo(0L) >= 0)))
				.get();
	}

	public boolean containsFirstPosition(ContentRange range)
	{
		return value.exists(v -> range.getFirst(this) < v);
	}
	
	public String print()
	{
		return value.map(v -> v.toString()).getOrElse("");
	}
}
