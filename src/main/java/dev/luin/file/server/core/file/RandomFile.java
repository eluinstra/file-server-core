package dev.luin.file.server.core.file;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.RandomStringUtils;

import io.vavr.control.Option;
import io.vavr.control.Try;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.Value;
import lombok.val;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
class RandomFile
{
	@NonNull
	Path path;
	@NonNull
	File file;

	static Try<RandomFile> create(@NonNull final String baseDir, final int filenameLength)
	{
		while (true)
		{
			val path = createRandomPath(baseDir, filenameLength);
			try
			{
				val file = createFile(path);
				if (file.isSingleValued())
					return Try.success(file.get());
			}
			catch (IOException e)
			{
				return Try.failure(new IOException("Error creating file " + path,e));
			}
		}
	}
	
	private static Path createRandomPath(final String baseDir, final int filenameLength)
	{
		val filename = RandomStringUtils.randomNumeric(filenameLength);
		return Paths.get(baseDir,filename);
	}
	
	private static Option<RandomFile> createFile(final Path path) throws IOException
	{
		val file = path.toFile();
		return file.createNewFile() ? Option.some(new RandomFile(path,file)) : Option.none();
	}

	Length getLength()
	{
		return new Length(file.length());
	}

	long write(@NonNull final InputStream input)
	{
		return Try.withResources(() -> new FileOutputStream(file))
				.of(o -> IOUtils.copyLarge(input,o))
				.getOrElseThrow(t -> new IllegalStateException("Error writing to file " + path,t));
	}
}
