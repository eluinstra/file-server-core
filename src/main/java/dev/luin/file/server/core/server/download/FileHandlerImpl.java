package dev.luin.file.server.core.server.download;

import dev.luin.file.server.core.file.FSFile;
import dev.luin.file.server.core.server.download.header.ContentRange;
import io.vavr.control.Try;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.val;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
public class FileHandlerImpl implements FileHandler
{
	@NonNull
	FSFile fsFile;

	@Override
	public void handle(@NonNull final DownloadRequest request, @NonNull final DownloadResponse response)
	{
		log.info("Download {}",fsFile);
		val ranges = getRanges(request,fsFile);
		sendFile(response,fsFile,ranges);
	}

	private ContentRange getRanges(final DownloadRequest request, final FSFile fsFile)
	{
		if (!fsFile.isCompleted())
			throw DownloadException.fileNotFound(fsFile.getVirtualPath());
		return new ContentRange(request,fsFile);
	}

	private void sendFile(final DownloadResponse response, final FSFile fsFile, final ContentRange ranges)
	{
		Try.success(response)
			.map(ResponseWriter::new)
			.andThenTry(w -> w.write(fsFile,ranges))
			.getOrElseThrow(t -> new IllegalStateException(t));
	}
}
