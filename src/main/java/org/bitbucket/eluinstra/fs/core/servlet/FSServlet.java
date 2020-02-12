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
package org.bitbucket.eluinstra.fs.core.servlet;

import java.io.IOException;

import javax.servlet.GenericServlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bitbucket.eluinstra.fs.core.FSProcessorException;
import org.bitbucket.eluinstra.fs.core.server.FSHttpHandler;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class FSServlet extends GenericServlet
{
	private static final long serialVersionUID = 1L;
	private FSHttpHandler httpHandler;

	@Override
	public void init(ServletConfig config) throws ServletException
	{
		super.init(config);
		WebApplicationContext wac = WebApplicationContextUtils.getRequiredWebApplicationContext(getServletContext());
		String id = config.getInitParameter("fsHttpHandler");
		if (id == null)
			id = "fsHttpHandler";
		httpHandler = wac.getBean(id,FSHttpHandler.class);
	}

	@Override
	public void service(ServletRequest request, ServletResponse response) throws ServletException, IOException
	{
		try
		{
			httpHandler.handle((HttpServletRequest)request,(HttpServletResponse)response);
		}
		catch (FSProcessorException e)
		{
			throw new ServletException(e);
		}
	}

}
