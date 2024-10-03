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
package dev.luin.file.server.core.server.upload.http;

import com.google.common.util.concurrent.RateLimiter;
import dev.luin.file.server.core.file.VirtualPath;
import dev.luin.file.server.core.server.servlet.throttling.ThrottlingInputStream;
import dev.luin.file.server.core.server.upload.UploadMethod;
import dev.luin.file.server.core.server.upload.UploadRequest;
import dev.luin.file.server.core.server.upload.header.XHTTPMethodOverride;
import dev.luin.file.server.core.service.user.ClientCertificateManager;
import io.vavr.control.Option;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.security.cert.X509Certificate;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
public class UploadRequestImpl implements UploadRequest
{
	@NonNull
	HttpServletRequest request;
	@NonNull
	RateLimiter rateLimiter;

	@Override
	public X509Certificate getClientCertificate()
	{
		return ClientCertificateManager.getCertificate();
	}

	@Override
	public String getHeader(@NonNull final String headerName)
	{
		return request.getHeader(headerName);
	}

	@Override
	public VirtualPath getPath()
	{
		return new VirtualPath(parsePathInfo(request.getPathInfo()));
	}

	private @NonNull String parsePathInfo(String pathInfo)
	{
		return pathInfo == null ? "" : pathInfo.substring(1);
	}

	@Override
	public Option<UploadMethod> getMethod()
	{
		return UploadMethod.getMethod(request.getMethod(), () -> XHTTPMethodOverride.get(this));
	}

	@Override
	public InputStream getInputStream() throws IOException
	{
		return new ThrottlingInputStream(rateLimiter, request.getInputStream());
	}
}
