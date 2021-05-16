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
package dev.luin.file.server.core.server.upload;

import dev.luin.file.server.core.ProcessorException;
import dev.luin.file.server.core.http.HttpException;
import dev.luin.file.server.core.server.upload.header.TusResumable;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UploadException extends ProcessorException
{
	private static final long serialVersionUID = 1L;
	HttpException httpException;

	public static UploadException methodNotAllowed(String method)
	{
		return new UploadException(HttpException.methodNotAllowed(method));
	}

	public UploadException(Throwable cause)
	{
		super(cause);
		this.httpException = HttpException.internalServiceError();
	}

	public UploadException(HttpException httpException)
	{
		this.httpException = httpException;
		httpException.getHeaders().put(TusResumable.get().asTuple());
	}

	public HttpException toHttpException()
	{
		return httpException;
	}
}
