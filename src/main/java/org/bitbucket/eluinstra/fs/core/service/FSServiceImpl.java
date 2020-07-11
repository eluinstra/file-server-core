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
package org.bitbucket.eluinstra.fs.core.service;

import java.io.IOException;
import java.util.function.Supplier;

import org.bitbucket.eluinstra.fs.core.dao.ClientDAO;
import org.bitbucket.eluinstra.fs.core.file.FileSystem;
import org.bitbucket.eluinstra.fs.core.service.model.File;
import org.bitbucket.eluinstra.fs.core.service.model.FileInfo;
import org.bitbucket.eluinstra.fs.core.service.model.FileMapper;
import org.springframework.transaction.annotation.Transactional;

import io.vavr.control.Either;
import io.vavr.control.Try;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.val;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level=AccessLevel.PRIVATE, makeFinal=true)
@AllArgsConstructor
@Transactional(transactionManager = "dataSourceTransactionManager")
public class FSServiceImpl implements FSService
{
	@NonNull
	ClientDAO clientDAO;
	@NonNull
	FileSystem fs;

	@Override
	public String uploadFile(@NonNull final File file, final long clientId) throws FSServiceException
	{
		val client = clientDAO.findClient(clientId);
		return client.map(c -> Try.of(() -> createFile(file,client)))
			.get()
			.getOrElseThrow(() -> new FSServiceException("ClientId " + clientId + " not found!"));
	}

	private String createFile(final File file, final io.vavr.control.Option<org.bitbucket.eluinstra.fs.core.service.model.Client> client) throws IOException
	{
		return fs.createFile(file.getFilename(),file.getContentType(),file.getChecksum(),file.getStartDate(),file.getEndDate(),client.get().getId(),file.getContent().getInputStream())
			.getVirtualPath();
	}

	@Override
	public FileInfo getFileInfo(String path) throws FSServiceException
	{
		val fsFile = fs.findFile(path);
		return fsFile.map(f -> FileMapper.INSTANCE.toFileInfo(f)).getOrNull();
	}

	@Override
	public void deleteFile(@NonNull final String path, final Boolean force) throws FSServiceException
	{
		val fsFile = fs.findFile(path);
		val isDeleted = fsFile.map(f -> toEither(fs.deleteFile(fsFile.get(),force != null && force),() -> "Unable to delete " + path + "!"))
				.getOrElseThrow(() -> new FSServiceException(path + " not found!"));
		isDeleted.getOrElseThrow(s -> new FSServiceException(s));
	}

	private Either<String,Void> toEither(boolean success, Supplier<String> errorMessage)
	{
		return success ? Either.right(null) : Either.left(errorMessage.get());
	}
}
