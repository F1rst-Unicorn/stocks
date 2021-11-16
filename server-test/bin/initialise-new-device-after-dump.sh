#!/bin/bash

STOCKS_ROOT="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )/../.."

(cat | ssh dp-server sudo -u postgres psql stocks) <<EOF
begin transaction isolation level serializable;

insert into user_device (name, belongs_to, initiates) values
('dp-android', 0, 0);

update user_device
set initiates = (
        select min(id)
        from user_device
        where valid_time_start <= current_timestamp
        and current_timestamp < valid_time_end
        and transaction_time_end = 'infinity')
where id = lastval();

update user_device
set belongs_to = (
        select min(id)
        from "user"
        where valid_time_start <= current_timestamp
        and current_timestamp < valid_time_end
        and transaction_time_end = 'infinity')
where id = lastval();

insert into ticket (ticket, belongs_device, created_on)
select id :: text, id, 'infinity'
from current_user_device;

commit;
EOF
