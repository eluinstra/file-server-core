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

import static com.querydsl.core.types.PathMetadataFactory.forVariable;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.dsl.SimplePath;
import com.querydsl.sql.ColumnMetadata;
import dev.luin.file.server.core.file.Timestamp;
import dev.luin.file.server.core.file.UserId;
import dev.luin.file.server.core.server.servlet.Certificate;
import jakarta.annotation.Generated;
import java.sql.Types;

/**
 * QUser is a Querydsl query type for QUser
 */
@Generated("com.querydsl.sql.codegen.MetaDataSerializer")
public class QCertificate extends com.querydsl.sql.RelationalPathBase<QCertificate>
{

	private static final long serialVersionUID = -1;

	public static final QCertificate certificateTable = new QCertificate("certificate");

	public final SimplePath<Certificate> certificate = createSimple("certificate", Certificate.class);

	public final SimplePath<UserId> id = createSimple("id", UserId.class);

	public final SimplePath<Timestamp> timestamp = createSimple("timestamp", Timestamp.class);

	public final com.querydsl.sql.PrimaryKey<QCertificate> sysPk20092 = createPrimaryKey(id);

	public final com.querydsl.sql.ForeignKey<QUser> _sysFk20112 = createForeignKey(id, "id");

	public QCertificate(String variable)
	{
		super(QCertificate.class, forVariable(variable), "PUBLIC", "certificate");
		addMetadata();
	}

	public QCertificate(String variable, String schema, String table)
	{
		super(QCertificate.class, forVariable(variable), schema, table);
		addMetadata();
	}

	public QCertificate(String variable, String schema)
	{
		super(QCertificate.class, forVariable(variable), schema, "certificate");
		addMetadata();
	}

	public QCertificate(Path<? extends QCertificate> path)
	{
		super(path.getType(), path.getMetadata(), "PUBLIC", "certificate");
		addMetadata();
	}

	public QCertificate(PathMetadata metadata)
	{
		super(QCertificate.class, metadata, "PUBLIC", "certificate");
		addMetadata();
	}

	public void addMetadata()
	{
		addMetadata(certificate, ColumnMetadata.named("certificate").withIndex(2).ofType(Types.BLOB).withSize(1073741824).notNull());
		addMetadata(id, ColumnMetadata.named("id").withIndex(1).ofType(Types.BIGINT).withSize(32).notNull());
		addMetadata(timestamp, ColumnMetadata.named("time_stamp").withIndex(3).ofType(Types.TIMESTAMP).withSize(26).notNull());
	}

}
