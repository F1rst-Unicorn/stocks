# Server Installation

Installation is done via the ansible role provided in deploy-server/roles.
You are supposed to review the variables defined in defaults/main.yml and
overwrite them according to your needs. The variables in vars/main.yml are
only used as internal shortcuts and don't need modifications.

## Role variables

* `mysql_server_host`: The hostname of the DB server. URL or IP address are
   supported here.
* `mysql_server_port`: Port number of the DB server.
* `mysql_root_password`: The root password of the DB server.
* `mysql_stocks_database`: The name of the DB the stocks server shall use.
* `mysql_stocks_username`: Username of the DB user.
* `mysql_stocks_password`: Password of the DB user.
* `stocks_first_user`: The name of the first user in the stocks system.
* `stocks_first_device`: The name of the first device of the first user.
* `stocks_ticket_timeout_minutes`: Maximum allowed time between new device
   creation and its first registration to the server in minutes.
   This timeout should be long enough for a new user to carry out the
   registration process.
* `sudoers_line`: This line is added to the sudoers file to allow the stocks
   server to reload the nginx server. This process is needed to forward
   revokation of deleted devices. It is added here for transparency.
