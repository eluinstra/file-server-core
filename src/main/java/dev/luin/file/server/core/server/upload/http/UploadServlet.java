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

import javax.servlet.GenericServlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.context.support.WebApplicationContextUtils;

import dev.luin.file.server.core.http.HttpException;
import dev.luin.file.server.core.server.upload.UploadException;
import dev.luin.file.server.core.server.upload.UploadHandler;
import dev.luin.file.server.core.service.user.UserManagerException;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.val;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@FieldDefaults(level=AccessLevel.PRIVATE)
public class UploadServlet extends GenericServlet
{
	private static final long serialVersionUID = 1L;
	@NonNull
	UploadHandler uploadHandler;

	@Override
	public void init(final ServletConfig config) throws ServletException
	{
		super.init(config);
		val applicationContext = WebApplicationContextUtils.getRequiredWebApplicationContext(getServletContext());
		uploadHandler = applicationContext.getBean(UploadHandler.class);
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
			uploadHandler.handle(new UploadRequestImpl(request),new UploadResponseImpl(response));
		}
		catch (UserManagerException e)
		{
			log.error("",e);
			sendError(response,e.toHttpException());
		}
		catch (UploadException e)
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

	private void sendError(final HttpServletResponse response, HttpException e) throws IOException
	{
		response.setStatus(e.getStatusCode());
		e.getHeaders().forEach((k,v) -> response.setHeader(k,v));
	}
}
