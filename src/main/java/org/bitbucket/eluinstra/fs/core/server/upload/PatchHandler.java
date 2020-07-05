package org.bitbucket.eluinstra.fs.core.server.upload;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bitbucket.eluinstra.fs.core.file.FileSystem;
import org.bitbucket.eluinstra.fs.core.service.model.Client;

public class PatchHandler extends BaseHandler
{
	public PatchHandler(FileSystem fs)
	{
		super(fs);
	}

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response, Client client) throws IOException
	{
		validateTUSHeader(request);
		//val file = getFs().findFile(clientCertificate,path).orElseThrow(() -> new FSHttpException(400));
	}
}
