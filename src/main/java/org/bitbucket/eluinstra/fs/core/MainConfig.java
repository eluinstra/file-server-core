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
package org.bitbucket.eluinstra.fs.core;

import org.bitbucket.eluinstra.fs.core.dao.DAOConfig;
import org.bitbucket.eluinstra.fs.core.datasource.DataSourceConfig;
import org.bitbucket.eluinstra.fs.core.file.FileSystemConfig;
import org.bitbucket.eluinstra.fs.core.querydsl.QueryDSLConfig;
import org.bitbucket.eluinstra.fs.core.server.download.DownloadServerConfig;
import org.bitbucket.eluinstra.fs.core.service.ServiceConfig;
import org.bitbucket.eluinstra.fs.core.transaction.TransactionManagerConfig;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

@Configuration
@Import({
	DAOConfig.class,
	DataSourceConfig.class,
	DownloadServerConfig.class,
	FileSystemConfig.class,
	QueryDSLConfig.class,
	ServiceConfig.class,
	TransactionManagerConfig.class
})
@PropertySource(value = {"classpath:org/bitbucket/eluinstra/fs/core/default.properties"}, ignoreResourceNotFound = true)
public class MainConfig
{
	public static void main(String[] args)
	{
		try(AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MainConfig.class))
		{
			
		}
	}
}
