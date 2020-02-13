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

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.transaction.support.TransactionTemplate;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FSFileDAOImpl implements FSFileDAO
{
	@NonNull
	protected TransactionTemplate transactionTemplate;
	@NonNull
	protected JdbcTemplate jdbcTemplate;

	private final RowMapper<FSFile> fsFileRowMapper = (RowMapper<FSFile>)(rs,rowNum) ->
	{
		Period period = new Period(rs.getTimestamp("startDate"),rs.getTimestamp("endDate"));
		return new FSFile(
				rs.getString("virtual_path"),
				rs.getString("real_path"),
				rs.getString("content_type"),
				rs.getString("checksum"),
				period,
				rs.getLong("clientId"));
	};

	@Override
	public boolean isAuthorized(@NonNull byte[] certificate, @NonNull String path)
	{
		return jdbcTemplate.queryForObject(
				"select count(*) from fs_client c, fs_file f where f.virtual_path = ? and f.client_id = c.id and c.certificate = ?",
				Integer.class,
				path,
				certificate) > 0;
//		byte[] result = jdbcTemplate.queryForObject(
//				"select certificate from fs_client c, fs_file f where f.virtual_path = ? and f.client_id = c.id",
//				byte[].class,
//				path
//			);
//		return certificate.equals(result) ;
	}

	@Override
	public Optional<FSFile> findFileByVirtualPath(@NonNull String path)
	{
		try
		{
			return Optional.of(jdbcTemplate.queryForObject(
					"select *" +
					" from fs_file" +
					" where virtual_path = ?",
					fsFileRowMapper,
					path));
		}
		catch(EmptyResultDataAccessException e)
		{
			return Optional.empty();
		}
	}

	@Override
	public int insertFile(@NonNull FSFile fsFile)
	{
		return jdbcTemplate.update(
			"insert into fs_file (" +
				"virtual_path," +
				"real_path," +
				"content_type," +
				"checksum," +
				"start_date," +
				"end_date," +
				"client_id" +
			") values (?,?,?,?,?,?)",
			fsFile.getVirtualPath(),
			fsFile.getRealPath(),
			fsFile.getContentType(),
			fsFile.getChecksum(),
			fsFile.getPeriod().getStartDate(),
			fsFile.getPeriod().getEndDate(),
			fsFile.getClientId());
	}

	@Override
	public int deleteFile(@NonNull String path)
	{
		return jdbcTemplate.update(
			"delete from fs_file" +
			" where virtual_path = ?",
			path);
	}

}
