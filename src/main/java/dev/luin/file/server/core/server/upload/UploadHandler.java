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
package dev.luin.file.server.core.server.upload;

import static io.vavr.control.Try.success;

import java.security.cert.X509Certificate;
import java.util.function.Consumer;

import dev.luin.file.server.core.service.user.User;
import io.vavr.Function1;
import io.vavr.Function2;
import io.vavr.control.Try;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@FieldDefaults(level=AccessLevel.PRIVATE, makeFinal=true)
public class UploadHandler
{
	private static final Function1<String,Consumer<Object>> logger = msg -> o -> log.info(msg,o);

	@NonNull
	Function1<X509Certificate,Try<User>> authenticate;
	@NonNull
	Function2<UploadRequest,User,Try<Consumer<UploadResponse>>> handle;

	@Builder(access = AccessLevel.PACKAGE)
	public UploadHandler(@NonNull Function1<X509Certificate,Try<User>> authenticate, @NonNull Function1<UploadRequest,Try<BaseHandler>> getUploadHandler)
	{
		this.authenticate = authenticate;
		handle = (request,user) -> success(request)
				.flatMap(getUploadHandler)
				.flatMap(handler -> handler.handle(request,user));
	}

	public Try<Consumer<UploadResponse>> handle(@NonNull final UploadRequest request)
	{
		return authenticate.apply(request.getClientCertificate())
				.toTry(() -> UploadException.unauthorizedException())
				.peek(logger.apply("User {}"))
				.flatMap(handle.apply(request));
	}
}
