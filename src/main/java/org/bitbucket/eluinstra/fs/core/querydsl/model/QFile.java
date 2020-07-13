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

import org.bitbucket.eluinstra.fs.core.file.FileType;

import com.querydsl.core.types.Path;

import com.querydsl.sql.ColumnMetadata;
import java.sql.Types;




/**
 * QFile is a Querydsl query type for QFile
 */
@Generated("com.querydsl.sql.codegen.MetaDataSerializer")
public class QFile extends com.querydsl.sql.RelationalPathBase<QFile> {

    private static final long serialVersionUID = -1706166711;

    public static final QFile file = new QFile("file");

    public final NumberPath<Long> clientId = createNumber("clientId", Long.class);

    public final StringPath contentType = createString("contentType");

    public final DateTimePath<java.time.Instant> endDate = createDateTime("endDate", java.time.Instant.class);

    public final StringPath filename = createString("filename");

    public final NumberPath<Long> fileLength = createNumber("file_length", Long.class);

    public final EnumPath<FileType> fileType = createEnum("file_type", FileType.class);

    public final StringPath md5Checksum = createString("md5Checksum");

    public final StringPath realPath = createString("realPath");

    public final StringPath sha256Checksum = createString("sha256Checksum");

    public final DateTimePath<java.time.Instant> startDate = createDateTime("startDate", java.time.Instant.class);

    public final DateTimePath<java.time.Instant> timestamp = createDateTime("timestamp", java.time.Instant.class);

    public final StringPath virtualPath = createString("virtualPath");

    public final com.querydsl.sql.PrimaryKey<QFile> sysPk10102 = createPrimaryKey(virtualPath);

    public final com.querydsl.sql.ForeignKey<QClient> sysFk10112 = createForeignKey(clientId, "id");

    public QFile(String variable) {
        super(QFile.class, forVariable(variable), "PUBLIC", "file");
        addMetadata();
    }

    public QFile(String variable, String schema, String table) {
        super(QFile.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QFile(String variable, String schema) {
        super(QFile.class, forVariable(variable), schema, "file");
        addMetadata();
    }

    public QFile(Path<? extends QFile> path) {
        super(path.getType(), path.getMetadata(), "PUBLIC", "file");
        addMetadata();
    }

    public QFile(PathMetadata metadata) {
        super(QFile.class, metadata, "PUBLIC", "file");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(clientId, ColumnMetadata.named("client_id").withIndex(10).ofType(Types.BIGINT).withSize(32).notNull());
        addMetadata(contentType, ColumnMetadata.named("content_type").withIndex(4).ofType(Types.VARCHAR).withSize(256).notNull());
        addMetadata(endDate, ColumnMetadata.named("end_date").withIndex(9).ofType(Types.TIMESTAMP).withSize(26).notNull());
        addMetadata(filename, ColumnMetadata.named("filename").withIndex(3).ofType(Types.VARCHAR).withSize(256).notNull());
        addMetadata(fileLength, ColumnMetadata.named("file_length").withIndex(11).ofType(Types.BIGINT).withSize(32).notNull());
        addMetadata(fileType, ColumnMetadata.named("file_type").withIndex(12).ofType(Types.SMALLINT).withSize(8).notNull());
        addMetadata(md5Checksum, ColumnMetadata.named("md5_checksum").withIndex(5).ofType(Types.VARCHAR).withSize(32).notNull());
        addMetadata(realPath, ColumnMetadata.named("real_path").withIndex(2).ofType(Types.VARCHAR).withSize(256).notNull());
        addMetadata(sha256Checksum, ColumnMetadata.named("sha256_checksum").withIndex(6).ofType(Types.VARCHAR).withSize(64).notNull());
        addMetadata(startDate, ColumnMetadata.named("start_date").withIndex(8).ofType(Types.TIMESTAMP).withSize(26).notNull());
        addMetadata(timestamp, ColumnMetadata.named("timestamp").withIndex(7).ofType(Types.TIMESTAMP).withSize(26).notNull());
        addMetadata(virtualPath, ColumnMetadata.named("virtual_path").withIndex(1).ofType(Types.VARCHAR).withSize(256).notNull());
    }

}

