package org.bitbucket.eluinstra.fs.core.server.upload;

import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor(staticName = "of")
@Getter
public class Header
{
	TUSHeader header;
	String value;

	public static Header of(HttpServletRequest request, TUSHeader header)
	{
		return new Header(header,request.getHeader(header.getHeaderName()));
	}

	public Optional<Integer> toInteger()
	{
		try
		{
			return Optional.of(Integer.parseInt(value));
		}
		catch (NumberFormatException e)
		{
			return Optional.empty();
		}
	}
}
