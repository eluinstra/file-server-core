--
-- Copyright 2020 E.Luinstra
--
-- Licensed under the Apache License, Version 2.0 (the "License");
-- you may not use this file except in compliance with the License.
-- You may obtain a copy of the License at
--
--   http://www.apache.org/licenses/LICENSE-2.0
--
-- Unless required by applicable law or agreed to in writing, software
-- distributed under the License is distributed on an "AS IS" BASIS,
-- WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
-- See the License for the specific language governing permissions and
-- limitations under the License.
--

ALTER TABLE file ADD enc_algorithm SMALLINT 0 FALSE NOT NULL;
ALTER TABLE file ADD enc_secret CLOB NULL;

CREATE TABLE certificate
(
  id                SERIAL          NOT NULL,
  certificate       BYTEA           NOT NULL UNIQUE,
  time_stamp        TIMESTAMP       NOT NULL
);

INSERT INTO certificate SELECT id, certificate FROM fs_user;

ALTER TABLE fs_user DROP COLUMN certificate;
