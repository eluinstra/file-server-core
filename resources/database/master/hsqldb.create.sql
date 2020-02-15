CREATE TABLE fs_client
(
	id								INTEGER					NOT NULL PRIMARY KEY,
	name							VARCHAR(256)		NOT NULL UNIQUE,
	certificate				BLOB						NOT NULL
);

CREATE TABLE fs_file
(
	virtual_path			VARCHAR(256)		NOT NULL PRIMARY KEY,
	real_path					VARCHAR(256)		NOT NULL,
	content_type			VARCHAR(256)		NOT NULL,
	timestamp					TIMESTAMP				DEFAULT NOW NOT NULL,
	start_date				TIMESTAMP				NOT NULL,
	end_date					TIMESTAMP				NOT NULL,
	client_id					INTEGER					NOT NULL,
	FOREIGN KEY (client_id) REFERENCES fs_client(id)
);
