# Server Installation

Installation on Arch Linux is done via the ansible role (see references). You
are supposed to review the variables defined in `defaults/main.yml` and
overwrite them according to your needs. The variables in `vars/main.yml` are
only used as internal shortcuts and don't need modifications.

The Debian package does not depend on the Ansible role, however there is a bit
of manual setup required: After installing the package you may have to edit the
stocks-server configuration and create an initial user as shown in
`/usr/share/doc/stocks-server/`.

## Generating new DH parameters

For improved security you might want to generate Diffie-Hellman parameters used
for the DH key exchange. The Ansible role does this by default, this behaviour
can be changed through the `stocks_nginx_generate_dhparam` variable.

You can also regenerate these manually using `openssl dhparam`, see `man 1
dhparam` for more information. After generation you must configure nginx to use
these parameters via the
[`ssl_dhparam`](https://nginx.org/en/docs/http/ngx_http_ssl_module.html#ssl_dhparam)
setting.

## References

* https://j.njsm.de/git/ansible/stocks

## License

Copyright (C)  2019  The stocks developers

Permission is granted to copy, distribute and/or modify this document
under the terms of the GNU Free Documentation License, Version 1.3
or any later version published by the Free Software Foundation;
with no Invariant Sections, no Front-Cover Texts, and no Back-Cover Texts.
A copy of the license is included in the section entitled "GNU
Free Documentation License".
