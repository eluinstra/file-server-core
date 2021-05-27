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
		return uploadMetadata.map(m -> m.getFilename()).getOrNull();
	}

	@Override
	public String getContentType()
	{
		val uploadMetadata = uploadRequest.getUploadMetadata();
		return uploadMetadata.map(m -> m.getContentType()).getOrNull();
	}

	@Override
	public Long getLength()
	{
		return uploadRequest.getUploadLength().getOrNull();
	}

}
