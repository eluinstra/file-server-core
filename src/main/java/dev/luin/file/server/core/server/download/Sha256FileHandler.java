package dev.luin.file.server.core.server.download;

import dev.luin.file.server.core.file.ContentType;
import dev.luin.file.server.core.file.FSFile;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
public class Sha256FileHandler implements FileHandler
{
	FSFile fsFile;
	Extension extension;

	@Override
	public void handle(DownloadRequest request, DownloadResponse response)
	{
		log.debug("GetSHA256Checksum {}",fsFile);
		sendContent(response,extension.getDefaultContentType(),fsFile.getSha256Checksum().getValue());
	}

	public void sendContent(DownloadResponse response, ContentType contentType, String content)
	{
		response.setStatusOk();
		response.setHeader("Content-Type",contentType.getValue());
		response.setHeader("Content-Length",Long.toString(content.length()));
		response.getWriter().write(content);
	}
}
