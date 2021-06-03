package dev.luin.file.server.core.server.upload;

import dev.luin.file.server.core.file.EmptyFSFile;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.val;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor(staticName = "of")
public class EmptyFSFileImpl implements EmptyFSFile
{
	UploadRequest uploadRequest;

	@Override
	public String getName()
	{
		val uploadMetadata = uploadRequest.getUploadMetadata();
		return uploadMetadata.getFilename();
	}

	@Override
	public String getContentType()
	{
		val uploadMetadata = uploadRequest.getUploadMetadata();
		return uploadMetadata.getContentType();
	}

	@Override
	public Long getLength()
	{
		return uploadRequest.getUploadLength().getOrNull();
	}

}
