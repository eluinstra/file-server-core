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

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.bitbucket.eluinstra.fs.core.service.model.Client;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.transaction.support.TransactionTemplate;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RequiredArgsConstructor
@FieldDefaults(level=AccessLevel.PRIVATE, makeFinal=true)
public class ClientDAOImpl implements ClientDAO
{
	@NonNull
	TransactionTemplate transactionTemplate;
	@NonNull
	JdbcTemplate jdbcTemplate;

	RowMapper<Client> clientRowMapper = (RowMapper<Client>)(rs,rowNum) ->
	{
		return new Client(rs.getLong("id"),rs.getString("name"),rs.getBytes("certificate"));
	};

	@Override
	public Optional<Client> findClient(final long id)
	{
		try
		{
			return Optional.of(jdbcTemplate.queryForObject(
					"select *" +
					" from fs_client" +
					" where id = ?",
					clientRowMapper,
					id));
		}
		catch(EmptyResultDataAccessException e)
		{
			return Optional.empty();
		}
	}

	@Override
	public int insertClient(@NonNull final Client client)
	{
		return jdbcTemplate.update(
			"insert into fs_client (" +
				"name," +
				"certificate" +
			") values (?,?)",
			client.getName(),
			client.getCertificate());
	}

	@Override
	public int updateClient(@NonNull final Client client)
	{
		return jdbcTemplate.update(
			"update fs_client set" +
			" name = ?," +
			" certificate = ?" +
			" where id = ?",
			client.getName(),
			client.getCertificate(),
			client.getId());
	}

	@Override
	public int deleteClient(final long id)
	{
		return jdbcTemplate.update(
			"delete from fs_client" +
			" where id = ?",
			id);
	}

	@Override
	public List<Client> selectClients()
	{
		return Collections.unmodifiableList(jdbcTemplate.query(
				"select *" +
				" from fs_client",
				clientRowMapper));
	}

}
