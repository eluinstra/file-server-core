package dev.luin.file.server.core.server.upload;

import java.io.IOException;
import java.io.InputStream;

import dev.luin.file.server.core.file.FSFile;
import dev.luin.file.server.core.file.FileSystem;
import dev.luin.file.server.core.server.upload.header.ContentLength;
import dev.luin.file.server.core.server.upload.header.UploadLength;
import dev.luin.file.server.core.server.upload.header.UploadMetadata;
import dev.luin.file.server.core.service.model.User;
import io.vavr.control.Option;

public interface UploadRequest
{
	void validateTusResumable();
	void validateContentType();
	void validateContentLength();
	Option<ContentLength> getContentLength(FSFile file);
	Option<UploadLength> getUploadLength();
	Option<UploadMetadata> getUploadMetadata();
	String getPath();
	UploadMethod getMethod();
	FSFile getFile(User user, FileSystem fs);
	InputStream getInputStream() throws IOException;
}
