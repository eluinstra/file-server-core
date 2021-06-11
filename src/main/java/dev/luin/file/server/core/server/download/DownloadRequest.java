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
package dev.luin.file.server.core.server.download;

import java.security.cert.X509Certificate;

import dev.luin.file.server.core.file.FSFile;
import dev.luin.file.server.core.file.VirtualPath;
import dev.luin.file.server.core.server.download.header.ContentRange;

public interface DownloadRequest
{
	String getHeader(String headerName);
	X509Certificate getClientCertificate();
	DownloadMethod getMethod();
	ContentRange getRanges(FSFile fsFile);
	VirtualPath getPath();
	VirtualPathWithExtension getVirtualPathWithExtension();
}
