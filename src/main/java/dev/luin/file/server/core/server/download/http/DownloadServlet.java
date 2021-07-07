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

import static java.util.function.Function.identity;

import java.io.IOException;

import javax.servlet.GenericServlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.context.support.WebApplicationContextUtils;

import dev.luin.file.server.core.http.HttpException;
import dev.luin.file.server.core.server.download.DownloadException;
import dev.luin.file.server.core.server.download.DownloadHandler;
import dev.luin.file.server.core.service.user.UserManagerException;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.val;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@FieldDefaults(level=AccessLevel.PRIVATE)
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
		service((HttpServletRequest)request,(HttpServletResponse)response);
	}

	public void service(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException
	{
		try
		{
			downloadHandler.handle(new DownloadRequestImpl(request))
					.peek(c -> c.accept(new DownloadResponseImpl(response)))
					.getOrElseThrow(identity());
		}
		catch (UserManagerException | DownloadException e)
		{
			log.error("",e);
			sendError(response,e.toHttpException());
		}
		catch (Exception e)
		{
			log.error("",e);
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}

	private void sendError(final HttpServletResponse response, final HttpException e) throws IOException
	{
		response.setStatus(e.getStatusCode());
		e.getHeaders().forEach(response::setHeader);
	}
}
