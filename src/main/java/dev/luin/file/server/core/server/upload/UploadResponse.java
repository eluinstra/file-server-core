package dev.luin.file.server.core.server.upload;

import lombok.NonNull;

public interface UploadResponse
{
	void setStatus(int statusCode);
	void setStatus(UploadResponseStatus status);
	void setHeader(@NonNull String name, String string);
}
