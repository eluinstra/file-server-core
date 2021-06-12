package dev.luin.file.server.core.server.download.header;

import java.time.Instant;
import java.util.Date;

import dev.luin.file.server.core.server.download.DownloadResponse;
import lombok.NonNull;

public class ETag
{
	public static void write(@NonNull final DownloadResponse response, final Instant lastModified)
	{
		response.setHeader("ETag","\"" + ETag.getHashCode(lastModified) + "\"");
	}

	static int getHashCode(@NonNull final Instant date)
	{
		return new Date(date.toEpochMilli()).hashCode();
	}
}
