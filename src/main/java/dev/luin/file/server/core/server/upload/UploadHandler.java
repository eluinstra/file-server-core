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

import java.security.cert.X509Certificate;
import java.util.function.Consumer;

import dev.luin.file.server.core.service.user.User;
import dev.luin.file.server.core.service.user.UserManagerException;
import io.vavr.Function1;
import io.vavr.Function2;
import io.vavr.control.Either;
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
	Function1<X509Certificate,Either<UserManagerException,User>> authenticate;
	@NonNull
	Function2<UploadRequest,User,Either<UploadException,Consumer<UploadResponse>>> handle;

	@Builder(access = AccessLevel.PACKAGE)
	public UploadHandler(@NonNull Function1<X509Certificate,Either<UserManagerException,User>> authenticate, @NonNull Function1<UploadRequest,Either<UploadException,BaseHandler>> getUploadHandler)
	{
		this.authenticate = authenticate;
		handle = (request,user) -> Either.<UploadException,UploadRequest>right(request)
				.flatMap(getUploadHandler)
				.flatMap(handler -> handler.handle(request,user));
	}

	public Either<UploadException,Consumer<UploadResponse>> handle(@NonNull final UploadRequest request)
	{
		return authenticate.apply(request.getClientCertificate())
				.mapLeft(e -> UploadException.unauthorizedException())
				.peek(logger.apply("User {}"))
				.flatMap(handle.apply(request));
	}
}
