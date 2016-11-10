#!/bin/bash
# This script will be used to compile the simulator application 

cd cslp
export CLASSPATH=bin:lib/*
javac -sourcepath src -d bin src/cslp/*.java
