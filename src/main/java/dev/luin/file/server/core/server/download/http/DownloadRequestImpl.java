package dev.luin.file.server.core.server.download.http;

import javax.servlet.http.HttpServletRequest;

import dev.luin.file.server.core.file.FSFile;
import dev.luin.file.server.core.server.download.DownloadException;
import dev.luin.file.server.core.server.download.DownloadMethod;
import dev.luin.file.server.core.server.download.DownloadRequest;
import dev.luin.file.server.core.server.download.range.ContentRange;
import dev.luin.file.server.core.server.download.range.ContentRangeHeader;
import dev.luin.file.server.core.server.download.range.ContentRangeUtils;
import io.vavr.collection.List;
import io.vavr.collection.Seq;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.val;
import lombok.var;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
public class DownloadRequestImpl implements DownloadRequest
{
	HttpServletRequest request;

	@Override
	public DownloadMethod getMethod()
	{
		return DownloadMethod.of(request.getMethod()).getOrElseThrow(() -> DownloadException.methodNotFound(request.getMethod()));
	}

	@Override
	public Seq<ContentRange> getRanges(final FSFile fsFile)
	{
		var ranges = ContentRangeUtils.parseRangeHeader(request.getHeader(ContentRangeHeader.RANGE.getName()));
		if (ranges.size() > 0)
		{
			val lastModified = fsFile.getLastModified();
			if (ContentRangeUtils.validateIfRangeValue(request.getHeader(ContentRangeHeader.IF_RANGE.getName()),lastModified.toEpochMilli()))
			{
				ranges = ContentRangeUtils.filterValidRanges(fsFile.getFileLength(),ranges);
				if (ranges.size() == 0)
					throw DownloadException.requestedRangeNotSatisfiable(fsFile.getLength());
			}
			else
				ranges = List.empty();
		}
		return ranges;
	}

	@Override
	public String getPath()
	{
		return request.getPathInfo();
	}
}
