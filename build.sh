#!/bin/bash

if [ -f "Compiler.zip" ]; then
  rm "Compiler.zip"
fi

find . -name "*.txt" ! -name "testfile.txt" -exec rm {} \;

cd src/

zip -r ../Compiler.zip ./*