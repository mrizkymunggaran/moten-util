#!/bin/bash
CHANNEL=$1
TITLE=$2
CHANNEL_EXPRESSION="dvb://.*@$CHANNEL\b"
ps -ef|grep mplayer|grep dvb|grep -e "$CHANNEL_EXPRESSION"|grep "$TITLE"|awk '{print $2}'|xargs kill
ps -ef|grep mencoder|grep dvb|grep -e "$CHANNEL_EXPRESSION"|grep "$TITLE"|awk '{print $2}'|xargs kill
