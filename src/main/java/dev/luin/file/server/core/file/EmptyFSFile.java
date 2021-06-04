package dev.luin.file.server.core.file;

public interface EmptyFSFile
{
	Filename getName();
	String getContentType();
	FileLength getLength();
}
