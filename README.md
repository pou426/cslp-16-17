README.md
------------------------------------------------------------------------------
------------------------------------------------------------------------------

Synopsis:
------------------------------------------------------------------------------

This is a command-line application for Computer Science Large Practical. The purpose of which is to execute stochastic
simulations of the bin collection process in a "smart" city. This simulator is developed in JAVA.

------------------------------------------------------------------------------
------------------------------------------------------------------------------

Input File Specification:
------------------------------------------------------------------------------

If multiple inputs are found, the new ones are always ignored apart from the 'noArea' keyword. In which case, the new information always overwrite the previously stored data.

Throw errors if:
- incorrect data type has been parsed from input file
- inputs are negative (apart from roadsLayout matrix which could have -1)
- inputs exceed their maximum valie according to there data type
- insufficient no. of service area information 
- noAreas parameter's next line is not the areaIdx line
- incorrect number of roadsLayout row elements or insufficient rows

Issue warnings if:
- no inputs or too many inputs for a valid input parameters
- invalid parameter/line detected
- repeated input parameters
- inputs are 0's
- roadsLayout matrix not diagonally zero or zero (for non-diagonal elements)
- repeat areaIdx values, overwrite

------------------------------------------------------------------------------
------------------------------------------------------------------------------

Code Structure:
------------------------------------------------------------------------------

Code Structure - Reading Inputs:
- compile.sh compiles the java files from the command line.
- simulate.sh executes the program with first argument as the inpu.t file.
- Methods for parsing input file, validation input parameters and initialise other classes' static vairables within Parser class.
- Input parameters are stored in the Simulator class as static variables.
- Each service area and their information is stored as a ServiceArea instance. All instances stored within the ArrayList called serviceAreas.
- Simulator class contains a Priority Queue to store all events according their time of occurence, with methods to insert new events to list.
- start() method in Simulator class runs the simulator under the condition of whether the input file contains the experiment keyword.

Code Structure - Generate, Schedule and Execute Waste Disposal Events:
- When the simulation starts, the first disposal event for each bin is generated with the time parameter set as a random value returned by the erlangk() method in Random class. All disposal events are added to the events Priority Queue.
- When the execute(Simulator simulator) method gets called, the disposeBag(DisposalEvent e) method from the Bin class is in turn called, which generates a bag and disposes it at a specific time according to erlangk in the bin it belongs to, and print out the event and its subsequent events relating to the bin. Then a new time will be set according to erlangk and if that time is smaller than stop time of the simulation, this new DisposalEvent instance will be appended to the events Priority Queue.
- The doAllEvents() method from Simulator class polls an event from the events Priority Queue one at a time, increments the time and calls the execute(Simulator simulator) method.

Code Structure - Bin Collection Events:
- Not implemented yet

Code Structure - Statistical Analysis: (no implementation yet)
- Average Trip Duration
- Number of Trips per Schedule
- Trip Efficiency
- Average Volume Collected
- Percentage of Overflowed Bins
- Visualisation

------------------------------------------------------------------------------
------------------------------------------------------------------------------

Tests:
------------------------------------------------------------------------------

A list of input text files are submitted in the "input_scripts" directory. With each input text script testing different aspects of the parsing part of the program.

The aspects tested by the input script is specified on the top of the file.

The aspects each input script tested is specified as follows (according to input script number):
Non-experiment testing:
1. all correct inputs for non-experiment simulation with blank lines and comments
2. too many (valid and invalid) inputs at the END of the line. should only parse the first input
3 - 15: check invalid input
16 - 24: check incorrect input data type
25 - 40: check negative value inputs
41 - 43: check 0 value inputs
44 - 56: check exceed maximum and by commenting out the keywords to check missing keywords
57: check that inputs makes sense
58 - 60: checks experiment - identify experiments, check missing experiment keyword, only one input for experiment, correct experiment input but run as non experiment simulation (for now)
62 - 74: all area specification related checks - areaIdx format, noAreas = 0, no or insufficient service information, strict format of lines after noArea line, diagonal 0 matrix, roadsLayout matrix elements magnitude, number of row elements and number of rows, identify missing row, duplicate service area information
75 - 76: disposalDistrRate and disposalDistrShape = 0
77: test generation of events