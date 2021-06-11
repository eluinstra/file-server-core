package dev.luin.file.server.core.server.download.header;

import dev.luin.file.server.core.file.FSFile;
import dev.luin.file.server.core.server.download.DownloadException;
import dev.luin.file.server.core.server.download.DownloadRequest;
import io.vavr.collection.List;
import io.vavr.collection.Seq;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.val;
import lombok.var;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ContentRange
{
	private final static String HEADER_NAME = "Range";
	Seq<Range> ranges;
	
	public ContentRange(final DownloadRequest request, final FSFile fsFile)
	{
		var ranges = ContentRangeUtils.parseRangeHeader(request.getHeader(HEADER_NAME));
		if (ranges.size() > 0)
		{
			val lastModified = fsFile.getLastModified();
			if (IfRange.of(request).map(r -> ContentRangeUtils.validateIfRangeValue(r.getValue(),lastModified)).getOrElse(true))
			{
				ranges = ContentRangeUtils.filterValidRanges(fsFile.getFileLength(),ranges);
				if (ranges.size() == 0)
					throw DownloadException.requestedRangeNotSatisfiable(fsFile.getLength());
			}
			else
				ranges = List.empty();
		}
		this.ranges = ranges;
	}

	public int count()
	{
		return ranges.size();
	}
}
