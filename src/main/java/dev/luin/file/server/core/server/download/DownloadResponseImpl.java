package dev.luin.file.server.core.server.download;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
public class DownloadResponseImpl implements DownloadResponse
{
	HttpServletResponse response;

	@Override
	public void setStatus(int statusCode)
	{
		response.setStatus(statusCode);
	}

	@Override
	public void setStatus(DownloadResponseStatus status)
	{
		response.setStatus(status.getStatusCode());
	}

	@Override
	public void setHeader(@NonNull String name, String value)
	{
		response.setHeader(name,value);
	}

	@Override
	public OutputStream getOutputStream() throws IOException
	{
		return response.getOutputStream();
	}

	@Override
	public void write(String s) throws IOException
	{
		response.getWriter().write(s);
	}
}
