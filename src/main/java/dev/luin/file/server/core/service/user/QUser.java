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
package dev.luin.file.server.core.service.user;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;

import com.querydsl.sql.ColumnMetadata;

import dev.luin.file.server.core.file.QFile;
import dev.luin.file.server.core.file.UserId;

import java.security.cert.X509Certificate;
import java.sql.Types;




/**
 * QUser is a Querydsl query type for QUser
 */
@Generated("com.querydsl.sql.codegen.MetaDataSerializer")
public class QUser extends com.querydsl.sql.RelationalPathBase<QUser> {

    private static final long serialVersionUID = -1;

    public static final QUser user = new QUser("fs_user");

    public final SimplePath<X509Certificate> certificate = createSimple("certificate", X509Certificate.class);

    public final SimplePath<UserId> id = createSimple("id", UserId.class);

    public final SimplePath<Username> name = createSimple("name", Username.class);

    public final com.querydsl.sql.PrimaryKey<QUser> sysPk10092 = createPrimaryKey(id);

    public final com.querydsl.sql.ForeignKey<QFile> _sysFk10112 = createInvForeignKey(id, "user_id");

    public QUser(String variable) {
        super(QUser.class, forVariable(variable), "PUBLIC", "fs_user");
        addMetadata();
    }

    public QUser(String variable, String schema, String table) {
        super(QUser.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QUser(String variable, String schema) {
        super(QUser.class, forVariable(variable), schema, "fs_user");
        addMetadata();
    }

    public QUser(Path<? extends QUser> path) {
        super(path.getType(), path.getMetadata(), "PUBLIC", "fs_user");
        addMetadata();
    }

    public QUser(PathMetadata metadata) {
        super(QUser.class, metadata, "PUBLIC", "fs_user");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(certificate, ColumnMetadata.named("certificate").withIndex(3).ofType(Types.BLOB).withSize(1073741824).notNull());
        addMetadata(id, ColumnMetadata.named("id").withIndex(1).ofType(Types.BIGINT).withSize(32).notNull());
        addMetadata(name, ColumnMetadata.named("name").withIndex(2).ofType(Types.VARCHAR).withSize(256).notNull());
    }

}

