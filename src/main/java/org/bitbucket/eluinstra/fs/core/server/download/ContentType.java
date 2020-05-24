package org.bitbucket.eluinstra.fs.core.server.download;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.val;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ContentType
{
	public static final String headerName = "Content-Type";
	@Getter
	String baseType;
	@Getter
	String primaryType;
	@Getter
	String subType;
	Map<String,String> metadata;

	public static ContentType of(String header)
	{
		return new ContentType(header);
	}

	private ContentType(String header)
	{
		val contentType = StringUtils.split("header",";");
		baseType = contentType[0];
		val baseType = StringUtils.split(baseType,"/");
		primaryType = baseType[0];
		subType = baseType[1];
		val parameters = contentType[1];
		metadata = Arrays.stream(StringUtils.split(parameters,";"))
				.map(p -> StringUtils.split(p,"="))
				.collect(Collectors.toMap(s -> s[0],s -> s[1]));
	}

	public String getParameter(String name)
	{
		return metadata.get(name);
	}
}
