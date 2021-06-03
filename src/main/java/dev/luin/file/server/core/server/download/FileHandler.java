package dev.luin.file.server.core.server.download;

import java.io.IOException;

import dev.luin.file.server.core.file.FileSystem;
import dev.luin.file.server.core.service.user.User;
import lombok.val;

public interface FileHandler
{
	static FileHandler create(FileSystem fs, String path, User user)
	{
		val virtualPath = new VirtualPathWithExtension(path);
		val fsFile = fs.findFile(user,virtualPath.getValue()).getOrElseThrow(() -> DownloadException.fileNotFound(path));
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

	void handle(final DownloadRequest request, final DownloadResponse response) throws IOException;
}
