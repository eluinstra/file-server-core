package dev.luin.file.server.core.server.upload;

import javax.servlet.http.HttpServletResponse;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
public class UploadResponseImpl implements UploadResponse
{
	HttpServletResponse response;

	@Override
	public void setStatus(int statusCode)
	{
		response.setStatus(statusCode);
	}

	@Override
	public void setStatus(UploadResponseStatus status)
	{
		response.setStatus(status.getStatusCode());
	}

	@Override
	public void setHeader(@NonNull String name, String value)
	{
		response.setHeader(name,value);
	}
}
