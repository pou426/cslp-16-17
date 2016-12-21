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
1. 	An initial bin collection event (an BinServiceEvent instance) is inserted into queue for each service area and stores the next bin service event with the time it should be scheduled if no delay. When executed, the simulator decides which bins to be serviced according to their occupancy and plan a route to visit all bins from and back to the depot.
2. 	The service area's lorry departs from the depot (LorryDepartureEvent) to the next location, and schedule another event to signal the arrival of lorry at that location (LorryArrivalEvent) at the specified time. 
3. 	When the lorry arrives at the bin, it begins to empty the bin, at this point, disposal events cannot occur at that bin. An BinEmptiedEvent instance is inserted which signals that the bin has been emptied and updates content of the bin and lorry.
4. If there are bins that still need to be visited, the lorry departs to that location and repeats steps 2 and 3 until there are no bins left, or if the lorry's capacity has been exceeded. 
5. Lorry only serviced a bin if it has enough capacity. If a lorry arrives at a bin and find out that its remaining capacity is not sufficient, it will go back to the depot through the shortest path, and empties its content. A LorryEmptiedEvent will be executed at a time from current simulation time that is 5 times the bin servicing time. 
6. Once the lorry is emptied, the simulator shall recompute a path to visit the remaining bins.
7. If one bin service event takes too long and it finishes at a time longer than the next service event for that service area, the next service event will be carried out right away.

Code Structure - Statistical Analysis:
Statistics that will be outputted to the console are:
- Average Trip Duration
- Number of Trips per Schedule
- Trip Efficiency
- Average Volume Collected
- Percentage of Overflowed Bins
- Visualisation
for each service area and averaged over all service areas. These statistics are only collected after a transient time (warmUpTime)

Code Structure - Mathematics and algorithms:
- Random: calculates erlang-k distribution
- Floyd-Warshall: computes shortest path for a given graph
- Nearest-neighbour and brute-force: carries out route planning

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


------------------------------------------------------------------------------
------------------------------------------------------------------------------

UPDATED: Part 3

------------------------------------------------------------------------------
------------------------------------------------------------------------------

Unfinished or non-obvious functionality:
------------------------------------------------------------------------------

The applicaton is considered to be complete following all specifications from the handout. Although, RandomPath class is an graph traversal algorithm that picks random vertices at it traverses the graph. The algorithm is done implementing but the code is not optimised. It has not been used in computing the path for bin collection due to time constraint. In that algorithm, it first computes the nearest neighbour path and use that as an upper bound. It returns a resulting route soon as it finds a path that is smaller than or equal to the upper bound.



------------------------------------------------------------------------------
------------------------------------------------------------------------------

Additional features:
------------------------------------------------------------------------------




------------------------------------------------------------------------------
------------------------------------------------------------------------------

Input scripts:
------------------------------------------------------------------------------
Testing scripts:

Testing scripts for part 3 are included in the folder '/input_scripts/' as well as '/cslp/src/test/'. The former includes input text scripts with different input paramters and area specification, where as the later contains JUnit tests to test various methods in the program.

Experimentation scripts:
Scripts used to perform experiments are included in the '/exp_scripts/' folder.

All input scripts contains comments in the beginning to state what aspect they tests/experiment with.


------------------------------------------------------------------------------
------------------------------------------------------------------------------

Output:
------------------------------------------------------------------------------
Output of summary statistics from the console is redirected to ./cslp/bin/output_files/output.txt

Plotted graphs are included in /cslp/bin/graphs/



