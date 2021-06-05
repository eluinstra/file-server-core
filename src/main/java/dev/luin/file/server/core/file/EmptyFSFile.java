package dev.luin.file.server.core.file;

public interface EmptyFSFile
{
	Filename getName();
	ContentType getContentType();
	FileLength getLength();
}
