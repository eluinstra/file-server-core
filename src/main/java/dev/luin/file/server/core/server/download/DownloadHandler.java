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

import static io.vavr.control.Try.success;

import com.google.common.util.concurrent.RateLimiter;
import dev.luin.file.server.core.service.user.User;
import io.vavr.Function1;
import io.vavr.control.Try;
import java.security.cert.X509Certificate;
import java.util.function.Consumer;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DownloadHandler
{
	@NonNull
	Function1<X509Certificate, Try<User>> authenticate;
	@NonNull
	Function1<DownloadRequest, Try<BaseHandler>> getDownloadHandler;
	@NonNull
	@Getter
	RateLimiter rateLimiter;

	@Builder
	public DownloadHandler(
			@NonNull Function1<X509Certificate, Try<User>> authenticate,
			@NonNull Function1<DownloadRequest, Try<BaseHandler>> getDownloadHandler,
			@NonNull RateLimiter rateLimiter)
	{
		this.authenticate = authenticate;
		this.getDownloadHandler = getDownloadHandler;
		this.rateLimiter = rateLimiter;
	}

	public Try<Function1<DownloadResponse, Try<Void>>> handle(@NonNull final DownloadRequest request)
	{
		return authenticate.apply(request.getClientCertificate())
				.toTry(DownloadException::unauthorizedException)
				.peek(logger("User {}"))
				.flatMap(handleRequest(request));
	}

	private static Consumer<Object> logger(String msg)
	{
		return o -> log.info(msg, o);
	}

	private Function1<User, Try<Function1<DownloadResponse, Try<Void>>>> handleRequest(DownloadRequest request)
	{
		return user -> success(request).flatMap(getDownloadHandler).flatMap(h -> h.handle(request, user));
	}
}
