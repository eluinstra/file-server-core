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

import static io.vavr.control.Try.failure;
import static io.vavr.control.Try.success;

import io.vavr.control.Option;
import io.vavr.control.Try;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Supplier;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.Value;
import lombok.val;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.RandomStringUtils;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
class RandomFile
{
	@NonNull
	Path path;
	@NonNull
	File file;

	static Try<RandomFile> create(@NonNull Supplier<Path> randomPathSupplier)
	{
		while (true)
		{
			val path = randomPathSupplier.get();
			try
			{
				val file = createFile(path);
				if (file.isSingleValued())
					return success(file.get());
			}
			catch (IOException e)
			{
				return failure(new IOException("Error creating file " + path, e));
			}
		}
	}

	static Supplier<Path> createRandomPathSupplier(final String baseDir, final int filenameLength)
	{
		return () ->
		{
			val filename = RandomStringUtils.randomNumeric(filenameLength);
			return Paths.get(baseDir, filename);
		};
	}

	private static Option<RandomFile> createFile(final Path path) throws IOException
	{
		val file = path.toFile();
		return file.createNewFile() ? Option.some(new RandomFile(path, file)) : Option.none();
	}

	Length getLength()
	{
		return new Length(file.length());
	}

	Try<Long> write(@NonNull final InputStream input)
	{
		return Try.withResources(() -> new FileOutputStream(file)).of(o -> IOUtils.copyLarge(input, o));
	}
}
