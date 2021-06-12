package dev.luin.file.server.core.server.download;

import dev.luin.file.server.core.file.FileSystem;
import dev.luin.file.server.core.service.user.User;
import lombok.NonNull;
import lombok.val;

public interface FileHandler
{
	static FileHandler create(@NonNull final FileSystem fs, @NonNull final VirtualPathWithExtension virtualPath, @NonNull final User user)
	{
		val fsFile = fs.findFile(user,virtualPath.getValue()).getOrElseThrow(() -> DownloadException.fileNotFound(virtualPath.getValue().getValue()));
		val extension = virtualPath.getExtension();
		switch(extension)
		{
			case MD5:
				return new Md5FileHandler(fsFile,extension);
			case SHA256:
				return new Sha256FileHandler(fsFile,extension);
			default:
				return new FileHandlerImpl(fsFile);
		}
	}

	void handle(final DownloadRequest request, final DownloadResponse response);
}
