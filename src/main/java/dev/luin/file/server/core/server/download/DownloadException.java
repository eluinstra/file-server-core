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
package dev.luin.file.server.core.server.download;

import dev.luin.file.server.core.ProcessingException;
import dev.luin.file.server.core.file.Length;
import dev.luin.file.server.core.file.VirtualPath;
import dev.luin.file.server.core.http.HttpException;
import dev.luin.file.server.core.http.UsingHttpException;
import dev.luin.file.server.core.server.download.header.Range;
import io.vavr.collection.HashMap;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DownloadException extends ProcessingException implements UsingHttpException
{
	private static final long serialVersionUID = 1L;
	@NonNull
	HttpException httpException;

	public static DownloadException invalidIfRange()
	{
		return new DownloadException(HttpException.badRequest());
	}

	public static DownloadException methodNotFound()
	{
		return methodNotAllowed();
	}

	private static DownloadException methodNotAllowed()
	{
		return new DownloadException(HttpException.methodNotAllowed());
	}

	public static DownloadException methodNotFound(String method)
	{
		return methodNotAllowed(method);
	}

	private static DownloadException methodNotAllowed(String method)
	{
		return new DownloadException(HttpException.methodNotAllowed(method));
	}

	public static DownloadException methodNotAllowed(DownloadMethod method)
	{
		return methodNotAllowed(method == null ? "<empty>" : method.getHttpMethod());
	}

	public static DownloadException requestedRangeNotSatisfiable(Length length)
	{
		return new DownloadException(HttpException.requestedRangeNotSatisfiable(HashMap.of(Range.createHeader(length))));
	}

	public static DownloadException fileNotFound(String path)
	{
		return new DownloadException(HttpException.notFound(path));
	}

	public static DownloadException fileNotFound(VirtualPath path)
	{
		return new DownloadException(HttpException.notFound(path.getValue()));
	}

	public static DownloadException unauthorizedException()
	{
		return new DownloadException(HttpException.unauthorizedException());
	}

	public DownloadException(Throwable cause)
	{
		super(cause);
		httpException = HttpException.internalServiceError();
	}

	public DownloadException(HttpException httpException)
	{
		this.httpException = httpException;
	}

	public HttpException toHttpException()
	{
		return httpException;
	}

}
