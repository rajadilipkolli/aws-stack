create sequence customers_seq start with 1 increment by 50;

create table customers (
    id bigint DEFAULT nextval('customers_seq') not null,
    text varchar(1024) not null,
    primary key (id)
);
