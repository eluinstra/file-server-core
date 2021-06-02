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
package dev.luin.file.server.core.server.download.http;

import javax.servlet.http.HttpServletRequest;

import dev.luin.file.server.core.file.FSFile;
import dev.luin.file.server.core.server.download.DownloadException;
import dev.luin.file.server.core.server.download.DownloadMethod;
import dev.luin.file.server.core.server.download.DownloadRequest;
import dev.luin.file.server.core.server.download.range.ContentRangeHeader;
import dev.luin.file.server.core.server.download.range.ContentRangeUtils;
import dev.luin.file.server.core.server.download.range.ContentRanges;
import dev.luin.file.server.core.service.user.ClientCertificateManager;
import io.vavr.collection.List;
import io.vavr.control.Try;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.val;
import lombok.var;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
public class DownloadRequestImpl implements DownloadRequest
{
	HttpServletRequest request;

	@Override
	public byte[] getClientCertificate()
	{
		return Try.of(() -> ClientCertificateManager.getEncodedCertificate()).getOrElseThrow(t -> new IllegalStateException("No valid certificate found"));
	}

	@Override
	public DownloadMethod getMethod()
	{
		return DownloadMethod.of(request.getMethod()).getOrElseThrow(() -> DownloadException.methodNotFound(request.getMethod()));
	}

	@Override
	public ContentRanges getRanges(final FSFile fsFile)
	{
		var ranges = ContentRangeUtils.parseRangeHeader(request.getHeader(ContentRangeHeader.RANGE.getName()));
		if (ranges.size() > 0)
		{
			val lastModified = fsFile.getLastModified();
			if (ContentRangeUtils.validateIfRangeValue(request.getHeader(ContentRangeHeader.IF_RANGE.getName()),lastModified.toEpochMilli()))
			{
				ranges = ContentRangeUtils.filterValidRanges(fsFile.getFileLength(),ranges);
				if (ranges.size() == 0)
					throw DownloadException.requestedRangeNotSatisfiable(fsFile.getLength());
			}
			else
				ranges = List.empty();
		}
		return new ContentRanges(ranges);
	}

	@Override
	public String getPath()
	{
		return request.getPathInfo();
	}
}
