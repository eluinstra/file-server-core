/*
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

import dev.luin.file.server.core.file.FileSystem;
import dev.luin.file.server.core.service.user.AuthenticationManager;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.vavr.Function1;
import io.vavr.control.Try;
import java.time.Duration;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DownloadServerConfig
{
	@Autowired
	FileSystem fs;

	@Bean("DownloadHttpHandler")
	public DownloadHandler downloadHandler(
			@Autowired AuthenticationManager authenticationManager,
			@Value("${server.upload.maxMBsPerPeriod}") int maxMBsPerPeriod,
			@Value("${server.upload.periodInMillis}") long periodInMillis,
			@Value("${server.upload.maxTimeoutInMillis}") long maxTimeoutInMillis)
	{
		return DownloadHandler.builder()
				.authenticate(authenticationManager.authenticate)
				.getDownloadHandler(createDownloadHandler())
				.rateLimiter(
						RateLimiter.of(
								"downloadLimit",
								() -> RateLimiterConfig.custom()
										.limitForPeriod(maxMBsPerPeriod * 1024 * 1024)
										.limitRefreshPeriod(Duration.ofMillis(periodInMillis))
										.timeoutDuration(Duration.ofMillis(maxTimeoutInMillis))
										.build()))
				.build();
	}

	private Function1<DownloadRequest, Try<BaseHandler>> createDownloadHandler()
	{
		return BaseHandler.getDownloadHandlerBuilder().fileInfoHandler(new FileInfoHandler(fs)).downloadFileHandler(new DownloadFileHandler(fs)).build();
	}
}
