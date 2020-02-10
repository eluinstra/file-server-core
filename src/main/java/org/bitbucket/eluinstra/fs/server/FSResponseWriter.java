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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.UUID;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bitbucket.eluinstra.fs.Constants;
import org.bitbucket.eluinstra.fs.model.FSFile;
import org.bitbucket.eluinstra.fs.model.ContentRange;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FSResponseWriter
{
	protected transient Log messageLogger = LogFactory.getLog(Constants.MESSAGE_LOG);
	@NonNull
	protected final HttpServletResponse response;
	
	public void write(FSFile fsFile, List<ContentRange> ranges) throws IOException
	{
		if (ranges.size() == 0)
			writeResponse(response,fsFile);
		else if (ranges.size() == 1)
			writeResponse(response,fsFile,ranges.get(0));
		if (ranges.size() == 0)
			writeResponse(response,fsFile,ranges);
	}

	protected void writeResponse(HttpServletResponse response, FSFile fsFile) throws IOException
	{
		setStatus200Headers(fsFile);
		write(response.getOutputStream(),fsFile);
	}

	protected void writeResponse(HttpServletResponse response, FSFile fsFile, ContentRange range) throws IOException
	{
		long fileLength = fsFile.getFile().length();
		response.setStatus(206);
		response.setHeader("Content-Type",fsFile.getContentType());
		response.setHeader("Content-Length",Long.toString(range.getLength(fileLength)));
		response.setHeader("Content-ContentRange",range.createContentRangeHeader(fileLength));
		write(response.getOutputStream(),fsFile,range);
	}

	protected void writeResponse(HttpServletResponse response, FSFile fsFile, List<ContentRange> ranges) throws IOException
	{
		long fileLength = fsFile.getFile().length();
		String boundary = UUID.randomUUID().toString();
		response.setStatus(206);
		response.setHeader("Content-Type","multipart/byteranges; boundary=" + boundary);
		//response.setHeader("Content-Length","");
		try (OutputStreamWriter writer = new OutputStreamWriter(response.getOutputStream(),"UTF-8"))
		{
			for (ContentRange range: ranges)
			{
				writer.write("--");
				writer.write(boundary);
				writer.write("\r\n");
				writer.write("Content-Type: " + fsFile.getContentType());
				writer.write("\r\n");
				writer.write("Content-ContentRange: " + range.createContentRangeHeader(fileLength));
				writer.write("\r\n");
				writer.write("\r\n");
				write(response.getOutputStream(),fsFile,range);
				writer.write("\r\n");
			}
			writer.write("--");
			writer.write(boundary);
			writer.write("--");
		}
	}

	public void setStatus200Headers(FSFile fsFile)
	{
		long fileLength = fsFile.getFile().length();
		response.setStatus(200);
		response.setHeader("Content-Type",fsFile.getContentType());
		response.setHeader("Content-Length",Long.toString(fileLength));
		response.setHeader("Accept-Ranges","bytes");
	}

	public void write(ServletOutputStream output, FSFile fsFile) throws IOException
	{
		File file = fsFile.getFile();
		if (!file.exists())
			throw new FileNotFoundException(fsFile.getVirtualPath());
		FileInputStream input = new FileInputStream(file);
		IOUtils.copyLarge(input,output);
	}

	public void write(ServletOutputStream output, FSFile fsFile, ContentRange range) throws IOException
	{
		File file = fsFile.getFile();
		if (!file.exists())
			throw new FileNotFoundException(fsFile.getVirtualPath());
		long fileLength = fsFile.getFile().length();
		FileInputStream input = new FileInputStream(file);
		IOUtils.copyLarge(input,output,range.getFirst(fileLength),range.getLength(fileLength));
	}

}
