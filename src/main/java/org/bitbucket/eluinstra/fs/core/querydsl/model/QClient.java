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
package org.bitbucket.eluinstra.fs.core.querydsl.model;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;

import com.querydsl.sql.ColumnMetadata;
import java.sql.Types;




/**
 * QClient is a Querydsl query type for QClient
 */
@Generated("com.querydsl.sql.codegen.MetaDataSerializer")
public class QClient extends com.querydsl.sql.RelationalPathBase<QClient> {

    private static final long serialVersionUID = 968095064;

    public static final QClient client = new QClient("client");

    public final SimplePath<byte[]> certificate = createSimple("certificate", byte[].class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath name = createString("name");

    public final com.querydsl.sql.PrimaryKey<QClient> sysPk10092 = createPrimaryKey(id);

    public final com.querydsl.sql.ForeignKey<QFile> _sysFk10112 = createInvForeignKey(id, "client_id");

    public QClient(String variable) {
        super(QClient.class, forVariable(variable), "PUBLIC", "client");
        addMetadata();
    }

    public QClient(String variable, String schema, String table) {
        super(QClient.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QClient(String variable, String schema) {
        super(QClient.class, forVariable(variable), schema, "client");
        addMetadata();
    }

    public QClient(Path<? extends QClient> path) {
        super(path.getType(), path.getMetadata(), "PUBLIC", "client");
        addMetadata();
    }

    public QClient(PathMetadata metadata) {
        super(QClient.class, metadata, "PUBLIC", "client");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(certificate, ColumnMetadata.named("certificate").withIndex(3).ofType(Types.BLOB).withSize(1073741824).notNull());
        addMetadata(id, ColumnMetadata.named("id").withIndex(1).ofType(Types.BIGINT).withSize(32).notNull());
        addMetadata(name, ColumnMetadata.named("name").withIndex(2).ofType(Types.VARCHAR).withSize(256).notNull());
    }

}

