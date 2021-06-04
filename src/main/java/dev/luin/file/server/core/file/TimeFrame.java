package dev.luin.file.server.core.file;

import java.time.Instant;

import io.vavr.control.Option;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.val;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class TimeFrame
{
	Option<Instant> startDate;
	Option<Instant> endDate;

	public TimeFrame(Instant startDate, Instant endDate)
	{
		if (startDate != null && endDate != null && !startDate.isBefore(endDate))
			throw new IllegalStateException("StartDate not before EndDate");
		this.startDate = Option.of(startDate);
		this.endDate = Option.of(endDate);
	}

	public boolean isValid()
	{
		val now = Instant.now();
		return (!startDate.isDefined() || startDate.exists(s -> s.compareTo(now) <= 0)
				&& !endDate.isDefined() || endDate.exists(e -> e.compareTo(now) > 0));
	}
}
