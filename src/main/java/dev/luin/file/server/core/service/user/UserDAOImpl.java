/*
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
package dev.luin.file.server.core.service.user;

import com.querydsl.core.NonUniqueResultException;
import com.querydsl.core.types.ConstructorExpression;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.sql.SQLQueryFactory;
import dev.luin.file.server.core.file.Timestamp;
import dev.luin.file.server.core.file.UserId;
import dev.luin.file.server.core.server.servlet.Certificate;
import io.vavr.collection.List;
import io.vavr.collection.Seq;
import io.vavr.control.Option;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import lombok.val;
import org.springframework.transaction.annotation.Transactional;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
@Transactional(transactionManager = "dataSourceTransactionManager")
class UserDAOImpl implements UserDAO
{
	@NonNull
	SQLQueryFactory queryFactory;
	QUser userTable = QUser.user;
	QCertificate certificateTable = QCertificate.certificateTable;
	ConstructorExpression<User> userProjection = Projections.constructor(User.class, userTable.id, userTable.name);
	ConstructorExpression<Certificate> certificateProjection = Projections.constructor(Certificate.class, certificateTable.certificate);

	@Override
	public Option<User> findUser(@NonNull final UserId id)
	{
		return Option.of(queryFactory.select(userProjection).from(userTable).where(userTable.id.eq(id)).fetchOne());
	}

	@Override
	public Option<User> findUser(@NonNull final Certificate certificate)
	{
		try
		{
			return Option.of(
					queryFactory.select(userProjection)
							.from(userTable)
							.innerJoin(certificateTable)
							.on(userTable.id.eq(certificateTable.id))
							.where(certificateTable.certificate.eq(certificate))
							.fetchOne());
		}
		catch (NonUniqueResultException e)
		{
			throw new IllegalStateException(e);
		}
	}

	@Override
	public Seq<User> selectUsers()
	{
		val username = Expressions.comparablePath(String.class, "name");
		return List.ofAll(queryFactory.select(userProjection).from(userTable).orderBy(username.asc()).fetch());
	}

	@Override
	public User insertUser(@NonNull final User user)
	{
		val id = queryFactory.insert(userTable).set(userTable.name, user.getName()).executeWithKey(Long.class);
		return user.withId(new UserId(id));
	}

	@Override
	public long updateUser(@NonNull final User user)
	{
		return queryFactory.update(userTable).set(userTable.name, user.getName()).where(userTable.id.eq(user.getId())).execute();
	}

	@Override
	public long deleteUser(@NonNull final UserId id)
	{
		return queryFactory.delete(userTable).where(userTable.id.eq(id)).execute();
	}

	@Override
	public Seq<Certificate> selectCertificates(@NonNull UserId id)
	{
		return List.ofAll(queryFactory.select(certificateProjection).from(certificateTable).where(certificateTable.id.eq(id)).fetch());
	}

	@Override
	public long insertCertificate(@NonNull UserId id, @NonNull Certificate certificate)
	{
		return queryFactory.insert(certificateTable)
				.set(certificateTable.id, id)
				.set(certificateTable.certificate, certificate)
				.set(certificateTable.timestamp, new Timestamp(certificate.getValue().getNotAfter()))
				.execute();
	}

	@Override
	public long deleteCertificate(@NonNull Certificate certificate)
	{
		return queryFactory.delete(certificateTable).where(certificateTable.certificate.eq(certificate)).execute();
	}
}
