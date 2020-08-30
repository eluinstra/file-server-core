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
package dev.luin.file.server.core.http;

import io.vavr.collection.CharSeq;
import io.vavr.collection.List;
import io.vavr.collection.Seq;
import io.vavr.control.Option;
import lombok.NonNull;
import lombok.Value;
import lombok.val;

@Value
public class HeaderValues
{
	@NonNull
	Seq<HeaderValue> values;

	public static HeaderValues of(String value)
	{
		val values = getHeaderValues(value.trim());
		return new HeaderValues(values);
	}

	private static Seq<HeaderValue> getHeaderValues(String value)
	{
		val values = value != null ? CharSeq.of(value).split(",") : List.<CharSeq>empty();
		return values.flatMap(v -> createHeaderValue(v));
	}

	private static Option<HeaderValue> createHeaderValue(CharSeq value)
	{
		return HeaderValue.of(value.mkString());
	}

	public String getValue()
	{
		return values.headOption().map(v -> v.getValue()).getOrNull();
	}

	public Seq<String> getValues()
	{
		return values.map(v -> v.getValue());
	}

	@Override
	public String toString()
	{
		return values.mkString(", ");
	}

	public static void main(String[] args)
	{
		val v = HeaderValues.of("test;a=b;b=c;c,test1");
		System.out.println(v);
	}
}
