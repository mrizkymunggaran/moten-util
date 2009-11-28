#!/bin/bash
# parameters
# 1. channel
channel=$1
startDate=$2
ps -ef|grep mencoder|grep -i ${channel}|grep ${startDate}|grep -v grep 