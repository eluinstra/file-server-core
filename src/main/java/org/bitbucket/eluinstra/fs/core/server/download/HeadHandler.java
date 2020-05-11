package org.bitbucket.eluinstra.fs.core.server.download;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.cert.CertificateEncodingException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bitbucket.eluinstra.fs.core.FSProcessingException;
import org.bitbucket.eluinstra.fs.core.FSProcessorException;
import org.bitbucket.eluinstra.fs.core.file.FileSystem;
import org.bitbucket.eluinstra.fs.core.server.ClientCertificateManager;
import org.bitbucket.eluinstra.fs.core.server.FSHttpException;

import lombok.val;

public class HeadHandler extends BaseHandler
{
	public HeadHandler(FileSystem fs)
	{
		super(fs);
	}

	@Override
	public void handle(final HttpServletRequest request, final HttpServletResponse response) throws IOException, FSProcessorException
	{
		try
		{
			val clientCertificate = ClientCertificateManager.getEncodedCertificate();
			val path = request.getPathInfo();
			val fsFile = getFs().findFile(clientCertificate,path);
			new FSResponseWriter(getFs(),response).setStatus200Headers(fsFile);
		}
		catch (CertificateEncodingException e)
		{
			throw new FSProcessingException(e);
		}
		catch (FileNotFoundException e)
		{
			throw new FSHttpException(404,"File not found!");
		}
	}
}
