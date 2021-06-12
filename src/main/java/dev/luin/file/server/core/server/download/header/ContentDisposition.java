package dev.luin.file.server.core.server.download.header;

import dev.luin.file.server.core.file.Filename;
import dev.luin.file.server.core.server.download.DownloadResponse;
import lombok.NonNull;

public class ContentDisposition
{
	private static final String HEADER_NAME = "Content-Disposition";

	public static void write(@NonNull final DownloadResponse response, @NonNull final Filename filename)
	{
		response.setHeader(HEADER_NAME,"attachment; filename=\"" + filename.getValue() + "\"");
	}

}
