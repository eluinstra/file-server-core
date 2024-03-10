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
package dev.luin.file.server.core.service.user;

import dev.luin.file.server.core.file.UserId;
import io.vavr.collection.Seq;
import io.vavr.control.Option;
import java.security.cert.X509Certificate;
import lombok.NonNull;

interface UserDAO
{
	Option<User> findUser(@NonNull UserId id);

	Option<User> findUser(@NonNull X509Certificate certificate);

	Seq<User> selectUsers();

	User insertUser(@NonNull User user);

	long updateUser(@NonNull User user);

	long deleteUser(@NonNull UserId id);
}
