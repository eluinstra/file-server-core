package dev.luin.file.server.core.server.download;

import dev.luin.file.server.core.file.FSFile;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.val;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
public class FileHandlerImpl implements FileHandler
{
	FSFile fsFile;

	@Override
	public void handle(DownloadRequest request, DownloadResponse response)
	{
		log.info("Download {}",fsFile);
		handle(request,response,fsFile);
	}

	private void handle(final DownloadRequest request, final DownloadResponse response, final FSFile fsFile)
	{
		if (!fsFile.isCompleted())
			throw DownloadException.fileNotFound(fsFile.getVirtualPath());
		val ranges = request.getRanges(fsFile);
		response.sendFile(fsFile,ranges);
	}
}
