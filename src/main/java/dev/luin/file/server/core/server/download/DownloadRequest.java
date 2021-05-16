package dev.luin.file.server.core.server.download;

import java.io.InputStream;

import dev.luin.file.server.core.server.download.range.ContentRange;
import io.vavr.collection.Seq;

public interface DownloadRequest
{
	String getHeader(String headerName);
	String getPath();
	InputStream getInputStream();
	String getMethod();
	Seq<ContentRange> getContentRanges();
}
