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

import javax.xml.ws.http.HTTPException;

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

	public FSHttpException(int statusCode)
	{
		this(statusCode,null,Collections.emptyMap());
	}

	public FSHttpException(int statusCode, String message)
	{
		this(statusCode,message,Collections.emptyMap());
	}

	public FSHttpException(int statusCode, Map<String,String> headers)
	{
		this(statusCode,null,headers);
	}

	public FSHttpException(int statusCode, String message, Map<String,String> headers)
	{
		super(statusCode);
		this.message = message;
		this.headers = headers;
	}
}
