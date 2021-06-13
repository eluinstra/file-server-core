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
package dev.luin.file.server.core.service.user;

import dev.luin.file.server.core.file.FSUser;
import dev.luin.file.server.core.file.UserId;
import dev.luin.file.server.core.server.servlet.Certificate;
import io.vavr.control.Try;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.ToString;
import lombok.Value;
import lombok.With;

@Value
@AllArgsConstructor
public class User implements FSUser
{
	@With
	UserId id;
	@NonNull
	Username name;
	@NonNull
	@ToString.Exclude
	Certificate certificate;

	public User(Username name, Certificate certificate)
	{
		this.id = null;
		this.name = name;
		this.certificate = certificate;
	}

	byte[] getEncodedCertificate()
	{
		return Try.of(() -> certificate.getEncoded()).getOrElseThrow(t -> new IllegalStateException(t));
	}
}
