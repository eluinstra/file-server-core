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
package org.bitbucket.eluinstra.fs.core.server.upload;

import javax.servlet.http.HttpServletResponse;

import org.bitbucket.eluinstra.fs.core.http.HttpException;
import org.bitbucket.eluinstra.fs.core.server.upload.header.TusHeader;

import io.vavr.collection.HashMap;
import io.vavr.collection.Seq;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Getter
public class FSHttpException
{
	public static HttpException preconditionFailedException(Seq<TusHeader> headers)
	{
		return new HttpException(HttpServletResponse.SC_PRECONDITION_FAILED,headers.foldLeft(HashMap.empty(),(m,h) -> m.put(h.getName(),h.toString())));
	}
}
