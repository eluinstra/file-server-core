package dev.luin.file.server.core.server.download.header;

import java.time.Instant;
import java.util.Date;

public class ETag
{
	public static int getHashCode(final Instant date)
	{
		return new Date(date.toEpochMilli()).hashCode();
	}
}
