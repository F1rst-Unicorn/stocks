-- You have to create an initial user for your database.
begin transaction isolation level serializable;

insert into "User" (name, creator_user, creator_user_device)
values ('<username>', 0, 0);

insert into "User_device" (name, belongs_to, creator_user, creator_user_device)
values ('<device>', lastval(), 0, 0);

update "User"
set creator_user = (select min("ID") from "User"),
creator_user_device = (select min("ID") from "User_device");

update "User_device"
set creator_user = (select min("ID") from "User"),
creator_user_device = (select min("ID") from "User_device");

insert into "Ticket" (ticket, belongs_device) values ('0000', lastval());

commit;
