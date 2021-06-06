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

import org.springframework.transaction.annotation.Transactional;

import com.querydsl.core.types.ConstructorExpression;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Projections;
import com.querydsl.sql.SQLQueryFactory;

import io.vavr.control.Option;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level=AccessLevel.PRIVATE, makeFinal=true)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@Transactional(transactionManager = "dataSourceTransactionManager")
class FSFileDAOImpl implements FSFileDAO
{
	@NonNull
	SQLQueryFactory queryFactory;
	QFile table = QFile.file;
	Expression<?>[] fsFileColumns = {table.virtualPath,table.path,table.name,table.contentType,table.md5Checksum,table.sha256Checksum,table.timestamp,table.startDate,table.endDate,table.userId,table.length,table.state};
	ConstructorExpression<FSFile> fsFileProjection = Projections.constructor(FSFile.class,fsFileColumns);

	@Override
	public boolean isAuthorized(@NonNull final VirtualPath path, final UserId userId)
	{
		return queryFactory.select(table.virtualPath.count())
				.from(table)
				.where(table.virtualPath.eq(path).and(table.userId.eq(userId)))
				.fetchOne() > 0;
	}

	@Override
	public Option<FSFile> findFile(@NonNull final VirtualPath path)
	{
		return Option.of(queryFactory.select(fsFileProjection)
				.from(table)
				.where(table.virtualPath.eq(path))
				.fetchOne());
	}

	@Override
	public List<VirtualPath> selectFiles()
	{
//		val entityPath = new EntityPathBase<FSFile>(FSFile.class,"virtualPath");
//		val sortProperty = Expressions.comparablePath(Comparable.class, entityPath, "value");
		return queryFactory.select(table.virtualPath)
				.from(table)
//FIXME				.orderBy(table.virtualPath.asc())
//				.orderBy(sortProperty.asc())
				.fetch();
	}

	@Override
	public FSFile insertFile(@NonNull final FSFile fsFile)
	{
		queryFactory.insert(table)
				.set(table.virtualPath,fsFile.getVirtualPath())
				.set(table.path,fsFile.getPath())
				.set(table.name,fsFile.getName())
				.set(table.contentType,fsFile.getContentType())
				.set(table.md5Checksum,fsFile.getMd5Checksum())
				.set(table.sha256Checksum,fsFile.getSha256Checksum())
				.set(table.timestamp,fsFile.getTimestamp())
				.set(table.startDate,fsFile.getValidTimeFrame().getStartDate().getOrNull())
				.set(table.endDate,fsFile.getValidTimeFrame().getEndDate().getOrNull())
				.set(table.userId,fsFile.getUserId())
				.set(table.length,fsFile.getLength())
				.set(table.state,fsFile.getState())
				.execute();
		return fsFile;
	}

	@Override
	public long updateFile(@NonNull FSFile fsFile)
	{
		return queryFactory.update(table)
				.set(table.md5Checksum,fsFile.getMd5Checksum())
				.set(table.sha256Checksum,fsFile.getSha256Checksum())
				.set(table.length,fsFile.getLength())
				.where(table.virtualPath.eq(fsFile.getVirtualPath()))
				.execute();
	}

	@Override
	public long deleteFile(@NonNull final VirtualPath path)
	{
		return queryFactory.delete(table)
				.where(table.virtualPath.eq(path))
				.execute();
	}
}
