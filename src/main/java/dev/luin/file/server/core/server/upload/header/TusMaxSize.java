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

import static org.apache.commons.lang3.Validate.isTrue;

import dev.luin.file.server.core.ValueObject;
import dev.luin.file.server.core.server.upload.UploadResponse;
import io.vavr.control.Try;
import lombok.NonNull;
import lombok.Value;

@Value
public class TusMaxSize implements ValueObject<Long>
{
	private static String HEADER_NAME = "Tus-Max-Size";
	Long value;

	public static TusMaxSize of(Long maxSize)
	{
		return maxSize == null || maxSize == 0 ? null : new TusMaxSize(maxSize);
	}
	
	private TusMaxSize(@NonNull Long maxSize)
	{
		value = Try.success(maxSize)
				.andThen(v -> isTrue(v > 0))
				.get();
	}

	public void write(UploadResponse response)
	{
		response.setHeader(HEADER_NAME,value.toString());
	}
}
