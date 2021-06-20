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

import dev.luin.file.server.core.ValueObject;
import io.vavr.Function1;
import lombok.NonNull;
import lombok.Value;

@Value
public class Username implements ValueObject<String>
{
	private static final Function1<String,String> checkLength = inclusiveBetween.apply(5L,32L);
	private static final Function1<String,String> checkPattern = matchesPattern.apply("^[0-9a-zA-Z\\\\.-_]*$");
	private static final Function1<String,String> validate = checkLength.andThen(checkPattern);
	@NonNull
	String value;

	public Username(@NonNull String username)
	{
		value = validate.apply(username);
	}
}
