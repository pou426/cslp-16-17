#!/bin/bash
# This script will be used to launch simulations 

if [[ $# == 0 ]]; then
	# display usage information
	echo "Usage information: "
	echo "./simulate.sh <input_filename>"
	# exit 1

elif [[ -e $1 ]] ; then
	abs_path="$(cd "$(dirname $1)" && pwd)/$(basename "$1")"

	cd cslp/bin
	java cslp/Simulator $abs_path
	# exit 1
	
else
	echo "File does not exist"
	echo "Simulation will terminate"

	exit 1
fi