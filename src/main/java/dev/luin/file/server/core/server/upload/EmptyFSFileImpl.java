package dev.luin.file.server.core.server.upload;

import dev.luin.file.server.core.file.ContentType;
import dev.luin.file.server.core.file.EmptyFSFile;
import dev.luin.file.server.core.file.Length;
import dev.luin.file.server.core.server.upload.header.TusMaxSize;
import dev.luin.file.server.core.server.upload.header.UploadLength;
import dev.luin.file.server.core.server.upload.header.UploadMetadata;
import io.vavr.control.Option;
import dev.luin.file.server.core.file.Filename;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.val;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor(staticName = "of")
public class EmptyFSFileImpl implements EmptyFSFile
{
	@NonNull
	UploadRequest uploadRequest;
	TusMaxSize tusMaxSize;

	@Override
	public Filename getName()
	{
		val uploadMetadata = UploadMetadata.of(uploadRequest);
		return uploadMetadata.getFilename();
	}

	@Override
	public ContentType getContentType()
	{
		val uploadMetadata = UploadMetadata.of(uploadRequest);
		return uploadMetadata.getContentType();
	}

	@Override
	public Option<Length> getLength()
	{
		return UploadLength.of(uploadRequest,tusMaxSize).map(v -> v.toFileLength());
	}

}
