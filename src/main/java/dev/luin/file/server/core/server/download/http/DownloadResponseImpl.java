package dev.luin.file.server.core.server.download.http;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import dev.luin.file.server.core.file.FSFile;
import dev.luin.file.server.core.file.FileSystem;
import dev.luin.file.server.core.server.download.DownloadResponse;
import dev.luin.file.server.core.server.download.range.ContentRange;
import io.vavr.collection.Seq;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
public class DownloadResponseImpl implements DownloadResponse
{
	HttpServletResponse response;

	@Override
	public void sendContent(String contentType, String content) throws IOException
	{
		response.setStatus(HttpServletResponse.SC_OK);
		response.setHeader("Content-Type",contentType);
		response.setHeader("Content-Length",Long.toString(content.length()));
		response.getWriter().write(content);
	}

	@Override
	public void sendFileInfo(FileSystem fs, FSFile fsFile)
	{
		new ResponseWriter(fs,response).writeFileInfo(fsFile);
	}

	@Override
	public void sendFile(FileSystem fs, FSFile fsFile, Seq<ContentRange> ranges) throws IOException
	{
		new ResponseWriter(fs,response).write(fsFile,ranges);
	}
}
