package dev.luin.file.server.core.server.download.header;

import java.io.IOException;
import java.io.OutputStreamWriter;

import dev.luin.file.server.core.server.download.DownloadResponse;
import lombok.NonNull;

public class ContentType
{
	private static final String HEADER_NAME = "Content-Type";

	public static void write(@NonNull DownloadResponse response, dev.luin.file.server.core.file.@NonNull ContentType contentType)
	{
		response.setHeader(HEADER_NAME,contentType.getValue());
	}

	public static void writeMultiPart(@NonNull DownloadResponse response, String boundary)
	{
		response.setHeader(HEADER_NAME,"multipart/byteranges; boundary=" + boundary);
	}

	public static void write(OutputStreamWriter writer, dev.luin.file.server.core.file.@NonNull ContentType contentType) throws IOException
	{
		writer.write("Content-Type: " + contentType.getValue());
	}
}
