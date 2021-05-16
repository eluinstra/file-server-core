package dev.luin.file.server.core.server.download;

import java.io.IOException;
import java.io.OutputStream;

import lombok.NonNull;

public interface DownloadResponse
{
	void setStatus(DownloadResponseStatus status);
	void setHeader(@NonNull String name, String string);

	void setStatus(int scNotFound);
	OutputStream getOutputStream() throws IOException;
	void write(String s) throws IOException;
}
