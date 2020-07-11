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
package org.bitbucket.eluinstra.fs.core.dao;

import org.bitbucket.eluinstra.fs.core.querydsl.model.QClient;
import org.bitbucket.eluinstra.fs.core.service.model.Client;

import com.querydsl.core.types.ConstructorExpression;
import com.querydsl.core.types.Projections;
import com.querydsl.sql.SQLQueryFactory;

import io.vavr.collection.List;
import io.vavr.collection.Seq;
import io.vavr.control.Option;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level=AccessLevel.PRIVATE, makeFinal=true)
@AllArgsConstructor
public class ClientDAOImpl implements ClientDAO
{
	@NonNull
	SQLQueryFactory queryFactory;
	QClient table = QClient.client;
	ConstructorExpression<Client> clientProjection = Projections.constructor(Client.class,table.id,table.name,table.certificate);

	@Override
	public Option<Client> findClient(final long id)
	{
		return Option.of(queryFactory.select(clientProjection)
				.from(table)
				.where(table.id.eq(id))
				.fetchOne());
	}				

	@Override
	public Option<Client> findClient(final String name)
	{
		return Option.of(queryFactory.select(clientProjection)
				.from(table)
				.where(table.name.eq(name))
				.fetchOne());
	}

	@Override
	public Seq<Client> selectClients()
	{
		return List.ofAll(queryFactory.select(clientProjection)
				.from(table)
				.fetch());
	}

	@Override
	public long insertClient(@NonNull final Client client)
	{
		return queryFactory.insert(table)
				.set(table.name,client.getName())
				.set(table.certificate,client.getCertificate())
				.executeWithKey(Long.class);
	}

	@Override
	public long updateClient(@NonNull final Client client)
	{
		return queryFactory.update(table)
				.set(table.name,client.getName())
				.set(table.certificate,client.getCertificate())
				.where(table.id.eq(client.getId()))
				.execute();
	}

	@Override
	public long deleteClient(final long id)
	{
		return queryFactory.delete(table)
				.where(table.id.eq(id))
				.execute();
	}
}
