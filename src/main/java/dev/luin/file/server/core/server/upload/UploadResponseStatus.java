package dev.luin.file.server.core.server.upload;

import javax.servlet.http.HttpServletResponse;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
@Getter
public enum UploadResponseStatus
{
	CREATED(HttpServletResponse.SC_CREATED),
	NO_CONTENT(HttpServletResponse.SC_NO_CONTENT),
	INTERNAL_SERVER_ERROR(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
	
	int statusCode;
}
