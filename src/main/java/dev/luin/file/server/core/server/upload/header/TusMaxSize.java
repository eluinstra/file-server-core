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

import dev.luin.file.server.core.ValueObject;
import dev.luin.file.server.core.server.upload.UploadResponse;
import io.vavr.Function1;
import lombok.NonNull;
import lombok.Value;

@Value
public class TusMaxSize implements ValueObject<Long>
{
	private static String HEADER_NAME = "Tus-Max-Size";
	private static final Function1<Long,Long> isGreaterThenZero = isGreaterThen.apply(0L);
	@NonNull
	Long value;

	public static TusMaxSize of(final Long maxSize)
	{
		return maxSize == null || maxSize == 0 ? null : new TusMaxSize(maxSize);
	}
	
	private TusMaxSize(final Long maxSize)
	{
		value = isGreaterThenZero.apply(maxSize);
	}

	public void write(@NonNull final UploadResponse response)
	{
		response.setHeader(HEADER_NAME,value.toString());
	}
}
