package dev.luin.file.server.core.server.download.range;

import io.vavr.collection.Seq;
import io.vavr.collection.Stream;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
public class ContentRanges
{
	Seq<ContentRange> ranges;
	
	public int count()
	{
		return ranges.size();
	}

	public Stream<ContentRange> asStream()
	{
		return ranges.toStream();
	}
}
