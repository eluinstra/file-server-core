package dev.luin.fs.core.server.download.range;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Getter
enum HttpDate
{
	IMF_FIXDATE(new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z",Locale.ENGLISH)),
	RFC_850(new SimpleDateFormat("EEEE, dd-MMM-yy HH:mm:ss z",Locale.ENGLISH)),
	ANSI_C(new SimpleDateFormat("EEE MMM  d HH:mm:ss yyyy",Locale.ENGLISH));

	DateFormat dateFormat;

	HttpDate(@NonNull final DateFormat dateFormat)
	{
		dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
		this.dateFormat = dateFormat;
	}
}
