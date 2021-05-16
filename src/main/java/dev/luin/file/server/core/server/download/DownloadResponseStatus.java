package dev.luin.file.server.core.server.download;

import javax.servlet.http.HttpServletResponse;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
@Getter
public enum DownloadResponseStatus
{
	OK(HttpServletResponse.SC_OK),
	NOT_FOUND(HttpServletResponse.SC_NOT_FOUND),
	PARTIAL_CONTENT(HttpServletResponse.SC_PARTIAL_CONTENT),
	INTERNAL_SERVER_ERROR(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
	
	int statusCode;
}
