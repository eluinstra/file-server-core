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

import java.io.IOException;
import java.io.InputStream;

import dev.luin.file.server.core.file.FSFile;
import dev.luin.file.server.core.file.FileSystem;
import dev.luin.file.server.core.server.upload.header.ContentLength;
import dev.luin.file.server.core.server.upload.header.UploadLength;
import dev.luin.file.server.core.server.upload.header.UploadMetadata;
import dev.luin.file.server.core.service.model.User;
import io.vavr.control.Option;

public interface UploadRequest
{
	void validateTusResumable();
	void validateContentType();
	void validateContentLength();
	Option<ContentLength> getContentLength(FSFile file);
	Option<UploadLength> getUploadLength();
	Option<UploadMetadata> getUploadMetadata();
	String getPath();
	UploadMethod getMethod();
	FSFile getFile(User user, FileSystem fs);
	InputStream getInputStream() throws IOException;
}
