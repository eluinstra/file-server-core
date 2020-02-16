/**
 * Copyright 2020 E.Luinstra
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.bitbucket.eluinstra.fs.core.server;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.cert.CertificateEncodingException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bitbucket.eluinstra.fs.core.FSProcessingException;
import org.bitbucket.eluinstra.fs.core.FSProcessorException;
import org.bitbucket.eluinstra.fs.core.FileExtension;
import org.bitbucket.eluinstra.fs.core.file.FSFile;
import org.bitbucket.eluinstra.fs.core.file.FileSystem;
import org.bitbucket.eluinstra.fs.core.server.range.ContentRangeUtils;
import org.bitbucket.eluinstra.fs.core.server.range.ContentRangeUtils.ContentRangeHeader;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;
import lombok.var;
import lombok.experimental.FieldDefaults;

@RequiredArgsConstructor
@FieldDefaults(level=AccessLevel.PRIVATE, makeFinal=true)
public class FSHttpHandler
{
	@NonNull
	FileSystem fs;

	public void handle(@NonNull final HttpServletRequest request, @NonNull final HttpServletResponse response) throws FSProcessorException
	{
		try
		{
			switch(request.getMethod())
			{
				case "GET":
					handleGET(request,response);
					break;
				case "HEAD":
					handleHEAD(request,response);
					break;
				default:
					sendStatus404ErrorMessage(response);
					break;
			}
		}
		catch (CertificateEncodingException e)
		{
			throw new FSProcessingException(e);
		}
		catch (IOException e)
		{
			throw new FSProcessorException(e);
		}
	}

	private void handleGET(final HttpServletRequest request, final HttpServletResponse response) throws CertificateEncodingException, IOException
	{
		try
		{
			val clientCertificate = ClientCertificateManager.getEncodedCertificate();
			val path = request.getPathInfo();
			val extension = FileExtension.getExtension(path);
			val fsFile = fs.findFile(clientCertificate,extension.getPath(path));
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
		catch (FileNotFoundException e)
		{
			sendStatus404ErrorMessage(response);
		}
	}

	private void handle(final HttpServletRequest request, final HttpServletResponse response, final org.bitbucket.eluinstra.fs.core.file.FSFile fsFile) throws IOException
	{
		var ranges = ContentRangeUtils.parseRangeHeader(request.getHeader(ContentRangeHeader.RANGE.getName()));
		if (ranges.size() > 0)
		{
			long lastModified = fsFile.getFileLastModified();
			if (ContentRangeUtils.validateIfRangeHeader(request.getHeader(ContentRangeHeader.IF_RANGE.getName()),lastModified))
			{
				ranges = ContentRangeUtils.filterValidRanges(fsFile.getFileLength(),ranges);
				if (ranges.size() == 0)
				{
					sendStatus416ErrorMessage(response,fsFile);
					return;
				}
			}
			else
				ranges.clear();
		}
		new FSResponseWriter(fs,response).write(fsFile,ranges);
	}

	private void handleHEAD(final HttpServletRequest request, final HttpServletResponse response) throws CertificateEncodingException, IOException
	{
		try
		{
			val clientCertificate = ClientCertificateManager.getEncodedCertificate();
			val path = request.getPathInfo();
			val fsFile = fs.findFile(clientCertificate,path);
			new FSResponseWriter(fs,response).setStatus200Headers(fsFile);
		}
		catch (FileNotFoundException e)
		{
			sendStatus404ErrorMessage(response);
		}
	}

	public void sendStatus200Response(HttpServletResponse response, String contentType, String content) throws IOException
	{
		response.setStatus(200);
		response.setHeader("Content-Type",contentType);
		response.setHeader("Content-Length",Long.toString(content.length()));
		response.getWriter().write(content);
	}

	private void sendStatus404ErrorMessage(final HttpServletResponse response) throws IOException
	{
		response.sendError(404,"File not found!");
	}

	private void sendStatus416ErrorMessage(final HttpServletResponse response, final FSFile fsFile)
	{
		response.setStatus(416);
		response.setHeader(ContentRangeHeader.CONTENT_RANGE.getName(),ContentRangeUtils.createContentRangeHeader(fsFile.getFileLength()));
	}

}
