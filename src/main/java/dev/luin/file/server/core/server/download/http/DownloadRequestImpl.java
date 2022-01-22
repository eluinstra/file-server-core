/*
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

import dev.luin.file.server.core.file.VirtualPath;
import dev.luin.file.server.core.server.download.DownloadMethod;
import dev.luin.file.server.core.server.download.DownloadRequest;
import dev.luin.file.server.core.server.download.VirtualPathWithExtension;
import dev.luin.file.server.core.service.user.ClientCertificateManager;
import io.vavr.control.Option;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
public class DownloadRequestImpl implements DownloadRequest
{
	@NonNull
	HttpServletRequest request;

	@Override
	public String getHeader(@NonNull final String headerName)
	{
		return request.getHeader(headerName);
	}

	@Override
	public X509Certificate getClientCertificate()
	{
		return ClientCertificateManager.getCertificate();
	}

	@Override
	public Option<DownloadMethod> getMethod()
	{
		return DownloadMethod.of(request.getMethod());
	}

	@Override
	public VirtualPath getPath()
	{
		return new VirtualPath(parsePathInfo(request.getPathInfo()));
	}

	private @NonNull String parsePathInfo(String pathInfo)
	{
		return pathInfo ==  null ? "" : pathInfo.substring(1);
	}

	@Override
	public VirtualPathWithExtension getVirtualPathWithExtension()
	{
		return new VirtualPathWithExtension(parsePathInfo(request.getPathInfo()));
	}
}
