package dev.luin.file.server.core.file;

import io.vavr.control.Option;

public interface EmptyFSFile
{
	Filename getName();
	ContentType getContentType();
	Option<Length> getLength();
}
