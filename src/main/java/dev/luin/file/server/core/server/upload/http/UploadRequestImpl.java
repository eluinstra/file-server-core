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
package dev.luin.file.server.core.server.upload.http;

import java.io.IOException;
import java.io.InputStream;
import java.security.cert.X509Certificate;

import javax.servlet.http.HttpServletRequest;

import dev.luin.file.server.core.file.VirtualPath;
import dev.luin.file.server.core.server.upload.UploadMethod;
import dev.luin.file.server.core.server.upload.UploadRequest;
import dev.luin.file.server.core.server.upload.header.XHTTPMethodOverride;
import dev.luin.file.server.core.service.user.ClientCertificateManager;
import io.vavr.control.Option;
import io.vavr.control.Try;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.val;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
public class UploadRequestImpl implements UploadRequest
{
	@NonNull
	HttpServletRequest request;

	@Override
	public X509Certificate getClientCertificate()
	{
		return Try.of(() -> ClientCertificateManager.getCertificate()).getOrElseThrow(t -> new IllegalStateException("No valid certificate found"));
	}

	@Override
	public String getHeader(@NonNull final String headerName)
	{
		return request.getHeader(headerName);
	}

	@Override
	public VirtualPath getPath()
	{
		return new VirtualPath(request.getPathInfo());
	}

	@Override
	public Option<UploadMethod> getMethod()
	{
		val method = XHTTPMethodOverride.get(this).map(h -> h.toString()).getOrElse(request.getMethod());
		return UploadMethod.of(method);
	}

	@Override
	public InputStream getInputStream() throws IOException
	{
		return request.getInputStream();
	}
}
