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
package dev.luin.file.server.core;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ProcessorException extends RuntimeException
{
	private static final long serialVersionUID = 1L;

	public ProcessorException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
	{
		super(message,cause,enableSuppression,writableStackTrace);
	}

	public ProcessorException(String message, Throwable cause)
	{
		super(message,cause);
	}

	public ProcessorException(String message)
	{
		super(message);
	}

	public ProcessorException(Throwable cause)
	{
		super(cause);
	}

}
