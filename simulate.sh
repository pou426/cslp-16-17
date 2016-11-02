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
	echo "./simulate.sh <input_filename>"
	#echo "Run code using ./simulate.sh <input_file_name> [OPTIONS]"

	exit 1
fi


# usage information:
#$ ./simulate.sh
#Usage: ./simulate.sh <input_filename> [debug=on|off]
#Default option: debug=off </input_filename>