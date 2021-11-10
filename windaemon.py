#! /usr/bin/env python
import subprocess
import json

from flask import Flask, request, jsonify
app = Flask(__name__)

def run(cmd):
  result = subprocess.run(["powershell", "-Command", cmd], capture_output=True)
  return result

@app.route('/', methods=['GET'])
def index():
  return "index"

@app.route('/ps', methods=['POST'])
def ps():
  result = {}
  post = request.json
  user = post.get('user', 'foobar')
  pwd = post.get('password', 'test')
  pscmd = run(f"C:\\Users\\Administrator\\Python\\test\\updadd_user.ps1 {user} {pwd}")
  result['stdout'] = pscmd.stdout.decode('utf-8')
  result['stderr'] = pscmd.stderr.decode('utf-8')
  #result['name'] = user
  #result['password'] = pwd
  return jsonify(result)

app.run(host='0.0.0.0', port=5001)


