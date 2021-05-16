package dev.luin.file.server.core.server.upload.http;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;

import dev.luin.file.server.core.file.FSFile;
import dev.luin.file.server.core.file.FileSystem;
import dev.luin.file.server.core.http.HttpException;
import dev.luin.file.server.core.server.upload.UploadException;
import dev.luin.file.server.core.server.upload.UploadMethod;
import dev.luin.file.server.core.server.upload.UploadRequest;
import dev.luin.file.server.core.server.upload.header.ContentLength;
import dev.luin.file.server.core.server.upload.header.ContentType;
import dev.luin.file.server.core.server.upload.header.TusResumable;
import dev.luin.file.server.core.server.upload.header.UploadLength;
import dev.luin.file.server.core.server.upload.header.UploadMetadata;
import dev.luin.file.server.core.server.upload.header.UploadOffset;
import dev.luin.file.server.core.server.upload.header.XHTTPMethodOverride;
import dev.luin.file.server.core.service.model.User;
import io.vavr.control.Option;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.val;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
public class UploadRequestImpl implements UploadRequest
{
	HttpServletRequest request;

	@Override
	public void validateTusResumable()
	{
		TusResumable.validate(request);
	}

	@Override
	public void validateContentType()
	{
		ContentType.validate(request);
	}

	@Override
	public void validateContentLength()
	{
		ContentLength.validate(request);
	}

	@Override
	public Option<ContentLength> getContentLength(FSFile file)
	{
		val contentLength = ContentLength.of(request);
		val uploadOffset = UploadOffset.of(request);
		validate(file,uploadOffset);
		validate(contentLength,file.getLength(),uploadOffset);
		return contentLength;
	}

	private void validate(FSFile file, UploadOffset uploadOffset)
	{
		if (file.getFileLength() != uploadOffset.getValue())
			throw HttpException.conflict();
	}

	private void validate(Option<ContentLength> contentLength, Long fileLength, UploadOffset uploadOffset)
	{
		if (contentLength.isDefined() && fileLength != null)
			if (uploadOffset.getValue() + contentLength.get().getValue() > fileLength)
				throw HttpException.badRequest();
	}

	@Override
	public Option<UploadLength> getUploadLength()
	{
		return UploadLength.get(request);
	}

	@Override
	public Option<UploadMetadata> getUploadMetadata()
	{
		return UploadMetadata.of(request);
	}

	@Override
	public String getPath()
	{
		return request.getPathInfo();
	}

	@Override
	public UploadMethod getMethod()
	{
		val method = XHTTPMethodOverride.of(request).map(h -> h.toString()).getOrElse(request.getMethod());
		return UploadMethod.of(method).getOrElseThrow(() -> UploadException.methodNotFound(method));
	}

	@Override
	public FSFile getFile(User user, FileSystem fs)
	{
		val path = request.getPathInfo();
		val file = fs.findFile(user,path).getOrElseThrow(() -> HttpException.notFound(path));
		val uploadLength = file.getLength() == null ? UploadLength.of(request) : Option.<UploadLength>none();
		return uploadLength.map(l -> file.withLength(l.getValue())).getOrElse(file);
	}

	@Override
	public InputStream getInputStream() throws IOException
	{
		return request.getInputStream();
	}
}
