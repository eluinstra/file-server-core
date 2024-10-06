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

import io.github.resilience4j.ratelimiter.RateLimiter;
import java.io.IOException;
import java.io.InputStream;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
public class ThrottlingInputStream extends InputStream
{
	RateLimiter maxBytesPerSecond;
	InputStream target;

	@Override
	public int read() throws IOException
	{
		maxBytesPerSecond.acquirePermission();
		return target.read();
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException
	{
		maxBytesPerSecond.acquirePermission(len);
		return target.read(b, off, len);
	}

	@Override
	public long skip(long n) throws IOException
	{
		return target.skip(n);
	}

	@Override
	public int available() throws IOException
	{
		return target.available();
	}

	@Override
	public synchronized void mark(int readlimit)
	{
		target.mark(readlimit);
	}

	@Override
	public synchronized void reset() throws IOException
	{
		target.reset();
	}

	@Override
	public boolean markSupported()
	{
		return target.markSupported();
	}

	@Override
	public void close() throws IOException
	{
		target.close();
	}
}
