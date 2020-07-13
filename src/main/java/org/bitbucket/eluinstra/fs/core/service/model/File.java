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
package org.bitbucket.eluinstra.fs.core.service.model;

import java.time.Instant;

import javax.activation.DataHandler;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.bitbucket.eluinstra.fs.core.InstantAdapter;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;

@XmlRootElement(name="file")
@XmlAccessorType(XmlAccessType.FIELD)
@Builder
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class File
{
	@XmlElement(required=true)
	String name;
	@NonNull
	@XmlElement(required=true)
	String contentType;
	@XmlElement
	String sha256Checksum;
	@XmlElement
	@XmlJavaTypeAdapter(InstantAdapter.class)
	@XmlSchemaType(name="dateTime")
	Instant startDate;
	@XmlElement
	@XmlJavaTypeAdapter(InstantAdapter.class)
	@XmlSchemaType(name="dateTime")
	Instant endDate;
	@NonNull
	@XmlElement(required=true)
	DataHandler content;
}
