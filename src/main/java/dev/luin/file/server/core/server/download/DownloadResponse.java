package dev.luin.file.server.core.server.download;

import java.io.IOException;

import dev.luin.file.server.core.file.FSFile;
import dev.luin.file.server.core.file.FileSystem;
import dev.luin.file.server.core.server.download.range.ContentRange;
import io.vavr.collection.Seq;

public interface DownloadResponse
{
	void sendContent(String contentType, String content) throws IOException;
	void sendFileInfo(FileSystem fs, FSFile fsFile);
	void sendFile(FileSystem fileSystem, FSFile fsFile, Seq<ContentRange> ranges) throws IOException;
}
