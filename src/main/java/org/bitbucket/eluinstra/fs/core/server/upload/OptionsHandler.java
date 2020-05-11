package org.bitbucket.eluinstra.fs.core.server.upload;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bitbucket.eluinstra.fs.core.FSProcessorException;

public class OptionsHandler extends BaseHandler
{
	@Override
	public void handle(final HttpServletRequest request, final HttpServletResponse response) throws IOException, FSProcessorException
	{
		response.setStatus(204);
		response.setHeader("Tus-Resumable","1.0.0");
		response.setHeader("Tus-Version","1.0.0");
		response.setHeader("Tus-Max-Size","" + Long.MAX_VALUE);
		response.setHeader("Tus-Extension","creation");
	}
}
