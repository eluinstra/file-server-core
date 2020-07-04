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
package org.bitbucket.eluinstra.fs.core.server.upload;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UploadMetadata
{
	public static final String headerName = "Upload-Metadata";
	Map<String,String> metadata;

	public static UploadMetadata of(String header)
	{
		return new UploadMetadata(header);
	}

	private UploadMetadata(String header)
	{
		metadata = Arrays.stream(StringUtils.split(header,","))
				.map(p -> StringUtils.split(p," "))
				.collect(Collectors.toMap(s -> s[0],s -> new String(Base64.decodeBase64(s[1]))));
	}

	public String getParameter(String name)
	{
		return metadata.get(name);
	}

}
