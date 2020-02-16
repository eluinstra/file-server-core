package org.bitbucket.eluinstra.fs.core;

import java.util.Arrays;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@RequiredArgsConstructor
@ToString
public enum FileExtension
{
	NONE(""), MD5(".md5"), SHA256(".sha256");
	
	@NonNull
	String extension;

	public static FileExtension getExtension(String path)
	{
		return Arrays.stream(FileExtension.values())
			.filter(e -> e != NONE && path.endsWith(e.extension))
			.findFirst().orElse(NONE);
	}

	public String getPath(String path)
	{
		return path.endsWith(extension) ? path.substring(0,path.length() - extension.length()) : path;
	}

	public String getContentType()
	{
		return "text/plain";
	}

}
