package org.bitbucket.eluinstra.fs.core.file;

import java.time.Instant;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.bind.DatatypeConverter;
import javax.xml.bind.annotation.adapters.XmlAdapter;

import lombok.val;

public class InstantAdapter extends XmlAdapter<String, Instant>
{
	@Override
	public Instant unmarshal(String v) throws Exception
	{
		return DatatypeConverter.parseDateTime(v).getTime().toInstant();
	}

	@Override
	public String marshal(Instant v) throws Exception
	{
		val calendar = new GregorianCalendar();
		calendar.setTime(Date.from(v));
		return DatatypeConverter.printDateTime(calendar);
//		return DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX").format(ZonedDateTime.ofInstant(v,ZoneId.of("GMT")));
	}
}
