#!/bin/bash

#expects the last part of the filename only (not the full path)
FILENAME=$1

#remove prefixing timestamp stuff (e.g 20100228_0930_)
QUERY=`echo $FILENAME|sed 's/[0-9]*_[0-9]*_//'`

#remove the extension, replace underscores with spaces, 
#replace periods with spaces, then replace all spaces with +
QUERY=`echo $QUERY|sed 's/\.[a-z]*$//'|sed 's/_/ /g'|sed 's/\./ /g'|sed 's/ /+/g'`

#make an imdb url with the query
URL="http://www.imdb.com/find?s=all&q=$QUERY"

#open the url in a browser
firefox $URL
