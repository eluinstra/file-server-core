package org.bitbucket.eluinstra.fs.core.server.upload;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bitbucket.eluinstra.fs.core.file.FileSystem;
import org.bitbucket.eluinstra.fs.core.server.FSHttpException.FSBadRequestException;
import org.bitbucket.eluinstra.fs.core.service.model.Client;

import lombok.NonNull;
import lombok.val;

public class HeadHandler extends BaseHandler
{
	public HeadHandler(FileSystem fs)
	{
		super(fs);
	}

	@Override
	public void handle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, Client client)
	{
		validateTUSHeader(request);
		val path = request.getPathInfo();
		val file = getFs().findFile(client.getCertificate(),path).orElseThrow(() -> new FSBadRequestException());
		response.setStatus(201);
		response.setHeader("Upload-Offset",Long.toString(file.getFileLength()));
		response.setHeader("Tus-Resumable","1.0.0");
	}
}
