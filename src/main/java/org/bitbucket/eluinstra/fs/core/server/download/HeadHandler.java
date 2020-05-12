package org.bitbucket.eluinstra.fs.core.server.download;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bitbucket.eluinstra.fs.core.file.FileSystem;
import org.bitbucket.eluinstra.fs.core.server.FSHttpException;

import lombok.NonNull;
import lombok.val;

public class HeadHandler extends BaseHandler
{
	public HeadHandler(FileSystem fs)
	{
		super(fs);
	}

	@Override
	public void handle(final HttpServletRequest request, final HttpServletResponse response, @NonNull byte[] clientCertificate) throws IOException
	{
		val path = request.getPathInfo();
		val fsFile = getFs().findFile(clientCertificate,path).orElseThrow(() -> new FSHttpException(404));
		new FSResponseWriter(getFs(),response).setStatus200Headers(fsFile);
	}
}
