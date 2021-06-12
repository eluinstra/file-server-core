package dev.luin.file.server.core.server.download.header;

import dev.luin.file.server.core.file.Length;
import dev.luin.file.server.core.server.download.DownloadResponse;
import lombok.NonNull;

public class ContentLength
{
	private static final String HEADER_NAME = "Content-Length";

	public static void write(@NonNull final DownloadResponse response, @NonNull final Length fileLength)
	{
		response.setHeader(HEADER_NAME,fileLength.getStringValue());
	}
}
