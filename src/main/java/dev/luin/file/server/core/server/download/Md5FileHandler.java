package dev.luin.file.server.core.server.download;

import dev.luin.file.server.core.file.ContentType;
import dev.luin.file.server.core.file.FSFile;
import dev.luin.file.server.core.file.Length;
import dev.luin.file.server.core.server.download.header.ContentLength;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
public class Md5FileHandler implements FileHandler
{
	@NonNull
	FSFile fsFile;
	@NonNull
	Extension extension;

	@Override
	public void handle(@NonNull final DownloadRequest request, @NonNull final DownloadResponse response)
	{
		log.debug("GetMD5Checksum {}",fsFile);
		sendContent(response,extension.getDefaultContentType(),fsFile.getMd5Checksum().getValue());
	}

	private void sendContent(final DownloadResponse response, final ContentType contentType, final String content)
	{
		response.setStatusOk();
		dev.luin.file.server.core.server.download.header.ContentType.write(response,contentType);
		ContentLength.write(response,new Length(content.length()));
		response.getWriter().write(content);
	}
}
