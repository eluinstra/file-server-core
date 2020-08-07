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
package dev.luin.fs.core.file;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.querydsl.core.types.ConstructorExpression;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Projections;
import com.querydsl.sql.SQLQueryFactory;

import dev.luin.fs.core.querydsl.model.QFile;
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
	Expression<?>[] fsFileColumns = {table.virtualPath,table.realPath,table.filename,table.contentType,table.md5Checksum,table.sha256Checksum,table.startDate,table.endDate,table.userId,table.fileLength,table.fileType};
	ConstructorExpression<FSFile> fsFileProjection = Projections.constructor(FSFile.class,fsFileColumns);

	@Override
	public boolean isAuthorized(@NonNull final String path, final long userId)
	{
		return queryFactory.select(table.virtualPath.count())
				.from(table)
				.where(table.virtualPath.eq(path).and(table.userId.eq(userId)))
				.fetchOne() > 0;
	}

	@Override
	public Option<FSFile> findFile(@NonNull final String path)
	{
		return Option.of(queryFactory.select(fsFileProjection)
				.from(table)
				.where(table.virtualPath.eq(path))
				.fetchOne());
	}

	@Override
	public List<String> selectFiles()
	{
		return queryFactory.select(table.virtualPath)
				.from(table)
				.fetch();
	}

	@Override
	public String insertFile(@NonNull final FSFile fsFile)
	{
		queryFactory.insert(table)
				.set(table.virtualPath,fsFile.getVirtualPath())
				.set(table.realPath,fsFile.getRealPath())
				.set(table.filename,fsFile.getName())
				.set(table.contentType,fsFile.getContentType())
				.set(table.md5Checksum,fsFile.getMd5Checksum())
				.set(table.sha256Checksum,fsFile.getSha256Checksum())
				.set(table.startDate,fsFile.getStartDate())
				.set(table.endDate,fsFile.getEndDate())
				.set(table.userId,fsFile.getUserId())
				.set(table.fileLength,fsFile.getFileLength())
				.set(table.fileType,fsFile.getFileType())
				.execute();
		return fsFile.getVirtualPath();
	}

	@Override
	public long updateFile(@NonNull FSFile fsFile)
	{
		return queryFactory.update(table)
				.set(table.md5Checksum,fsFile.getMd5Checksum())
				.set(table.sha256Checksum,fsFile.getSha256Checksum())
				.set(table.fileLength,fsFile.getFileLength())
				.where(table.virtualPath.eq(fsFile.getVirtualPath()))
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
