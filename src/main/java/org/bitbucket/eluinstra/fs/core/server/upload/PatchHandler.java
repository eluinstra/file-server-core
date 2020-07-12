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
package org.bitbucket.eluinstra.fs.core.server.upload;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bitbucket.eluinstra.fs.core.file.FSFile;
import org.bitbucket.eluinstra.fs.core.file.FileSystem;
import org.bitbucket.eluinstra.fs.core.http.HttpException;
import org.bitbucket.eluinstra.fs.core.server.upload.header.ContentLength;
import org.bitbucket.eluinstra.fs.core.server.upload.header.ContentType;
import org.bitbucket.eluinstra.fs.core.server.upload.header.TusMaxSize;
import org.bitbucket.eluinstra.fs.core.server.upload.header.TusResumable;
import org.bitbucket.eluinstra.fs.core.server.upload.header.UploadDeferLength;
import org.bitbucket.eluinstra.fs.core.server.upload.header.UploadLength;
import org.bitbucket.eluinstra.fs.core.server.upload.header.UploadOffset;
import org.bitbucket.eluinstra.fs.core.service.model.Client;

import io.vavr.control.Option;
import io.vavr.control.Try;
import lombok.val;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PatchHandler extends BaseHandler
{
	public PatchHandler(FileSystem fs)
	{
		super(fs);
	}

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response, Client client) throws IOException
	{
		TusResumable.of(request);
		ContentType.of(request);
		val contentLength = ContentLength.of(request);
		val uploadLength = UploadLength.of(request);
		if (!uploadLength.isDefined())
			UploadDeferLength.of(request).getOrElseThrow(() -> HttpException.invalidHeaderException(UploadLength.HEADER_NAME));
		uploadLength.filter(v -> v.getValue() <= TusMaxSize.getMaxSize()).getOrElseThrow(() -> HttpException.requestEntityTooLargeException());
		val uploadOffset = UploadOffset.of(request);
		val path = request.getPathInfo();
		val file = getFs().findFile(client.getCertificate(),path).getOrElseThrow(() -> HttpException.notFound());
		validate(file,uploadOffset);
		validate(contentLength,uploadLength,uploadOffset);
		getFs().write(file,request.getInputStream());
		uploadLength.filter(l -> l.getValue() == file.getFileLength() ).forEach(l -> Try.of(() -> getFs().finishPartialFile(file)).onFailure(e -> log.error("",e)));
		response.setStatus(HttpServletResponse.SC_NO_CONTENT);
		UploadOffset.of(file.getFileLength()).write(response);
		TusResumable.get().write(response);
	}

	private void validate(FSFile file, UploadOffset uploadOffset)
	{
		if (file.getFileLength() != uploadOffset.getValue())
			throw HttpException.conflictException();
	}

	private void validate(Option<ContentLength> contentLength, Option<UploadLength> uploadLength, UploadOffset uploadOffset)
	{
		if (contentLength.isDefined() && uploadLength.isDefined())
			if (uploadOffset.getValue() + contentLength.get().getValue() > uploadLength.get().getValue())
				throw HttpException.badRequestException();
	}
}
