package dev.luin.file.server.core.file;

import java.time.Instant;

import lombok.Value;
import lombok.val;

@Value
public class TimeFrame
{
	Instant startDate;
	Instant endDate;

	public TimeFrame(final Instant startDate, final Instant endDate)
	{
		if (startDate != null && endDate != null && !startDate.isBefore(endDate))
			throw new IllegalStateException("StartDate not before EndDate");
		this.startDate = startDate;
		this.endDate = endDate;
	}

	public boolean hasTimeFrame()
	{
		return startDate != null || endDate != null;
	}

	public boolean isValid()
	{
		val now = Instant.now();
		return (startDate == null || startDate.compareTo(now) <= 0
				&& endDate == null || endDate.compareTo(now) > 0);
	}
}
