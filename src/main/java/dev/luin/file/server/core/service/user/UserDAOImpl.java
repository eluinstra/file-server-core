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

import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;

import org.springframework.transaction.annotation.Transactional;

import com.querydsl.core.NonUniqueResultException;
import com.querydsl.core.types.ConstructorExpression;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.sql.SQLQueryFactory;

import dev.luin.file.server.core.file.UserId;
import io.vavr.collection.List;
import io.vavr.collection.Seq;
import io.vavr.control.Option;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.val;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level=AccessLevel.PRIVATE, makeFinal=true)
@AllArgsConstructor
@Transactional(transactionManager = "dataSourceTransactionManager")
class UserDAOImpl implements UserDAO
{
	@NonNull
	SQLQueryFactory queryFactory;
	QUser table = QUser.user;
	ConstructorExpression<User> userProjection = Projections.constructor(User.class,table.id,table.name,table.certificate);

	@Override
	public Option<User> findUser(@NonNull final UserId id)
	{
		return Option.of(queryFactory.select(userProjection)
				.from(table)
				.where(table.id.eq(id))
				.fetchOne());
	}				

	@Override
	public Option<User> findUser(@NonNull final X509Certificate certificate)
	{
		try
		{
			val c = Expressions.path(byte[].class,"certificate");
			return Option.of(queryFactory.select(userProjection)
					.from(table)
					.where(c.eq(certificate.getEncoded()))
					.fetchOne());
		}
		catch (NonUniqueResultException | CertificateEncodingException e)
		{
			throw new IllegalStateException(e);
		}
	}

	@Override
	public Seq<User> selectUsers()
	{
		val username = Expressions.comparablePath(String.class,"name");
		return List.ofAll(queryFactory.select(userProjection)
				.from(table)
				.orderBy(username.asc())
				.fetch());
	}

	@Override
	public User insertUser(@NonNull final User user)
	{
		val id = queryFactory.insert(table)
				.set(table.name,user.getName())
				.set(table.certificate,user.getCertificate())
				.executeWithKey(Long.class);
		return user.withId(new UserId(id));
	}

	@Override
	public long updateUser(@NonNull final User user)
	{
		return queryFactory.update(table)
				.set(table.name,user.getName())
				.set(table.certificate,user.getCertificate())
				.where(table.id.eq(user.getId()))
				.execute();
	}

	@Override
	public long deleteUser(@NonNull final UserId id)
	{
		return queryFactory.delete(table)
				.where(table.id.eq(id))
				.execute();
	}
}
