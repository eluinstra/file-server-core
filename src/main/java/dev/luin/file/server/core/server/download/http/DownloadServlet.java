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

import dev.luin.file.server.core.http.HttpException;
import dev.luin.file.server.core.server.download.DownloadException;
import dev.luin.file.server.core.server.download.DownloadHandler;
import jakarta.servlet.GenericServlet;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.web.context.support.WebApplicationContextUtils;

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DownloadServlet extends GenericServlet
{
	private static final long serialVersionUID = 1L;

	@NonNull
	DownloadHandler downloadHandler;

	@Override
	public void init(final ServletConfig config) throws ServletException
	{
		super.init(config);
		val wac = WebApplicationContextUtils.getRequiredWebApplicationContext(getServletContext());
		downloadHandler = wac.getBean(DownloadHandler.class);
	}

	@Override
	public void service(final ServletRequest request, final ServletResponse response) throws ServletException, IOException
	{
		service((HttpServletRequest)request, (HttpServletResponse)response);
	}

	public void service(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException
	{
		try
		{
			downloadHandler.handle(new DownloadRequestImpl(request)).get().apply(new DownloadResponseImpl(response)).get();
		}
		catch (DownloadException e)
		{
			log.error("", e);
			sendError(response, e.toHttpException());
		}
		catch (Exception e)
		{
			log.error("", e);
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}

	private void sendError(final HttpServletResponse response, final HttpException e)
	{
		response.setStatus(e.getStatusCode());
		e.getHeaders().forEach(response::setHeader);
	}
}
