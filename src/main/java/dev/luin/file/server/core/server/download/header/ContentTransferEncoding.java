package dev.luin.file.server.core.server.download.header;

import java.io.IOException;
import java.io.OutputStreamWriter;

import dev.luin.file.server.core.server.download.DownloadResponse;
import lombok.NonNull;

public class ContentTransferEncoding
{
	private static final String HEADER_NAME = "Content-Transfer-Encoding";
	private static final String BINARY_VALUE = "binary";
	private static final String BASE64_VALUE = "base64";

	public static void writeBinary(@NonNull final DownloadResponse response)
	{
		response.setHeader(HEADER_NAME,BINARY_VALUE);
	}

	public static void writeBase64(@NonNull final DownloadResponse response)
	{
		response.setHeader(HEADER_NAME,BASE64_VALUE);
	}

	public static void writeBinary(@NonNull final OutputStreamWriter writer) throws IOException
	{
		writer.write(HEADER_NAME + ": " + BINARY_VALUE);
	}

	public static void writeBase64(@NonNull final OutputStreamWriter writer) throws IOException
	{
		writer.write(HEADER_NAME + ": " + BASE64_VALUE);
	}

}
