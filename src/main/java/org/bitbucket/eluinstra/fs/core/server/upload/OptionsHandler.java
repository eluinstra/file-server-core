package org.bitbucket.eluinstra.fs.core.server.upload;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bitbucket.eluinstra.fs.core.ClientManager;
import org.bitbucket.eluinstra.fs.core.file.FileSystem;

import lombok.NonNull;

public class OptionsHandler extends BaseHandler
{
	public OptionsHandler(@NonNull FileSystem fs, @NonNull ClientManager clientManager)
	{
		super(fs,clientManager);
	}

	@Override
	public void handle(final HttpServletRequest request, final HttpServletResponse response, byte[] clientCertificate) throws IOException
	{
		response.setStatus(204);
		response.setHeader("Tus-Version","1.0.0");
		response.setHeader("Tus-Max-Size","" + Long.MAX_VALUE);
		response.setHeader("Tus-Extension","creation");
	}
}
