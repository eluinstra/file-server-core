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
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bitbucket.eluinstra.fs.core.ClientCertificateManager;
import org.bitbucket.eluinstra.fs.core.file.FSFile;
import org.bitbucket.eluinstra.fs.core.file.FileSystem;
import org.bitbucket.eluinstra.fs.core.server.range.ContentRange;
import org.bitbucket.eluinstra.fs.core.server.range.ContentRangeUtils;
import org.bitbucket.eluinstra.fs.core.server.range.ContentRangeUtils.ContentRangeHeader;
import org.bitbucket.eluinstra.fs.core.FSProcessingException;
import org.bitbucket.eluinstra.fs.core.FSProcessorException;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FSHttpHandler
{
	@NonNull
	private FileSystem fs;

	public void handle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response) throws FSProcessorException
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

	private void handleGET(HttpServletRequest request, HttpServletResponse response) throws CertificateEncodingException, IOException
	{
		try
		{
			byte[] clientCertificate = ClientCertificateManager.getEncodedCertificate();
			String path = request.getPathInfo();
			FSFile fsFile = fs.findFile(clientCertificate,path);
			List<ContentRange> ranges = ContentRangeUtils.parseRangeHeader(request.getHeader(ContentRangeHeader.RANGE.getName()));
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
		catch (FileNotFoundException e)
		{
			sendStatus404ErrorMessage(response);
		}
	}

	private void handleHEAD(HttpServletRequest request, HttpServletResponse response) throws CertificateEncodingException, IOException
	{
		try
		{
			byte[] clientCertificate = ClientCertificateManager.getEncodedCertificate();
			String path = request.getPathInfo();
			FSFile fsFile = fs.findFile(clientCertificate,path);
			new FSResponseWriter(fs,response).setStatus200Headers(fsFile);
		}
		catch (FileNotFoundException e)
		{
			sendStatus404ErrorMessage(response);
		}
	}

	private void sendStatus404ErrorMessage(HttpServletResponse response) throws IOException
	{
		response.sendError(404,"File not found!");
	}

	private void sendStatus416ErrorMessage(HttpServletResponse response, FSFile fsFile)
	{
		response.setStatus(416);
		response.setHeader(ContentRangeHeader.CONTENT_RANGE.getName(),"bytes */" + fsFile.getFileLength());
	}

}
