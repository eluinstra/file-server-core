package org.bitbucket.eluinstra.fs.core.server.download.range;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
@Getter
public enum ContentRangeHeader
{
	ACCEPT_RANGES("Accept-Ranges"), CONTENT_RANGE("Content-Range"), IF_RANGE("If-Range"), RANGE("Range");
	
	String name;
}
