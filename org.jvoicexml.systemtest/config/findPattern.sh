#!/bin/sh
( cd ../irtests; 

find . -name \*txml -print -exec grep "<transfer" {} \; | \
awk '{if(/txml/){printf $0 } else {print}}' | \
grep txml | \
sed -e 's/^.*\.txml\.//' | \
sed -e 's/^\.//' | \
sed -e 's/^\///' | \
sed -e 's/\/.*$//' | \
sort -u -g | \
sed -e 's/$/" ignore="\&lt;transfer\&gt; no implements" \/>/' | \
sed -e 's/^/<scriptDoc id="/' 

find . -name \*txml -print -exec grep "<subdialog" {} \; | \
awk '{if(/txml/){printf $0 } else {print}}' | \
grep txml | \
sed -e 's/^.*\.txml\.//' | \
sed -e 's/^\.//' | \
sed -e 's/^\///' | \
sed -e 's/\/.*$//' | \
sort -u -g | \
sed -e 's/$/" ignore="\&lt;subdialog\&gt; no implements" \/>/' | \
sed -e 's/^/<scriptDoc id="/' 

find . -name \*txml -print -exec grep "<initial" {} \; | \
awk '{if(/txml/){printf $0 } else {print}}' | \
grep txml | \
sed -e 's/^.*\.txml\.//' | \
sed -e 's/^\.//' | \
sed -e 's/^\///' | \
sed -e 's/\/.*$//' | \
sort -u -g | \
sed -e 's/$/" ignore="\&lt;initial\&gt; no implements" \/>/' | \
sed -e 's/^/<scriptDoc id="/'

)
