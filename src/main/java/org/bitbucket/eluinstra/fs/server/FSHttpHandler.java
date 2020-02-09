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
package org.bitbucket.eluinstra.fs.server;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.cert.CertificateEncodingException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bitbucket.eluinstra.fs.ClientCertificateManager;
import org.bitbucket.eluinstra.fs.FSProcessingException;
import org.bitbucket.eluinstra.fs.FSProcessorException;
import org.bitbucket.eluinstra.fs.FileSystem;
import org.bitbucket.eluinstra.fs.model.FSFile;
import org.bitbucket.eluinstra.fs.model.ContentRange;
import org.bitbucket.eluinstra.fs.validation.ContentRangeParser;
import org.bitbucket.eluinstra.fs.validation.ContentRangeValidator;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class FSHttpHandler
{
	private FileSystem fs;

	public void handle(HttpServletRequest request, HttpServletResponse response) throws FSProcessorException
	{
		try
		{
			switch(request.getMethod())
			{
				case "GET":
					handleGet(request,response);
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

	private void handleGet(HttpServletRequest request, HttpServletResponse response) throws CertificateEncodingException, IOException
	{
		try
		{
			byte[] clientCertificate = ClientCertificateManager.getEncodedCertificate();
			String path = request.getPathInfo();
			FSFile fsFile = fs.findFile(clientCertificate,path);
			List<ContentRange> ranges = new ContentRangeParser().parseRangeHeader(request.getHeader("ContentRange"));
			if (ranges.size() > 0)
			{
				ranges = new ContentRangeValidator().validate(fsFile,ranges);
				if (ranges.size() == 0)
				{
					sendStatus416ErrorMessage(response,fsFile);
					return;
				}
			}
			new FSResponseWriter(response).write(fsFile,ranges);
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
			new FSResponseWriter(response).setStatus200Headers(fsFile);
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
		response.setHeader("Content-ContentRange","bytes */" + fsFile.getFile().length());
	}

}
