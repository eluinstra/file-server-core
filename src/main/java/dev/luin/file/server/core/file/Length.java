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
package dev.luin.file.server.core.file;

import java.math.BigInteger;

import org.apache.commons.lang3.Validate;

import dev.luin.file.server.core.ValueObject;
import dev.luin.file.server.core.server.download.header.Range;
import io.vavr.control.Try;
import lombok.NonNull;
import lombok.Value;

@Value
public class Length implements ValueObject<Long>
{
	@NonNull
	Long value;

	public Length(final int fileLength)
	{
		this((long)fileLength);
	}

	public Length(@NonNull final Long fileLength)
	{
		value = Try.success(fileLength)
				.andThenTry(v -> Validate.isTrue(v.compareTo(0L) >= 0))
				.get();
	}

	public boolean containsFirstPosition(@NonNull final Range range)
	{
		return range.getFirst(this) < value;
	}

	public BigInteger toBigInteger()
	{
		return BigInteger.valueOf(value);
	}

	public String getStringValue()
	{
		return value.toString();
	}
}
