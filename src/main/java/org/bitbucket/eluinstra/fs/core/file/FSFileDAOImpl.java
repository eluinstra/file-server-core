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
package org.bitbucket.eluinstra.fs.core.file;

import java.util.Optional;

import org.bitbucket.eluinstra.fs.core.querydsl.model.QClient;
import org.bitbucket.eluinstra.fs.core.querydsl.model.QFile;

import com.querydsl.core.types.ConstructorExpression;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Projections;
import com.querydsl.sql.SQLQueryFactory;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level=AccessLevel.PRIVATE, makeFinal=true)
@AllArgsConstructor
public class FSFileDAOImpl implements FSFileDAO
{
	@NonNull
	SQLQueryFactory queryFactory;
	QFile table = QFile.file;
	Expression<?>[] fsFileColumns = {table.virtualPath,table.realPath,table.filename,table.contentType,table.md5Checksum,table.sha256Checksum,table.startDate,table.endDate,table.clientId};
	ConstructorExpression<FSFile> fsFileProjection = Projections.constructor(FSFile.class,fsFileColumns);
	QClient clientTable = QClient.client;

	@Override
	public boolean isAuthorized(@NonNull final byte[] certificate, @NonNull final String path)
	{
		return queryFactory.select(table.virtualPath.count())
				.from(table,clientTable)
				.where(table.virtualPath.eq(path).and(table.clientId.eq(clientTable.id)).and(clientTable.certificate.eq(certificate)))
				.fetchOne() > 0;
//		val result = queryFactory.select(clientTable.certificate)
//				.from(table,clientTable)
//				.where(table.virtualPath.eq(path).and(table.clientId.eq(clientTable.id)))
//				.fetchOne();
//		return certificate.equals(result) ;
	}

	@Override
	public Optional<FSFile> findFileByVirtualPath(@NonNull final String path)
	{
		return Optional.ofNullable(queryFactory.select(fsFileProjection)
				.from(table)
				.where(table.virtualPath.eq(path))
				.fetchOne());
	}

	@Override
	public long insertFile(@NonNull final FSFile fsFile)
	{
		return queryFactory.insert(table)
				.set(table.virtualPath,fsFile.getVirtualPath())
				.set(table.realPath,fsFile.getRealPath())
				.set(table.filename,fsFile.getFilename())
				.set(table.contentType,fsFile.getContentType())
				.set(table.md5Checksum,fsFile.getMd5checksum())
				.set(table.sha256Checksum,fsFile.getSha256checksum())
				.set(table.startDate,fsFile.getPeriod() != null ? fsFile.getPeriod().getStartDate() : null)
				.set(table.endDate,fsFile.getPeriod() != null ? fsFile.getPeriod().getEndDate() : null)
				.set(table.clientId,fsFile.getClientId())
				.execute();
	}

	@Override
	public long deleteFile(@NonNull final String path)
	{
		return queryFactory.delete(table)
				.where(table.virtualPath.eq(path))
				.execute();
	}
}
