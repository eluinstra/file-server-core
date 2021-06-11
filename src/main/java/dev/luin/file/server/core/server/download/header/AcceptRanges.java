package dev.luin.file.server.core.server.download.header;

import dev.luin.file.server.core.server.download.DownloadResponse;
import lombok.NonNull;

public class AcceptRanges
{
	private final static String HEADER_NAME = "Accept-Ranges";

	public static void write(@NonNull DownloadResponse response)
	{
		response.setHeader(HEADER_NAME,"bytes");
	}
}
