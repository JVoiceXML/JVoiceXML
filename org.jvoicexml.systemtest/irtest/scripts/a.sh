#!/bin/sh
( cd ../irtests; \
find . -name \*txml -print -exec grep subdialog {} \; | \
awk '{if(/txml/){printf $0 } else {print}}' | \
grep txml |
sed -e 's/^.*\.txml\.//' |
sed -e 's/^\.//' | 
sed -e 's/^\///' |
sed -e 's/\/.*$//' |
sort -u -g |
sed -e 's/$/" ignore="subdialog no implements" \/>/' |
sed -e 's/^/<scriptDoc id="/'
)
