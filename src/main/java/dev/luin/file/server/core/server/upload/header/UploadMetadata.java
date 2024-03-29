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
package dev.luin.file.server.core.server.upload.header;

import org.apache.commons.codec.binary.Base64;

import dev.luin.file.server.core.file.ContentType;
import dev.luin.file.server.core.file.Filename;
import dev.luin.file.server.core.server.upload.UploadRequest;
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
public class UploadMetadata
{
	private static final String HEADER_NAME = "Upload-Metadata";

	public static UploadMetadata of(UploadRequest request)
	{
		return new UploadMetadata(request.getHeader(HEADER_NAME));
	}

	@NonNull
	Map<String,String> metadata;

	public UploadMetadata(final String header)
	{
		metadata = header == null ? HashMap.empty() : toHashMap(header);
	}

	private HashMap<String,String> toHashMap(final String header)
	{
		return CharSeq.of(header).split(",")
				.flatMap(part -> toTuple2(part," "))
				.foldLeft(HashMap.empty(),(map,tuple) -> map.put(tuple));
	}

	private Option<Tuple2<String,String>> toTuple2(final CharSeq s, final String splitRegEx)
	{
		val parts = s.split(splitRegEx,2);
		return parts.headOption()
				.map(key -> Tuple.of(
						key.trim().mkString(),
						parts.tail().headOption()
							.map(CharSeq::trim)
							.map(CharSeq::mkString)
							.map(Base64::decodeBase64)
							.map(String::new)
							.getOrNull()));
	}

	public ContentType getContentType()
	{
		return getParameter("Content-Type").map(ContentType::new).getOrElse(ContentType.DEFAULT);
	}

	public Filename getFilename()
	{
		return getParameter("filename").map(Filename::new).getOrNull();
	}

	Option<String> getParameter(final String name)
	{
		return metadata.get(name);
	}

	@Override
	public String toString()
	{
		return metadata.map(this::toString).mkString(",");
	}

	private String toString(final Tuple2<String,String> t)
	{
		return t._1 + (t._2 != null ? " " + Base64.encodeBase64(t._2.getBytes()) : "");
	}
}
