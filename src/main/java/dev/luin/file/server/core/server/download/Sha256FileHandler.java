package dev.luin.file.server.core.server.download;

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
		fsFile.getSha256Checksum().forEach(c -> response.sendContent(extension.getDefaultContentType(),c));
	}

}
