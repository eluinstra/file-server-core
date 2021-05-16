package dev.luin.file.server.core.server.upload;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;

import dev.luin.file.server.core.server.upload.header.XHTTPMethodOverride;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
public class UploadRequestImpl implements UploadRequest
{
	HttpServletRequest request;

	@Override
	public String getHeader(String name)
	{
		return request.getHeader(name);
	}

	@Override
	public String getPath()
	{
		return request.getPathInfo();
	}

	@Override
	public String getRequestMethod()
	{
		return XHTTPMethodOverride.of(this).map(h -> h.toString()).getOrElse(request.getMethod());
	}

	@Override
	public String getMethod()
	{
		return request.getMethod();
	}

	@Override
	public InputStream getInputStream() throws IOException
	{
		return request.getInputStream();
	}
}
