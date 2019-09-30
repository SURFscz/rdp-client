# Windows RDP client

Guacamole based Windows RDP client and Samba AD support scripts

## Installation
```
$ apt install maven
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
## Build guacd
```
apt install libavcodec-dev libavutil-dev libswscale-dev libfreerdp-dev libpango1.0-dev libssh2-1-dev libtelnet-dev libvncserver-dev libpulse-dev libssl-dev libvorbis-dev libwebp-dev
apt install libcairo2-dev libjpeg62-turbo-dev libpng-dev libossp-uuid-dev
apt install git build-essential autoconf libtool
git clone git://github.com/apache/guacamole-server.git
cd guacamole-server
autoreconf -fi
./configure --with-init-dir=/etc/init.d
```

## Warning
Only tested with Tomcat8/Chrome 77

Clipboard API only works from HTTPS endpoint!
