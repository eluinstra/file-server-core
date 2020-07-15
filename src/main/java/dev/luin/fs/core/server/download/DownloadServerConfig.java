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
package dev.luin.fs.core.server.download;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import dev.luin.fs.core.file.FileSystem;
import dev.luin.fs.core.user.UserManager;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@Configuration
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DownloadServerConfig
{
	@Autowired
	UserManager userManager;
	@Autowired
	FileSystem fs;

	@Bean
	public HttpHandler httpHandler()
	{
		return HttpHandler.builder()
				.userManager(userManager)
				.headHandler(new HeadHandler(fs))
				.getHandler(new GetHandler(fs))
				.build();
	}
}
