package dev.luin.file.server.core.server.download.header;

import dev.luin.file.server.core.ValueObject;
import dev.luin.file.server.core.server.download.DownloadRequest;
import io.vavr.control.Option;
import lombok.NonNull;
import lombok.Value;

@Value
public class IfRange implements ValueObject<String>
{
	private final static String HEADER_NAME = "If-Range";
	String value;

	public static Option<IfRange> of(final DownloadRequest request)
	{
		return of(request.getHeader(HEADER_NAME));
	}

	public static Option<IfRange> of(final String ifRange)
	{
		return Option.of(ifRange).map(IfRange::new);
	}

	private IfRange(@NonNull final String ifRange)
	{
		value = ifRange;
	}
}
