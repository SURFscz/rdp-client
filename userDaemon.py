#!/usr/bin/python2.7
import socket
import sys

from samba.netcmd.main import cache_loader
from samba.netcmd import Command, SuperCommand

# Create a TCP/IP socket
sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

# Bind the socket to the port
server_address = ('0.0.0.0', 10000)
print >>sys.stderr, 'starting up on %s port %s' % server_address
sock.bind(server_address)

# Listen for incoming connections
sock.listen(1)

Command.show_command_error = lambda self, e: None

class mySambaTool(SuperCommand):
    subcommands = cache_loader()
    subcommands["user"] = None

def setuser(user, pwd):
    print >>sys.stderr, 'pwd %s' % pwd
    print >>sys.stderr, 'Update User %s' % user
    result = cmd._run("samba-tool", "user", *['setpassword', user, '--newpassword={}'.format(pwd)])
    if result != None:
        print >>sys.stderr, 'User %s not found, creating' % user
        result = cmd._run("samba-tool", "user", *['create', user, pwd, '--given-name={}'.format(user)])

cmd = mySambaTool()

while True:
    connection = None
    try:
        connection, client_address = sock.accept()
        print >>sys.stderr, 'connection from', client_address
        data = connection.recv(2048)
        if ":" in data:
            [user, pwd] = data.strip().split(":")
            setuser(user,pwd)
            connection.sendall("OK\n")
        else:
            print >>sys.stderr, 'no more data from', client_address
            break
        connection.close()
    except KeyboardInterrupt:
        if connection:
            connection.close()
        break

