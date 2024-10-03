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
package dev.luin.file.server.core.server.servlet.throttling;

import com.google.common.util.concurrent.RateLimiter;
import java.io.IOException;
import java.io.Reader;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
public class ThrottlingReader extends Reader
{
	RateLimiter maxBytesPerSecond;
	Reader target;

	@Override
	public int read(char[] cbuf, int off, int len) throws IOException
	{
		maxBytesPerSecond.acquire(len);
		return target.read(cbuf, off, len);
	}

	@Override
	public void close() throws IOException
	{
		target.close();
	}
}
