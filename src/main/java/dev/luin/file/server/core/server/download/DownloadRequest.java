package dev.luin.file.server.core.server.download;

import dev.luin.file.server.core.file.FSFile;
import dev.luin.file.server.core.server.download.range.ContentRange;
import io.vavr.collection.Seq;

public interface DownloadRequest
{
	DownloadMethod getMethod();
	Seq<ContentRange> getRanges(FSFile fsFile);
	String getPath();
}
