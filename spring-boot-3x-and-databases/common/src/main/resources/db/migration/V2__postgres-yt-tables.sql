--
-- Copyright (c) 2024 Bear Giles <bgiles@coyotesong.com>.
-- All Rights Reserved.
--
-- Licensed under the Apache License, Version 2.0 (the "License");
-- you may not use this file except in compliance with the License.
-- You may obtain a copy of the License at
--
--    http://www.apache.org/licenses/LICENSE-2.0
--
-- Unless required by applicable law or agreed to in writing, software
-- distributed under the License is distributed on an "AS IS" BASIS,
-- WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
-- See the License for the specific language governing permissions and
-- limitations under the License.
--

--
-- Create tables
--

create table i18n_language
(
    key  serial not null,
    code text, -- not null,
    hl   text, -- not null,
    name text, -- not null,

    constraint i18n_language_pkey primary key (key)
);

create table i18n_region
(
    key  serial not null,
    code text   not null,
    hl   text   not null,
    name text   not null,
    gl   text,

    constraint i18n_region_pkey primary key (key)
);
