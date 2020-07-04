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
package org.bitbucket.eluinstra.fs.core.server.upload;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bitbucket.eluinstra.fs.core.ClientManager;
import org.bitbucket.eluinstra.fs.core.file.FileSystem;

import lombok.NonNull;

public class OptionsHandler extends BaseHandler
{
	public OptionsHandler(@NonNull FileSystem fs, @NonNull ClientManager clientManager)
	{
		super(fs,clientManager);
	}

	@Override
	public void handle(final HttpServletRequest request, final HttpServletResponse response, byte[] clientCertificate) throws IOException
	{
		response.setStatus(204);
		response.setHeader("Tus-Version","1.0.0");
		response.setHeader("Tus-Max-Size",String.valueOf(Long.MAX_VALUE));
		response.setHeader("Tus-Extension","creation");
	}
}
