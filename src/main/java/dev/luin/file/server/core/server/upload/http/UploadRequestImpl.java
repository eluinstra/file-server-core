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
package dev.luin.file.server.core.server.upload.http;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;

import dev.luin.file.server.core.file.FSFile;
import dev.luin.file.server.core.file.FileSystem;
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
import dev.luin.file.server.core.service.user.User;
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
		ContentLength.zeroValueValidation(request);
	}

	@Override
	public Option<Long> getContentLength(FSFile file)
	{
		val contentLength = ContentLength.get(request);
		val uploadOffset = UploadOffset.get(request);
		validate(file,uploadOffset);
		validate(contentLength,file.getLength(),uploadOffset);
		return contentLength;
	}

	private void validate(FSFile file, Long uploadOffset)
	{
		if (file.getFileLength() != uploadOffset)
			throw UploadException.invalidUploadOffset();
	}

	private void validate(Option<Long> contentLength, Long fileLength, Long uploadOffset)
	{
		contentLength.filter(c -> fileLength != null)
				.filter(c -> uploadOffset + c <= fileLength)
				.getOrElseThrow(() -> UploadException.invalidContentLength());
	}

	@Override
	public Option<Long> getUploadLength()
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
		val method = XHTTPMethodOverride.get(request).map(h -> h.toString()).getOrElse(request.getMethod());
		return UploadMethod.of(method).getOrElseThrow(() -> UploadException.methodNotFound(method));
	}

	@Override
	public FSFile getFile(User user, FileSystem fs)
	{
		val path = request.getPathInfo();
		val file = fs.findFile(user,path).getOrElseThrow(() -> UploadException.fileNotFound(path));
		val uploadLength = file.getLength() == null ? UploadLength.get(request) : Option.<Long>none();
		return uploadLength.map(l -> file.withLength(l)).getOrElse(file);
	}

	@Override
	public InputStream getInputStream() throws IOException
	{
		return request.getInputStream();
	}
}
