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
package dev.luin.file.server.core.server.download;

import javax.servlet.http.HttpServletRequest;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.collection.CharSeq;
import io.vavr.collection.HashMap;
import io.vavr.collection.Map;
import io.vavr.control.Option;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.val;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
class ContentType
{
	public static final String HEADER_NAME = "Content-Type";
	@Getter
	String baseType;
	@Getter
	String primaryType;
	@Getter
	String subType;
	Map<String,String> metadata;

	public static Option<ContentType> of(HttpServletRequest request)
	{
		String header = request.getHeader(HEADER_NAME);
		return header != null ? Option.of(new ContentType(CharSeq.of(header))) : Option.none();
	}

	private ContentType(@NonNull CharSeq charSeq)
	{
		val contentType = charSeq.split(";",2);
		this.baseType = contentType.headOption().getOrNull().mkString();
		val baseType = contentType.headOption().map(s -> s.split("/",2)).get();
		this.primaryType = baseType.headOption().map(b -> b.mkString()).getOrNull();
		this.subType = baseType.tail().headOption().map(b -> b.mkString()).getOrNull();
		val parameters = contentType.tail().headOption();
		this.metadata = parameters.getOrElse(CharSeq.empty())
				.split(";")
				.flatMap(p -> toTuple2(p,"="))
				.foldLeft(HashMap.empty(),(m,t) -> m.put(t));
	}

	private static Option<Tuple2<String,String>> toTuple2(CharSeq s, String splitRegEx)
	{
		val parts = s.split(splitRegEx,2);
		return parts.headOption()
				.map(k -> Tuple.of(
						k.trim().mkString(),
						parts.tail().headOption().map(v -> v.trim().mkString()).getOrNull()));
	}

	public String getParameter(String name)
	{
		return metadata.get(name).getOrNull();
	}
}
