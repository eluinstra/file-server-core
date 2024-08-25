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
package dev.luin.file.server.core.file;

import static dev.luin.file.server.core.ValueObject.inclusiveBetween;
import static dev.luin.file.server.core.ValueObject.matchesPattern;
import static dev.luin.file.server.core.ValueObject.toUpperCase;
import static io.vavr.control.Try.success;

import dev.luin.file.server.core.ValueObject;
import io.vavr.control.Try;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import lombok.NonNull;
import lombok.Value;

@Value
public class Md5Checksum implements ValueObject<String>
{
	@NonNull
	String value;

	public static MessageDigest messageDigest() throws NoSuchAlgorithmException
	{
		return MessageDigest.getInstance("MD5");
	}

	public Md5Checksum(@NonNull final byte[] checksum)
	{
		value = validateAndTransform(HexFormat.of().formatHex(checksum)).get();
	}

	public Md5Checksum(@NonNull final String checksum)
	{
		value = validateAndTransform(checksum).get();
	}

	private static Try<String> validateAndTransform(@NonNull String checksum)
	{
		return success(checksum).flatMap(inclusiveBetween(32L, 32L)).map(toUpperCase()).flatMap(matchesPattern("^[0-9A-F]*$"));
	}
}
