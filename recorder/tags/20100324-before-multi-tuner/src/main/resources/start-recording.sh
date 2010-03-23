#!/bin/bash
#pass in as parameters to the script
# 1. channel alias
# 2. tuner no
# 3. yyyyMMdd_HHmm date
# 4. HHmm duration
# 5. title
# 6. series no
# 7. episode no

channel=$1
tunerNo=$2
date=$3
duration=$4
title=$5

mencoder dvb://${channel} -quiet -oac mp3lame -lameopts abr:br=128  -ovc lavc -lavcopts vcodec=mpeg4:vhq:v4mv:vqmin=2:vbitrate=922 -vf pp=de,crop=0:0:0:0,scale=480:-2 -o "/home/dave/Video/TV/${date}_${title}.avi"