<?php
$cfg['blowfish_secret'] = 'your_secret_key_here';
$cfg['Servers'][1]['auth_type'] = 'config';
$cfg['Servers'][1]['host'] = 'mysql-container';
$cfg['Servers'][1]['port'] = '3306';
$cfg['Servers'][1]['socket'] = '';
$cfg['Servers'][1]['user'] = 'root';
$cfg['Servers'][1]['password'] = 'root';
$cfg['Servers'][1]['AllowNoPassword'] = false;
$cfg['ForceSSL'] = false;
?> 