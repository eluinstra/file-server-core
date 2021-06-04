package dev.luin.file.server.core.file;

import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;

public interface NewFSFile
{
	Filename getName();
	String getContentType();
	String getSha256Checksum();
	Instant getStartDate();
	Instant getEndDate();
	InputStream getInputStream() throws IOException;
}
