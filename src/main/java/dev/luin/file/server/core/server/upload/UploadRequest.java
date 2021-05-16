package dev.luin.file.server.core.server.upload;

import java.io.IOException;
import java.io.InputStream;

public interface UploadRequest
{
	String getHeader(String headerName);
	String getPath();
	String getRequestMethod();
	String getMethod();
	InputStream getInputStream() throws IOException;
}
