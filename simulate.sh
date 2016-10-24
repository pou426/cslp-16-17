#!/bin/bash
# This script will be used to launch simulations 

if [[ -e $1 ]] ; then
	abs_path="$(cd "$(dirname $1)" && pwd)/$(basename "$1")"

	cd cslp/bin
	java cslp/Simulator $abs_path
	echo $abs_path

else
	#echo '"$1" does not exist; exiting.'
	# display usage information
	echo "Usage information: "
	echo "Found missing or invalid input arguments."
	echo "Run code using ./simulate.sh <input_file_name> [OPTIONS]"

	exit 1
fi
