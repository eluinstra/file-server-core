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
package dev.luin.file.server.core.server.upload.header;

import dev.luin.file.server.core.http.LongHeaderValue;
import io.vavr.control.Option;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TusMaxSize extends TusHeader
{
	private static Long maxSize;
	private static Option<TusMaxSize> DEFAULT = getDefault();

	public static Option<TusMaxSize> get()
	{
		return DEFAULT;
	}

	public static void setMaxSize(Long maxSize)
	{
		TusMaxSize.maxSize = maxSize;
		DEFAULT = getDefault();
	}

	public static Long getMaxSize()
	{
		return maxSize;
	}

	private static Option<TusMaxSize> getDefault()
	{
		return maxSize != null ? LongHeaderValue.of(maxSize,0,Long.MAX_VALUE).map(v -> new TusMaxSize(v)) : Option.none();
	}

	@NonNull
	LongHeaderValue value;

	private TusMaxSize(@NonNull LongHeaderValue value)
	{
		super("Tus-Max-Size");
		this.value = value;
	}

	@Override
	public String toString()
	{
		return value.toString();
	}
}
