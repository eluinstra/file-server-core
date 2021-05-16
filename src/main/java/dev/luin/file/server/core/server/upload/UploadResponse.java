package dev.luin.file.server.core.server.upload;

import dev.luin.file.server.core.file.FSFile;

public interface UploadResponse
{
	void sendTusOptionsResponse();
	void sendFileInfoResponse(FSFile file);
	void sendCreateFileResponse(FSFile file, String uploadPath);
	void sendUploadFileResponse(FSFile file);
	void sendDeleteFileResponse();
}
