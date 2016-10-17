#!/bin/bash
# This script will be used to launch simulations 

if [[ -e $1 ]] ; then
	abs_path="$(cd "$(dirname $1)" && pwd)/$(basename "$1")"

	cd cslp/bin
	java cslp/Simulator $abs_path
else
	echo '"$1" does not exist; exiting.'
	exit 1
fi
