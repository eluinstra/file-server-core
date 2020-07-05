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
package org.bitbucket.eluinstra.fs.core.server;

import java.util.Collections;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.xml.ws.http.HTTPException;

import org.bitbucket.eluinstra.fs.core.server.upload.TUSHeader;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Getter
public class FSHttpException extends HTTPException
{
	private static final long serialVersionUID = 1L;
	String message;
	Map<String,String> headers;

	private FSHttpException(int statusCode)
	{
		this(statusCode,null,Collections.emptyMap());
	}

	private FSHttpException(int statusCode, String message)
	{
		this(statusCode,message,Collections.emptyMap());
	}

	private FSHttpException(int statusCode, Map<String,String> headers)
	{
		this(statusCode,null,headers);
	}

	private FSHttpException(int statusCode, String message, Map<String,String> headers)
	{
		super(statusCode);
		this.message = message;
		this.headers = headers;
	}

	public static class FSBadRequestException extends FSHttpException
	{
		private static final long serialVersionUID = 1L;

		public FSBadRequestException()
		{
			super(HttpServletResponse.SC_BAD_REQUEST);
		}
	}

	public static class FSInvalidHeaderException extends FSHttpException
	{
		private static final long serialVersionUID = 1L;

		public FSInvalidHeaderException(TUSHeader header)
		{
			super(HttpServletResponse.SC_BAD_REQUEST,"Invalid or missing " + header.getHeaderName() + " header");
		}
	}

	public static class FSUnauthorizedException extends FSHttpException
	{
		private static final long serialVersionUID = 1L;

		public FSUnauthorizedException()
		{
			super(HttpServletResponse.SC_UNAUTHORIZED);
		}
	}

	public static class FSNotFoundException extends FSHttpException
	{
		private static final long serialVersionUID = 1L;

		public FSNotFoundException()
		{
			super(HttpServletResponse.SC_NOT_FOUND);
		}
	}

	public static class FSMethodNotAllowedException extends FSHttpException
	{
		private static final long serialVersionUID = 1L;

		public FSMethodNotAllowedException(String method)
		{
			super(HttpServletResponse.SC_METHOD_NOT_ALLOWED,"Method " + method + " not allowed");
		}
	}

	public static class FSPreconditionFailedException extends FSHttpException
	{
		private static final long serialVersionUID = 1L;

		public FSPreconditionFailedException(Map<String,String> headers)
		{
			super(HttpServletResponse.SC_PRECONDITION_FAILED,headers);
		}
	}

	public static class FSRequestedRangeNotSatisfiableException extends FSHttpException
	{
		private static final long serialVersionUID = 1L;
		
		public FSRequestedRangeNotSatisfiableException(Map<String,String> headers)
		{
			super(HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE,headers);
		}
	}
}
