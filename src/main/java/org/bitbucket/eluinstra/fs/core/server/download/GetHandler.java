package org.bitbucket.eluinstra.fs.core.server.download;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.cert.CertificateEncodingException;
import java.util.Collections;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bitbucket.eluinstra.fs.core.FSProcessingException;
import org.bitbucket.eluinstra.fs.core.FSProcessorException;
import org.bitbucket.eluinstra.fs.core.FileExtension;
import org.bitbucket.eluinstra.fs.core.file.FileSystem;
import org.bitbucket.eluinstra.fs.core.server.ClientCertificateManager;
import org.bitbucket.eluinstra.fs.core.server.FSHttpException;
import org.bitbucket.eluinstra.fs.core.server.download.range.ContentRangeUtils;
import org.bitbucket.eluinstra.fs.core.server.download.range.ContentRangeUtils.ContentRangeHeader;

import lombok.val;
import lombok.var;

public class GetHandler extends BaseHandler
{
	public GetHandler(FileSystem fs)
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
			val extension = FileExtension.getExtension(path);
			val fsFile = getFs().findFile(clientCertificate,extension.getPath(path));
			switch(extension)
			{
				case MD5:
					sendStatus200Response(response,extension.getContentType(),fsFile.getMd5checksum());
					break;
				case SHA256:
					sendStatus200Response(response,extension.getContentType(),fsFile.getSha256checksum());
					break;
				default:
					handle(request,response,fsFile);
					break;
			}
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

	private void sendStatus200Response(HttpServletResponse response, String contentType, String content) throws IOException
	{
		response.setStatus(200);
		response.setHeader("Content-Type",contentType);
		response.setHeader("Content-Length",Long.toString(content.length()));
		response.getWriter().write(content);
	}

	private void handle(final HttpServletRequest request, final HttpServletResponse response, final org.bitbucket.eluinstra.fs.core.file.FSFile fsFile) throws IOException
	{
		var ranges = ContentRangeUtils.parseRangeHeader(request.getHeader(ContentRangeHeader.RANGE.getName()));
		if (ranges.size() > 0)
		{
			val lastModified = fsFile.getFileLastModified();
			if (ContentRangeUtils.validateIfRangeHeader(request.getHeader(ContentRangeHeader.IF_RANGE.getName()),lastModified))
			{
				ranges = ContentRangeUtils.filterValidRanges(fsFile.getFileLength(),ranges);
				if (ranges.size() == 0)
				{
					throw new FSHttpException(416,
							Collections.singletonMap(ContentRangeHeader.CONTENT_RANGE.getName(),ContentRangeUtils.createContentRangeHeader(fsFile.getFileLength())));
				}
			}
			else
				ranges.clear();
		}
		new FSResponseWriter(getFs(),response).write(fsFile,ranges);
	}
}
