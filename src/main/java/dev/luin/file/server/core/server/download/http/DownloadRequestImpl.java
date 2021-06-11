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

import java.security.cert.X509Certificate;

import javax.servlet.http.HttpServletRequest;

import dev.luin.file.server.core.file.FSFile;
import dev.luin.file.server.core.file.VirtualPath;
import dev.luin.file.server.core.server.download.DownloadException;
import dev.luin.file.server.core.server.download.DownloadMethod;
import dev.luin.file.server.core.server.download.DownloadRequest;
import dev.luin.file.server.core.server.download.VirtualPathWithExtension;
import dev.luin.file.server.core.server.download.header.ContentRange;
import dev.luin.file.server.core.service.user.ClientCertificateManager;
import io.vavr.control.Try;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
public class DownloadRequestImpl implements DownloadRequest
{
	HttpServletRequest request;

	@Override
	public String getHeader(String headerName)
	{
		return request.getHeader(headerName);
	}

	@Override
	public X509Certificate getClientCertificate()
	{
		return Try.of(() -> ClientCertificateManager.getCertificate()).getOrElseThrow(t -> new IllegalStateException("No valid certificate found"));
	}

	@Override
	public DownloadMethod getMethod()
	{
		return DownloadMethod.of(request.getMethod()).getOrElseThrow(() -> DownloadException.methodNotFound(request.getMethod()));
	}

	@Override
	public ContentRange getRanges(final FSFile fsFile)
	{
		return new ContentRange(this,fsFile);
	}

	@Override
	public VirtualPath getPath()
	{
		return new VirtualPath(request.getPathInfo());
	}

	@Override
	public VirtualPathWithExtension getVirtualPathWithExtension()
	{
		return new VirtualPathWithExtension(request.getPathInfo());
	}
}
