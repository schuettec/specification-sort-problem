create table address (id bigint not null, city varchar(255), street varchar(255), primary key (id))
create table person (id bigint not null, name varchar(255), address_id bigint, primary key (id))
alter table person add constraint FKk7rgn6djxsv2j2bv1mvuxd4m9 foreign key (address_id) references address
