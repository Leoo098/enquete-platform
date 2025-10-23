create table users(
    id uuid not null primary key,
    username varchar(20) not null,
    email varchar(255) not null,
    password char(60) not null,
    role varchar(20) not null default 'USER' check (role in('USER', 'ADMIN'),
    social_login boolean default 'false')
);

create table polls(
    id uuid primary key,
    question varchar(100) not null,
    created_at timestamp not null,
    expires_at timestamp not null,
    user_id uuid not null,
    is_public boolean,

    constraint fk_user foreign key (user_id) references "users"(id)
);

create table options(
    id bigserial primary key,
    text varchar(50) not null,
    poll_id uuid not null,

    constraint fk_poll foreign key (poll_id) references "polls"(id)
);

create table votes(
    id bigserial primary key,
    user_id uuid not null,
    option_id bigint not null,
    voted_at timestamp with time zone not null,

    constraint fk_user foreign key (user_id) references "users"(id),
    constraint fk_option foreign key (option_id) references "options"(id)
);

create table client(
  id uuid not null primary key,
  client_id varchar(150) not null,
  client_secret varchar(400) not null,
  redirect_uri varchar(200) not null,
  scope varchar(50)
);