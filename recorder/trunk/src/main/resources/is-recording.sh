#!/bin/bash
# parameters
# 1. channel
channel=$1
ps -ef|grep mencoder|grep -i ${channel}|grep -v grep 