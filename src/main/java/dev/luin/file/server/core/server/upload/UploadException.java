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
package dev.luin.file.server.core.server.upload;

import dev.luin.file.server.core.ProcessingException;
import dev.luin.file.server.core.file.VirtualPath;
import dev.luin.file.server.core.http.HttpException;
import dev.luin.file.server.core.http.UsingHttpException;
import dev.luin.file.server.core.server.upload.header.TusResumable;
import dev.luin.file.server.core.server.upload.header.TusVersion;
import io.vavr.collection.HashMap;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UploadException extends ProcessingException implements UsingHttpException
{
	private static final long serialVersionUID = 1L;
	@NonNull
	HttpException httpException;

	public static UploadException fileNotFound(final String path)
	{
		return new UploadException(HttpException.notFound(path));
	}

	public static UploadException fileNotFound(final VirtualPath path)
	{
		return new UploadException(HttpException.notFound(path.getValue()));
	}

	public static UploadException fileTooLarge()
	{
		return new UploadException(HttpException.requestEntityTooLarge());
	}

	public static UploadException invalidContentLength()
	{
		return new UploadException(HttpException.badRequest());
	}

	public static UploadException invalidContentType()
	{
		return new UploadException(HttpException.unsupportedMediaType());
	}

	public static UploadException invalidTusVersion()
	{
		return new UploadException(HttpException.preconditionFailed(HashMap.of(TusVersion.HEADER_NAME,TusVersion.VALUE)));
	}

	public static UploadException invalidUploadOffset()
	{
		return new UploadException(HttpException.conflict());
	}

	public static UploadException methodNotAllowed()
	{
		return methodNotAllowed();
	}

	public static UploadException methodNotAllowed(final UploadMethod method)
	{
		return methodNotAllowed(method.getHttpMethod());
	}

	private  static UploadException methodNotAllowed(final String method)
	{
		return new UploadException(HttpException.methodNotAllowed(method));
	}

	public static UploadException methodNotFound()
	{
		return methodNotAllowed();
	}

	public static UploadException methodNotFound(final String method)
	{
		return methodNotAllowed(method);
	}

	public static UploadException missingContentType()
	{
		return new UploadException(HttpException.unsupportedMediaType());
	}

	public static UploadException missingUploadLength()
	{
		return new UploadException(HttpException.invalidHeader());
	}

	public static UploadException missingUploadOffset()
	{
		return new UploadException(HttpException.invalidHeader());
	}

	public static UploadException unauthorizedException()
	{
		return new UploadException(HttpException.unauthorizedException());
	}

	public static UploadException illegalStateException()
	{
		return new UploadException(HttpException.internalServiceError());
	}

	public static UploadException illegalStateException(Throwable t)
	{
		return new UploadException(t,HttpException.internalServiceError());
	}

	public UploadException(Throwable cause)
	{
		super(cause);
		this.httpException = HttpException.internalServiceError();
	}

	public UploadException(HttpException httpException)
	{
		this.httpException = httpException;
		httpException.getHeaders().put(TusResumable.HEADER_NAME,TusResumable.VALUE);
	}

	public UploadException(Throwable cause, HttpException httpException)
	{
		super(cause);
		this.httpException = httpException;
	}

	public HttpException toHttpException()
	{
		return httpException;
	}

}
