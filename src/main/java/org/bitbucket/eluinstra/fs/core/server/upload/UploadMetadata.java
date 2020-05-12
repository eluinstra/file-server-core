package org.bitbucket.eluinstra.fs.core.server.upload;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UploadMetadata
{
	public static final String headerName = "Upload-Metadata";
	Map<String,String> metadata;

	public static UploadMetadata of(String header)
	{
		return new UploadMetadata(header);
	}

	private UploadMetadata(String header)
	{
		metadata = Arrays.stream(StringUtils.split(header,","))
				.map(p -> StringUtils.split(p," "))
				.collect(Collectors.toMap(s -> s[0],s -> new String(Base64.decodeBase64(s[1]))));
	}

	public String getParameter(String name)
	{
		return metadata.get(name);
	}

}
