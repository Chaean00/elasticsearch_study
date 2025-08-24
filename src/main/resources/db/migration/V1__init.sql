CREATE SCHEMA IF NOT EXISTS public;
-- 방법 1: search_path 설정
SET search_path TO public;

create table if not exists products (
    id bigserial primary key,
    name text not null,
    price numeric(10, 2) not null default 0,
    category varchar(64) not null default 'DEFAULT',
    created_at timestamp not null default now(),
    updated_at timestamp not null default now()
);

create index if not exists idx_products_updated_id on products (updated_at, id);