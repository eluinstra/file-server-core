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
package dev.luin.file.server.core.http;

import javax.servlet.http.HttpServletResponse;
import javax.xml.ws.http.HTTPException;

import io.vavr.collection.HashMap;
import io.vavr.collection.Map;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Getter
public class HttpException extends HTTPException
{
	private static final long serialVersionUID = 1L;
	String message;
	@NonNull
	Map<String,String> headers;

	public HttpException(int statusCode)
	{
		this(statusCode,null,HashMap.empty());
	}

	public HttpException(int statusCode, @NonNull String message)
	{
		this(statusCode,message,HashMap.empty());
	}

	public HttpException(int statusCode, @NonNull Map<String,String> headers)
	{
		this(statusCode,null,headers);
	}

	public HttpException(int statusCode, String message, @NonNull Map<String,String> headers)
	{
		super(statusCode);
		this.message = message;
		this.headers = headers;
	}

	public static HttpException badRequest()
	{
		return new HttpException(HttpServletResponse.SC_BAD_REQUEST);
	}

	public static HttpException invalidHeader()
	{
		return new HttpException(HttpServletResponse.SC_BAD_REQUEST);
	}

	public static HttpException unauthorizedException()
	{
		return new HttpException(HttpServletResponse.SC_UNAUTHORIZED);
	}

	public static HttpException notFound(String resource)
	{
		return new HttpException(HttpServletResponse.SC_NOT_FOUND,resource + " not found");
	}

	public static HttpException methodNotAllowed()
	{
		return new HttpException(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
	}

	public static HttpException methodNotAllowed(String method)
	{
		return new HttpException(HttpServletResponse.SC_METHOD_NOT_ALLOWED,"Method " + method + " not allowed");
	}

	public static HttpException conflict()
	{
		return new HttpException(HttpServletResponse.SC_CONFLICT);
	}

	public static HttpException preconditionFailed(Map<String,String> headers)
	{
		return new HttpException(HttpServletResponse.SC_PRECONDITION_FAILED,headers);
	}

	public static HttpException requestEntityTooLarge()
	{
		return new HttpException(HttpServletResponse.SC_REQUEST_ENTITY_TOO_LARGE);
	}

	public static HttpException unsupportedMediaType()
	{
		return new HttpException(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE);
	}

	public static HttpException requestedRangeNotSatisfiable(Map<String,String> headers)
	{
		return new HttpException(HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE,headers);
	}

	public static HttpException internalServiceError()
	{
		return new HttpException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
	}

}
