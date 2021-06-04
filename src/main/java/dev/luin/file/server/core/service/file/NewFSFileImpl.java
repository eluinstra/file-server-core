package dev.luin.file.server.core.service.file;

import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;

import dev.luin.file.server.core.file.Filename;
import dev.luin.file.server.core.file.NewFSFile;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor(staticName = "of")
public class NewFSFileImpl implements NewFSFile
{
	NewFile file;

	@Override
	public Filename getName()
	{
		return new Filename(file.getContent().getName());
	}

	@Override
	public String getContentType()
	{
		return file.getContent().getContentType();
	}

	@Override
	public String getSha256Checksum()
	{
		return file.getSha256Checksum();
	}

	@Override
	public Instant getStartDate()
	{
		return file.getStartDate();
	}

	@Override
	public Instant getEndDate()
	{
		return file.getEndDate();
	}

	@Override
	public InputStream getInputStream() throws IOException
	{
		return file.getContent().getInputStream();
	}

}
