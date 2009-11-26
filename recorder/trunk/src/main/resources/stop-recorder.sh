#!/bin/bash
ps -ef|grep mplayer|grep dvb|awk '{print $2}'|xargs kill
ps -ef|grep mencoder|grep dvb|awk '{print $2}'|xargs kill
