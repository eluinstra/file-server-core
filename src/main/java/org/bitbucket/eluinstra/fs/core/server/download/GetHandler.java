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

import java.io.IOException;
import java.util.Collections;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bitbucket.eluinstra.fs.core.FileExtension;
import org.bitbucket.eluinstra.fs.core.file.FileSystem;
import org.bitbucket.eluinstra.fs.core.server.FSHttpException;
import org.bitbucket.eluinstra.fs.core.server.download.range.ContentRangeUtils;
import org.bitbucket.eluinstra.fs.core.server.download.range.ContentRangeUtils.ContentRangeHeader;

import lombok.NonNull;
import lombok.val;
import lombok.var;

public class GetHandler extends BaseHandler
{
	public GetHandler(FileSystem fs)
	{
		super(fs);
	}

	@Override
	public void handle(final HttpServletRequest request, final HttpServletResponse response, @NonNull byte[] clientCertificate) throws IOException
	{
		val path = request.getPathInfo();
		val extension = FileExtension.getExtension(path);
		val fsFile = getFs().findFile(clientCertificate,extension.getPath(path)).orElseThrow(() -> new FSHttpException(404));
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
