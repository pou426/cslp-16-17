#!/bin/bash
# This script will be used to launch simulations 

if [[ $# == 0 ]]; then
	# display usage information
	echo "Usage information: "
	echo "./simulate.sh <input_filename> <flag1>"
	echo "where <flag1> = 'yes' if user wants to output logging information to console" 
	echo "see ./output_files/output.txt for program outputs."
	# exit 1

elif [[ -e $1 ]] ; then
	abs_path="$(cd "$(dirname $1)" && pwd)/$(basename "$1")"

	cd cslp/bin
	java cslp/Simulator $abs_path $2 > ../../output_files/output.txt
	# exit 1
	
else
	echo "File does not exist"
	echo "Simulation will terminate"

	exit 1
fi