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
package org.bitbucket.eluinstra.fs.dao;

import java.util.List;
import java.util.Optional;

import org.bitbucket.eluinstra.fs.model.FSFile;
import org.bitbucket.eluinstra.fs.model.Period;
import org.bitbucket.eluinstra.fs.service.model.Client;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.transaction.support.TransactionTemplate;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FSDAOImpl implements FSDAO
{
	@NonNull
	protected TransactionTemplate transactionTemplate;
	@NonNull
	protected JdbcTemplate jdbcTemplate;

	private final RowMapper<Client> clientRowMapper = (RowMapper<Client>)(rs,rowNum) ->
	{
		return new Client(rs.getLong("id"),rs.getString("name"),rs.getBytes("certificate"));
	};

	private final RowMapper<FSFile> fsFileRowMapper = (RowMapper<FSFile>)(rs,rowNum) ->
	{
		Period period = new Period(rs.getTimestamp("startDate"),rs.getTimestamp("endDate"));
		return new FSFile(rs.getString("virtual_path"),rs.getString("real_path"),rs.getString("content_type"),period,rs.getLong("clientId"));
	};

	@Override
	public Optional<Client> findClient(long id)
	{
		try
		{
			return Optional.of(jdbcTemplate.queryForObject(
					"select *" +
					" from fs_client" +
					" where id = ?",
					clientRowMapper,
					id
				));
		}
		catch(EmptyResultDataAccessException e)
		{
			return Optional.empty();
		}
	}

	@Override
	public Optional<Client> findClient(@NonNull String name)
	{
		try
		{
			return Optional.of(jdbcTemplate.queryForObject(
					"select *" +
					" from fs_client" +
					" where name = ?",
					clientRowMapper,
					name
				));
		}
		catch(EmptyResultDataAccessException e)
		{
			return Optional.empty();
		}
	}

	@Override
	public List<Client> selectClients()
	{
		return jdbcTemplate.query(
				"select *" +
				" from fs_client",
				clientRowMapper
			);
	}

	@Override
	public int insertClient(@NonNull Client client)
	{
		return jdbcTemplate.update
		(
			"insert into fs_client (" +
				"name," +
				"certificate" +
			") values (?,?)",
			client.getName(),
			client.getCertificate()
		);
	}

	@Override
	public int updateClient(@NonNull Client client)
	{
		return jdbcTemplate.update
		(
			"update fs_client set" +
			" name = ?," +
			" certificate = ?" +
			" where id = ?",
			client.getName(),
			client.getCertificate(),
			client.getId()
		);
	}

	@Override
	public int deleteClient(long id)
	{
		return jdbcTemplate.update
		(
			"delete from fs_client" +
			" where id = ?",
			id
		);
	}

	@Override
	public int deleteClient(@NonNull String name)
	{
		return jdbcTemplate.update
		(
			"delete from fs_client" +
			" where name = ?",
			name
		);
	}

	@Override
	public boolean isAuthorized(@NonNull byte[] certificate, @NonNull String path)
	{
		return jdbcTemplate.queryForObject(
				"select count(*) from fs_client c, fs_file f where f.virtual_path = ? and f.client_id = c.id and c.certificate = ?",
				Integer.class,
				path,
				certificate
		) > 0;
//		byte[] result = jdbcTemplate.queryForObject(
//				"select certificate from fs_client c, fs_file f where f.virtual_path = ? and f.client_id = c.id",
//				byte[].class,
//				path
//			);
//		return certificate.equals(result) ;
	}

	@Override
	public Optional<FSFile> findFile(@NonNull String path)
	{
		try
		{
			return Optional.of(jdbcTemplate.queryForObject(
					"select *" +
					" from fs_file" +
					" where virtual_path = ?",
					fsFileRowMapper,
					path
				));
		}
		catch(EmptyResultDataAccessException e)
		{
			return Optional.empty();
		}
	}

	@Override
	public int insertFile(@NonNull FSFile fsFile)
	{
		return jdbcTemplate.update
		(
			"insert into fs_file (" +
				"virtual_path," +
				"real_path," +
				"content_type," +
				"start_date," +
				"end_date," +
				"client_id" +
			") values (?,?,?,?,?,?)",
			fsFile.getVirtualPath(),
			fsFile.getRealPath(),
			fsFile.getContentType(),
			fsFile.getPeriod().getStartDate(),
			fsFile.getPeriod().getEndDate(),
			fsFile.getClientId()
		);
	}

	@Override
	public int deleteFile(@NonNull String path)
	{
		return jdbcTemplate.update
		(
			"delete from fs_file" +
			" where virtual_path = ?",
			path
		);
	}

}
