README.md
------------------------------------------------------------------------------
------------------------------------------------------------------------------

Synopsis
------------------------------------------------------------------------------

This is a command-line application for Computer Science Large Practical. The purpose of which is to execute stochastic
simulations of the bin collection process in a "smart" city. This simulator is developed in JAVA.

------------------------------------------------------------------------------
------------------------------------------------------------------------------

Input File Specification
------------------------------------------------------------------------------

If multiple inputs are found, the new ones are always ignored apart from the 'noArea' keyword. In which case, the new information always overwrite the previously stored data.

Throw errors if:
- incorrect data type has been parsed from input file
- 

Issue warnings if:



------------------------------------------------------------------------------
------------------------------------------------------------------------------

Class Explanation
------------------------------------------------------------------------------

Abstract Classes:
- AbstractEvent

Classes:
- Bag
- Bin
- DisposalEvent <- extends AbstractEvent ??????????????
- Error
- Lorry
- Random
- ServiceArea
- Simulator <- main() method

------------------------------------------------------------------------------
------------------------------------------------------------------------------

Code Structure
------------------------------------------------------------------------------

Code Structure - Reading Inputs:
- compile.sh compiles the java files from the command line
- simulate.sh executes the program with first argument as the input file
- Parsing of input file done in Simulator class <- parseInputs(String file_path) method
- Input parameters are stored in the Simulator class as static variables.
- Validation of input parameters done in Simualtor class <- validation() method
- Static variables in other classes are initialised inside the main() method.
- Each service area and their information is stored as a ServiceArea instance. All instances stored within the ArrayList called serviceAreas.
- Simulator class contains a Priority Queue to store all events and their time of occurence.
- start() method in Simulator class runs the simulator under the condition of whether the input file continas the experiment keyword.


Code Structure - Generate, Schedule and Execute Waste Disposal Events:
- When the simulation starts, the first disposal event for each bin is generated with the time parameter set as a random value returned by the erlangk() method in Random class. All disposal events are added to the events Priority Queue.
- When the execute(Simulator simulator) method gets called, the disposeBag(DisposalEvent e) method from the Bin class is in turn called, which generates a bag and disposes it at a specific time according to erlangk in the bin it belongs to, and print out the event and its subsequent events relating to the bin. Then a new time will be set according to erlangk and if that time is smaller than stop time of the simulation, this new DisposalEvent instance will be appended to the events Priority Queue.
- The doAllEvents() method from Simulator class polls an event from the events Priority Queue one at a time, increments the time and calls the execute(Simulator simulator) method.


Code Structure - Bin Collection Events:
- Not implemented yet


Code Structure - Outputs:
- When each AbstractEvent instances are executed, they print out an ouput on a single line in the following format: 
	<time> -> <event> <details>
- The simulator output time in the format days:hours:minutes:seconds.
- The following events are accounted:
	1. bag disposed of
	2. bag load/contents volume change
	3. bin occupancy threshold exceeded
	4. bin overflowed
	5. lorry arrived at/left location
	6. lorry load/contents volume changed

Code Structure - Statistical Analysis: (no implementation yet)
- Average Trip Duration
- Number of Trips per Schedule
- Trip Efficiency
- Average Volume Collected
- Percentage of Overflowed Bins
- Visualisation

------------------------------------------------------------------------------
------------------------------------------------------------------------------

Execution of Simulator
------------------------------------------------------------------------------

On command line, type:
$ chmod +x compile.sh
$ chmod +x simulate.sh
$ ./compile.sh
$ ./simulate.sh <input_file_name>

or 
$ ./simulate.sh

which will display usage information

------------------------------------------------------------------------------
------------------------------------------------------------------------------

Tests
------------------------------------------------------------------------------

A list of input text files are submitted in the root directory. With each input text script testing different aspects of the parsing part of the program.

The aspects tested by the input script is specified on the top of the file.
<!-- 
	input files		|		aspect(s) tested
----------------------------------------------------------------------
	input1.txt 		|		all correct inputs
	input2.txt 		|		different incorrect input parameters
	input3.txt 		|		overwriting service area information 
	input4.txt 		| 		check missing input parameters
	input5.txt 		|		check missing service area
	input6.txt 		| 		identify experiment keywords
	input7.txt 		|		checks number of experiment inputs
	input8.txt 		|		checks the max. number of bins
	input9.txt 		| 		checks if input exceeds maximum values


 -->