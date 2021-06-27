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
package dev.luin.file.server.core.file;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import io.vavr.Function1;
import io.vavr.Function2;
import io.vavr.control.Option;

interface FSFileDAO
{
	Function2<VirtualPath,UserId,Boolean> virtualPathExists();
	Function1<VirtualPath,Option<FSFile>> findFile();
	Supplier<List<VirtualPath>> selectFiles();
	Function1<FSFile,FSFile> insertFile();
	Consumer<FSFile> updateFile();
	Consumer<VirtualPath> deleteFile();
}
