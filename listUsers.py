#!/usr/bin/python2.7
import sys
import signal
signal.signal(signal.SIGINT, signal.SIG_DFL)

from samba.netcmd.main import cmd_sambatool
cmd = cmd_sambatool()

try:
    retval = cmd._run("samba-tool", "user", *['list'])
except SystemExit as e:
    retval = e.code
except Exception as e:
    cmd.show_command_error(e)
    retval = 1
sys.exit(retval)

