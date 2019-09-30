# Windows RDP client

Guacamole based Windows RDP client and Samba AD support scripts

## Installation
```
$ mvn package
...
$ cp target/guacamole-tutorial-1.0.0.war /var/lib/tomcat8/webapps/

$ cat var/lib/tomcat8/conf/guacamole-test.properties
adc_host = buster.example.org
rdp_host = rdp.example.org
rdp_port = 3389
guacd_host = localhost
guacd_port = 4822
```

Only tested with Tomcat8/Chrome 77
Warning: Clipboard API only works from HTTPS endpoint!
