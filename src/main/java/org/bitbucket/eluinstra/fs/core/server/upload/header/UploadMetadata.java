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
package org.bitbucket.eluinstra.fs.core.server.upload.header;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.binary.Base64;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.collection.CharSeq;
import io.vavr.collection.HashMap;
import io.vavr.collection.Map;
import io.vavr.control.Option;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.val;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UploadMetadata extends TusHeader
{
	private static final String HEADER_NAME = "Upload-Metadata";

	public static Option<UploadMetadata> of(HttpServletRequest request)
	{
		val header = request.getHeader(HEADER_NAME);
		return header != null ? Option.of(new UploadMetadata(header)) : Option.none();
	}

	@NonNull
	Map<String,String> metadata;

	private UploadMetadata(@NonNull String header)
	{
		super(HEADER_NAME);
		metadata = CharSeq.of(header).split(",")
				.flatMap(p -> toTuple2(p," "))
				.foldLeft(HashMap.empty(),(m,t) -> m.put(t));
	}

	private static Option<Tuple2<String,String>> toTuple2(CharSeq s, String splitRegEx)
	{
		val parts = s.split(splitRegEx,2);
		return parts.headOption()
				.map(k -> Tuple.of(
						k.trim().mkString(),
						parts.tail().headOption().map(v -> new String(Base64.decodeBase64(v.trim().mkString()))).getOrNull()));
	}

	public String getParameter(String name)
	{
		return metadata.get(name).getOrNull();
	}

	@Override
	public String toString()
	{
		return metadata.map(t -> toString(t)).mkString(",");
	}

	private String toString(Tuple2<String,String> t)
	{
		return t._1 + (t._2 != null ? " " + Base64.encodeBase64(t._2.getBytes()) : "");
	}
}
