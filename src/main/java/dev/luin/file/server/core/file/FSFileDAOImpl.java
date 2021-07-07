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
import com.querydsl.core.types.Path;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.ComparablePath;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.SimplePath;
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
	QFile table = QFile.file;
	Expression<?>[] fsFileColumns = {table.virtualPath,table.path,table.name,table.contentType,table.md5Checksum,table.sha256Checksum,table.timestamp,table.startDate,table.endDate,table.userId,table.length,table.state};
	ConstructorExpression<FSFile> fsFileProjection = Projections.constructor(FSFile.class,fsFileColumns);
	Path<FSFile> fsFile = Expressions.path(FSFile.class,"file");
	ComparablePath<String> virtualPath = Expressions.comparablePath(String.class,Expressions.path(VirtualPath.class,fsFile,"virtualPath"),"value");
	SimplePath<Long> userId = Expressions.path(Long.class,Expressions.path(UserId.class,fsFile,"user_id"),"value");
	@NonNull
	SQLQueryFactory queryFactory;

	@Override
	public boolean isAuthorized(@NonNull final VirtualPath path, @NonNull final UserId userId)
	{
		return queryFactory.select(table.virtualPath.count())
				.from(table)
				.where(virtualPath.eq(path.getValue()).and(this.userId.eq(userId.getValue())))
				.fetchOne() > 0;
	}

	@Override
	public Option<FSFile> findFile(@NonNull final VirtualPath path)
	{
		return Option.of(queryFactory.select(fsFileProjection)
				.from(table)
				.where(virtualPath.eq(path.getValue()))
				.fetchOne());
	}

	@Override
	public List<VirtualPath> selectFiles()
	{
		return queryFactory.select(table.virtualPath)
				.from(table)
				.orderBy(virtualPath.asc())
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
				.set(table.startDate,fsFile.getValidTimeFrame().getStartDate())
				.set(table.endDate,fsFile.getValidTimeFrame().getEndDate())
				.set(table.userId,fsFile.getUserId())
				.set(table.length,fsFile.getLength())
				.set(table.state,fsFile.getState())
				.execute();
		return fsFile;
	}

	@Override
	public long updateFile(@NonNull final FSFile fsFile)
	{
		return queryFactory.update(table)
				.set(table.md5Checksum,fsFile.getMd5Checksum())
				.set(table.sha256Checksum,fsFile.getSha256Checksum())
				.set(table.length,fsFile.getLength())
				.where(virtualPath.eq(fsFile.getVirtualPath().getValue()))
				.execute();
	}

	@Override
	public long deleteFile(@NonNull final VirtualPath path)
	{
		return queryFactory.delete(table)
				.where(virtualPath.eq(path.getValue()))
				.execute();
	}
}
