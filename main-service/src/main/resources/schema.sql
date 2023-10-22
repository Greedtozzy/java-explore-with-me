drop table if exists users, categories, locations, events, requests, compilations, event_compilation;

create table if not exists users (
id serial not null primary key,
name varchar(255) not null,
email varchar(255) not null unique
);

create table if not exists categories (
id serial not null primary key,
name varchar(255) not null unique
);

create table if not exists locations (
id serial not null primary key,
lat float not null,
lon float not null
);

create table if not exists events (
id serial not null primary key,
annotation varchar(2000) not null,
description varchar(7000) not null,
title varchar(120) not null,
state varchar(9) not null,
initiator_id bigint not null references users(id) on delete cascade,
category_id bigint not null references categories(id) on delete cascade,
location_id int not null references locations(id) on delete cascade,
created timestamp not null,
event_date timestamp not null,
published_on timestamp,
paid boolean,
request_moderation boolean,
participant_limit int default 0,
views int not null
);

create table if not exists requests (
id serial not null primary key,
event_id bigint not null references events(id) on delete cascade,
requester_id bigint not null references users(id) on delete cascade,
created timestamp not null,
status varchar(9) not null
);

create table if not exists compilations (
id serial not null primary key,
title varchar(1000) not null,
pinned boolean default false
);

create table if not exists event_compilation (
event_id bigint not null references events(id) on delete cascade,
compilation_id bigint not null references compilations(id) on delete cascade,
primary key (event_id, compilation_id)
);