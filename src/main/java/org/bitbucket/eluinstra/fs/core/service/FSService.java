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
package org.bitbucket.eluinstra.fs.core.service;

import java.util.List;

import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.ws.soap.MTOM;

import org.bitbucket.eluinstra.fs.core.service.model.File;
import org.bitbucket.eluinstra.fs.core.service.model.FileInfo;

@MTOM(enabled=true)
@WebService(targetNamespace="http://bitbucket.org/eluinstra/fs/core/1.0")
public interface FSService
{
	@WebResult(name="path")
	String uploadFile(@WebParam(name="file") @XmlElement(required=true) File file, @WebParam(name="clientId") @XmlElement(required=true) long clientId) throws FSServiceException;
	@WebResult(name="file")
	File downloadFile(@WebParam(name="path") @XmlElement(required=true) String path) throws FSServiceException;
	@WebResult(name="path")
	List<String> getFiles() throws FSServiceException;
	@WebResult(name="fileInfo")
	FileInfo getFileInfo(@WebParam(name="path") @XmlElement(required=true) String path) throws FSServiceException;
	void deleteFile(@WebParam(name="path") @XmlElement(required=true) String path, @WebParam(name="force") Boolean force) throws FSServiceException;
}
