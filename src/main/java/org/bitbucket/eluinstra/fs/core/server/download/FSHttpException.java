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
package org.bitbucket.eluinstra.fs.core.server.download;

import javax.servlet.http.HttpServletResponse;

import org.bitbucket.eluinstra.fs.core.http.HttpException;

import io.vavr.collection.Map;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Getter
public class FSHttpException
{
	public static HttpException requestedRangeNotSatisfiable(Map<String,String> headers)
	{
		return new HttpException(HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE,headers);
	}
}
