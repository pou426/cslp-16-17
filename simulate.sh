#!/bin/bash
# This script will be used to launch simulations 

if [[ $# == 0 ]]; then
	# display usage information
	echo "Simulate.sh: Usage information: "
	echo "Simulate.sh: ./simulate.sh <input_filename> <flag1>"
	echo "Simulate.sh: where <flag1> = 'yes' if user wants to output logging information to console" 
	# exit 1

elif [[ -e $1 ]] ; then
	abs_path="$(cd "$(dirname $1)" && pwd)/$(basename "$1")"

	cd cslp/bin
	java cslp/Simulator $abs_path $2
	# exit 1
	echo "Simulate.sh: Simulation end."
	echo "Simulate.sh: see ./cslp/bin/output_files/output.txt for program output."
	
else
	echo "Simulate.sh: File does not exist"
	echo "Simulate.sh: Simulation will terminate"

	exit 1
fi