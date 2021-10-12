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

import java.nio.file.Paths;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import dev.luin.file.server.core.file.FileSystem;
import dev.luin.file.server.core.service.user.UserManager;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@Configuration
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FileServiceConfig
{
	@Value("${attachment.outputDirectory}")
	String attachmentOutputDirectory;
	@Value("${attachment.memoryTreshold}")
	int attachmentMemoryTreshold;
	@Value("${attachment.cipherTransformation}")
	String attachmentCipherTransformation;
	@Value("${share.location:shared}")
	String shareLocation;

	@Bean
	public FileService fileService(@Autowired UserManager userManager, @Autowired FileSystem fs)
	{
		return new FileServiceImpl(userManager,fs, Paths.get(shareLocation).toAbsolutePath());
	}

	@Bean
	public void ebMSAttachmentFactory()
	{
		AttachmentFactory.init(attachmentOutputDirectory,attachmentMemoryTreshold,attachmentCipherTransformation);
	}

}
