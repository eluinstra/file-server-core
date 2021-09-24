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
package dev.luin.file.server.core.service.user;

import java.security.cert.X509Certificate;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ClientCertificateManager
{
	private static final ThreadLocal<X509Certificate> certificateHolder = new ThreadLocal<>();
	
	public static X509Certificate getCertificate()
	{
		return certificateHolder.get();
	}

	public static void setCertificate(X509Certificate certificate)
	{
		if (certificate == null)
			certificateHolder.remove();
		else
			certificateHolder.set(certificate);
	}
}
