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
package org.bitbucket.eluinstra.fs.core.server.download;

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bitbucket.eluinstra.fs.core.FileExtension;
import org.bitbucket.eluinstra.fs.core.file.FSFile;
import org.bitbucket.eluinstra.fs.core.file.FileSystem;
import org.bitbucket.eluinstra.fs.core.http.HttpException;
import org.bitbucket.eluinstra.fs.core.server.BaseHandler;
import org.bitbucket.eluinstra.fs.core.server.download.range.ContentRangeHeader;
import org.bitbucket.eluinstra.fs.core.server.download.range.ContentRangeUtils;
import org.bitbucket.eluinstra.fs.core.service.model.User;

import io.vavr.collection.HashMap;
import io.vavr.collection.List;
import lombok.val;
import lombok.var;

public class GetHandler extends BaseHandler
{
	public GetHandler(FileSystem fs)
	{
		super(fs);
	}

	@Override
	public void handle(final HttpServletRequest request, final HttpServletResponse response, User user) throws IOException
	{
		val path = request.getPathInfo();
		val extension = FileExtension.getExtension(path);
		val fsFile = getFs().findFile(user,extension.getPath(path)).getOrElseThrow(() -> HttpException.notFound());
		switch(extension)
		{
			case MD5:
				sendStatus200Response(response,extension.getContentType(),fsFile.getMd5Checksum());
				break;
			case SHA256:
				sendStatus200Response(response,extension.getContentType(),fsFile.getSha256Checksum());
				break;
			default:
				handle(request,response,fsFile);
				break;
		}
	}

	private void sendStatus200Response(HttpServletResponse response, String contentType, String content) throws IOException
	{
		response.setStatus(HttpServletResponse.SC_OK);
		response.setHeader("Content-Type",contentType);
		response.setHeader("Content-Length",Long.toString(content.length()));
		response.getWriter().write(content);
	}

	private void handle(final HttpServletRequest request, final HttpServletResponse response, final FSFile fsFile) throws IOException
	{
		if (!fsFile.isCompleted())
			throw new FileNotFoundException(fsFile.getVirtualPath());
		var ranges = ContentRangeUtils.parseRangeHeader(request.getHeader(ContentRangeHeader.RANGE.getName()));
		if (ranges.size() > 0)
		{
			val lastModified = fsFile.getLastModified();
			if (ContentRangeUtils.validateIfRangeHeader(request.getHeader(ContentRangeHeader.IF_RANGE.getName()),lastModified.toEpochMilli()))
			{
				ranges = ContentRangeUtils.filterValidRanges(fsFile.getLength(),ranges);
				if (ranges.size() == 0)
					throw FSHttpException.requestedRangeNotSatisfiable(HashMap.of(ContentRangeHeader.CONTENT_RANGE.getName(),ContentRangeUtils.createContentRangeHeader(fsFile.getFileLength())));
			}
			else
				ranges = List.empty();
		}
		new FSResponseWriter(getFs(),response).write(fsFile,ranges);
	}
}
