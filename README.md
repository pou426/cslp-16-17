README.md
------------------------------------------------------------------------------
------------------------------------------------------------------------------

Synopsis


This is a command-line application for Computer Science Large Practical. The purpose of which is to execute stochastic
simulations of the bin collection process in a "smart" city. This simulator is developed in JAVA.

------------------------------------------------------------------------------
------------------------------------------------------------------------------

Code Explanation
------------------------------------------------------------------------------

Abstract Classes:
- AbstractEvent

Classes:
- Bag
- Bin
- DisposalEvent <- extends AbstractEvent
- InvalidInputFileException <- extends Exception
- Lorry
- Random
- ServiceArea
- Simulator <- main() method

Class descriptions:
- AbstractEvent:
.......??????????
?????????
?????????
?????????
????????
- Bag:
- Bin:
- DisposalEvent:
- InvalidInputFileException:
- Lorry:
- Random:
- ServiceArea:
- Simulator:


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
<!-- - Output Specification:
	<time> -> bag weighing <float> kg disposed of at bin <uint8_t>.<uint16_t>
	<time> -> load of bin <uint8_t>.<uint16_t> became <float> kg and contents volume <float> m^3
	<time> -> occupancy threshold of bin <uint8_t>.<uint16_t> exceeded
	<time> -> bin <uint8_t>.<uint16_t> overflowed
	<time> -> lorry <uint8_t> arrived at location <uint8_t>.<uint16_t>
	<time> -> load of lorry <uint8_t> became <float> kg and contents volume <float> m^3
	<time> -> lorry <uint8_t> left location <uint8_t>.<uint16_t>
	note: lorries are indexed according to their service area and bin x.y refers to bin y in area x.
 -->

Code Structure - Statistical Analysis:
- Average Trip Duration
- Number of Trips per Schedule
- Trip Efficiency
- Average Volume Collected
- Percentage of Overflowed Bins
- Visualisation

------------------------------------------------------------------------------
------------------------------------------------------------------------------

Motivation
------------------------------------------------------------------------------

The movativation of this practical is to study a smart city scnerario, in which bins are equipped with sensors to indicate their occupancy. By studying the simulation outputs, we hope to learn how the system behaves and find the optimal parameters in collection frequencies and route planning in order to reduce cost when this is implemented in real-life.

------------------------------------------------------------------------------
------------------------------------------------------------------------------

Execution of Simulator
------------------------------------------------------------------------------

in command line, type:
$ chmod +x compile.sh
$ chmod +x simulate.sh
$ ./compile.sh
$ ./simulate.sh <input_file_name> [OPTIONS]

or 
$ ./simulate.sh
which will display usage information

------------------------------------------------------------------------------
------------------------------------------------------------------------------

Tests
------------------------------------------------------------------------------

A list of input text files are submitted in the root directory. With each input text script testing different aspects of the parsing part of the program.

		input files		|		aspect tested
	---------------------------------------------------
		input1.txt 		|		
		input2.txt 		|		
		input3.txt 		|		
