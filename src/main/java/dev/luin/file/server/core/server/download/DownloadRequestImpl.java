package dev.luin.file.server.core.server.download;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;

import dev.luin.file.server.core.server.download.range.ContentRange;
import dev.luin.file.server.core.server.download.range.ContentRangeHeader;
import dev.luin.file.server.core.server.download.range.ContentRangeUtils;
import io.vavr.collection.Seq;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
public class DownloadRequestImpl implements DownloadRequest
{
	HttpServletRequest request;

	@Override
	public String getHeader(String name)
	{
		return request.getHeader(name);
	}

	@Override
	public Seq<ContentRange> getContentRanges()
	{
		return ContentRangeUtils.parseRangeHeader(request.getHeader(ContentRangeHeader.RANGE.getName()));
	}

	@Override
	public String getPath()
	{
		return request.getPathInfo();
	}

	@Override
	public String getMethod()
	{
		return request.getMethod();
	}

	@Override
	public InputStream getInputStream()
	{
		try
		{
			return request.getInputStream();
		}
		catch (IOException e)
		{
			throw new DownloadException(e);
		}
	}
}
