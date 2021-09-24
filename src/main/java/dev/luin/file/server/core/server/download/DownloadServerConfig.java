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
package dev.luin.file.server.core.server.download;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import dev.luin.file.server.core.file.FileSystem;
import dev.luin.file.server.core.service.user.AuthenticationManager;
import io.vavr.Function1;
import io.vavr.control.Try;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@Configuration
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DownloadServerConfig
{
	@Autowired
	FileSystem fs;

	@Bean("DownloadHttpHandler")
	public DownloadHandler downloadHandler(@Autowired AuthenticationManager authenticationManager)
	{
		return DownloadHandler.builder()
				.authenticate(authenticationManager.authenticate)
				.getDownloadHandler(createDownloadHandler())
				.build();
	}

	private Function1<DownloadRequest,Try<BaseHandler>> createDownloadHandler()
	{
		return BaseHandler.getDownloadHandlerBuilder()
				.fileInfoHandler(new FileInfoHandler(fs))
				.downloadFileHandler(new DownloadFileHandler(fs))
				.build();
	}
}
