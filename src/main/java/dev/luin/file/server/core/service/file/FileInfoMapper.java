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
package dev.luin.file.server.core.service.file;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import dev.luin.file.server.core.file.FSFile;
import dev.luin.file.server.core.file.Md5Checksum;
import dev.luin.file.server.core.file.Sha256Checksum;

@Mapper
public interface FileInfoMapper
{
	public FileInfoMapper INSTANCE = Mappers.getMapper(FileInfoMapper.class);

	@Mapping(source = "virtualPath", target = "path")
	FileInfo toFileInfo(FSFile file);

	default String map(Sha256Checksum value)
	{
		return value.getValue();
	}

	default String map(Md5Checksum value)
	{
		return value.getValue();
	}
}
