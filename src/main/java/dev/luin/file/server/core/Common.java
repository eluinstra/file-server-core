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
package dev.luin.file.server.core;

import java.io.IOException;
import java.util.function.Consumer;

import org.slf4j.Logger;

import io.vavr.Function1;
import io.vavr.Function2;

public class Common
{
	public static final Function1<Object,Void> toNull = o -> null;

	public static final Function2<Logger,String,Consumer<Object>> logObject = (log,message) -> o -> log.info(message,o);

	public static final Function1<Throwable,IOException> toIOException = t -> t instanceof IOException ? (IOException)t : new IOException(t);
}