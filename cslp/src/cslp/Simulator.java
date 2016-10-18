package cslp;

import java.awt.List;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Simulator {
	
	private static short lorryVolume;
	private static short lorryMaxLoad;
	private static float binServiceTime;
	private static float binVolume;
	private static float disposalDistrRate;
	private static short disposalDistrShape;
	private static float bagVolume;
	private static float bagWeightMin;
	private static float bagWeightMax;
	private static short noAreas;
	private static ArrayList<ServiceArea> serviceAreas = new ArrayList<ServiceArea>();
	private static float stopTime;
	private static float warmUpTime;
	
	// for storing experiment
	private static boolean isExperiment = false;
	private static ArrayList<Float> disposalDistrRateExp = new ArrayList<Float>(); // experiment
	private static ArrayList<Short> disposalDistrShapeExp = new ArrayList<Short>(); // experiment
	private static ArrayList<Float> serviceFreqExp = new ArrayList<Float>(); // experiment
	
	// to check that all inputs represent
	private static boolean lorryVolumeFound = false;
	private static boolean lorryMaxLoadFound = false;
	private static boolean binServiceTimeFound = false;
	private static boolean binVolumeFound = false;
	private static boolean disposalDistrRateFound = false;
	private static boolean disposalDistrShapeFound = false;
	private static boolean bagVolumeFound = false;
	private static boolean bagWeightMinFound = false;
	private static boolean bagWeightMaxFound = false;
	private static boolean noAreasFound = false;
	private static boolean serviceAreasFound = false;
	private static boolean stopTimeFound = false;
	private static boolean warmUpTimeFound = false;

	// read input file and set parameters
	// check that all inputs are there << implement better
	// check that the parameters are reasonable << implement
	public static void parseInputs(String file_path) throws FileNotFoundException, InvalidInputFileException {
		/**
		 * reads a file and parses all the inputs and store them into variables
		 * also checks whether any input is missing
		 */
		
		try {
			File file = new File(file_path);
			FileReader fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);
			String line;
			
			while ((line = br.readLine())!=null) {
				if (!(line.startsWith("#") || line.isEmpty())) {
					String[] tokens = line.split("\\s+");
					int tokensLen = tokens.length;
					
					switch (tokens[0]) {
					
					case "noAreas":
						if (noAreasFound) {
							System.out.println("Warning: Multiple inputs for noAreas variable. Disregarding this input.");
						} else if (tokensLen < 2) {
							throw new InvalidInputFileException("Invalid input format in this line: " + line);
						} else {
							if (tokensLen > 2)		System.out.println("Warning: too many inputs in this line: " + line);
							noAreas = Short.parseShort(tokens[1]);
							noAreasFound = true;
							
							while ((!serviceAreasFound) && (line = br.readLine()) != null) {
								if (!(line.startsWith("#") || line.isEmpty())) {
									String[] areaTokens = line.split("\\s+");
									if (!areaTokens[0].equals("areaIdx")) {
										throw new InvalidInputFileException("Invalid format after noAreas line or insufficient service area specifications");
									} else if (!((areaTokens.length == 8) && (areaTokens[2].equals("serviceFreq")) &&
	                                        (areaTokens[4].equals("thresholdVal")) && (areaTokens[6].equals("noBins")))) {
										throw new InvalidInputFileException("Invalid format for service area description line: " + line);
									} else {
										short areaIdx = Short.parseShort(areaTokens[1]);
	                                    float serviceFreq = Float.parseFloat(areaTokens[3]);
	                                    float thresholdVal = Float.parseFloat(areaTokens[5]);
	                                    int noBins = Integer.parseInt(areaTokens[7]);
	                                    if (noBins > 65535) {
	                                    	throw new InvalidInputFileException("noBins exceeds maximum 65,535 in line: "+line);
	                                    }
	                                    int m = noBins + 1; 
	                                    short[][] roadLayout = new short[m][m];
	                                    boolean areaFound = false;
	                                    
	                                    while ((!areaFound) && (line = br.readLine()) != null) {
	                                    	if (!(line.startsWith("#") || line.isEmpty())) {
	                                    		String[] layoutTokens = line.split("\\s+");
	                                    		if (!(layoutTokens[0].equals("roadsLayout"))) {
	                                    			throw new InvalidInputFileException("Incorrect format: roadsLayout keyword not in the next line of the areaIdx line for areaIdx = " + areaIdx + ".");
	                                    		} 
	                                    		int count = 0;
	                                    		while ((count < m) && ((line=br.readLine()) != null)) {
	                                    			String[] matrixTokens = line.trim().split("\\s+");
	                                    			if (!((matrixTokens[0].equals("#")) || line.isEmpty())) {
	                                    				if (matrixTokens.length != m) {
		                                                    throw new InvalidInputFileException("Incorrect number of row elements in roadlayout for areaIdx = " + areaIdx);
	                                    				} else {
	                                    					int index = 0;
		                                                    for (int col = 0; col < m; col++) {
		                                                        roadLayout[count][col] = Short.parseShort(matrixTokens[index]);                                                 
		                                                        index++;
		                                                    }
		                                                    count++;
		                                                }
	                                    			}
	                                    		}
	                                            // check the matrix is diagonally zeros
	                                            for (int d = 0; d < m; d++) {
	                                                if (roadLayout[d][d] != 0) {
	                                                    throw new InvalidInputFileException("Invalid format for the roadLayout matrix for areaIdx = " + areaIdx + ".");
	                                                }
	                                            }
	                                            // check that sufficient area information has been obtained 
	                                            if (count == m) {
	                                                areaFound = true;
	                                            } else {
	                                                throw new InvalidInputFileException("Incorrect no. of columns for the roadLayout matrix for areaIdx = " + areaIdx + ".");
	                                            }
	                                    	}
	                                    }
	                                    ServiceArea curr_sa = new ServiceArea(areaIdx,serviceFreq,thresholdVal,noBins,roadLayout);
	                                    serviceAreas.add(curr_sa);
	                                    if (serviceAreas.size() == noAreas)		serviceAreasFound = true;
									}
								}
							}
						}
						break;
						
					case "serviceFreq":
						if (noAreasFound) {
							System.out.println("Warning: Multiple inputs for noAreas variable. Previously stored area information will be overwritten.");
						} else if (!(tokens[1].equals("experiment"))) {
							throw new InvalidInputFileException("Invalid format: missing keyword \"experiment\" in this line: " + line);
						} else if (tokensLen < 3) {
							throw new InvalidInputFileException("Insufficient inputs for the serviceFreq experiment parameters in this line: "+line);
						} else {
							for (int i = 2; i < tokensLen; i++) {
								float serviceFreq = Float.parseFloat(tokens[i]);
								serviceFreqExp.add(serviceFreq);
							}
							isExperiment = true;
							while ((!noAreasFound) && ((line = br.readLine()) != null)) {
								if (!(line.startsWith("#") || line.isEmpty())) {
									String[] noAreasTokens = line.split("\\s+");
									if (!(noAreasTokens[0].equals("noAreas"))) {
										throw new InvalidInputFileException("Invalid format: noAreas should follow serviceFreq experiment line.");
									} else {
										if (noAreasTokens.length > 2) 	System.out.println("Warning: too many inputs for noAreas.");
										noAreas = Short.parseShort(noAreasTokens[1]);
										noAreasFound = true;
									}
								}
							}
							
							while ((!serviceAreasFound) && (line = br.readLine()) != null) {
								if (!(line.startsWith("#") || line.isEmpty())) {
									String[] areaTokens = line.split("\\s+");
									if (!areaTokens[0].equals("areaIdx")) {
										throw new InvalidInputFileException("Invalid format after noAreas line or insufficient service area specifications");
									} else if (!((areaTokens.length == 8) && (areaTokens[2].equals("serviceFreq")) &&
	                                        (areaTokens[4].equals("thresholdVal")) && (areaTokens[6].equals("noBins")))) {
										throw new InvalidInputFileException("Invalid format for service area description line: " + line);
									} else {
										short areaIdx = Short.parseShort(areaTokens[1]);
	                                    float serviceFreq = Float.parseFloat(areaTokens[3]);
	                                    float thresholdVal = Float.parseFloat(areaTokens[5]);
	                                    int noBins = Integer.parseInt(areaTokens[7]);
	                                    if (noBins > 65535) {
	                                    	throw new InvalidInputFileException("noBins exceeds maximum 65,535 in line: "+line);
	                                    }
	                                    int m = noBins + 1; 
	                                    short[][] roadLayout = new short[m][m];
	                                    boolean areaFound = false;
	                                    
	                                    while ((!areaFound) && (line = br.readLine()) != null) {
	                                    	if (!(line.startsWith("#") || line.isEmpty())) {
	                                    		String[] layoutTokens = line.split("\\s+");
	                                    		if (!(layoutTokens[0].equals("roadsLayout"))) {
	                                    			throw new InvalidInputFileException("Incorrect format: roadsLayout keyword not in the next line of the areaIdx line for areaIdx = " + areaIdx + ".");
	                                    		} 
	                                    		int count = 0;
	                                    		while ((count < m) && ((line=br.readLine()) != null)) {
	                                    			String[] matrixTokens = line.trim().split("\\s+");
	                                    			if (!((matrixTokens[0].equals("#")) || line.isEmpty())) {
	                                    				if (matrixTokens.length != m) {
		                                                    throw new InvalidInputFileException("Incorrect number of row elements in roadlayout for areaIdx = " + areaIdx);
	                                    				} else {
	                                    					int index = 0;
		                                                    for (int col = 0; col < m; col++) {
		                                                        roadLayout[count][col] = Short.parseShort(matrixTokens[index]);                                                 
		                                                        index++;
		                                                    }
		                                                    count++;
		                                                }
	                                    			}
	                                    		}
	                                            // check the matrix is diagonally zeros
	                                            for (int d = 0; d < m; d++) {
	                                                if (roadLayout[d][d] != 0) {
	                                                    throw new InvalidInputFileException("Invalid format for the roadLayout matrix for areaIdx = " + areaIdx + ".");
	                                                }
	                                            }
	                                            // check that sufficient area information has been obtained 
	                                            if (count == m) {
	                                                areaFound = true;
	                                            } else {
	                                                throw new InvalidInputFileException("Incorrect no. of columns for the roadLayout matrix for areaIdx = " + areaIdx + ".");
	                                            }
	                                    	}
	                                    }
	                                    ServiceArea curr_sa = new ServiceArea(areaIdx,serviceFreq,thresholdVal,noBins,roadLayout);
	                                    serviceAreas.add(curr_sa);
	                                    if (serviceAreas.size() == noAreas)		serviceAreasFound = true;
									}
								}
							}
						}
						break;
						
					case "lorryVolume":
						if (lorryVolumeFound) {
							System.out.println("Warning: Multiple inputs for lorryVolume variable. Disregarding this input.");
						} else if (tokensLen < 2) {
							throw new InvalidInputFileException("Invalid input format in this line: " + line);
						} else {
							if (tokensLen > 2)		System.out.println("Warning: too many inputs in this line: " + line);
							lorryVolume = Short.parseShort(tokens[1]);
							lorryVolumeFound = true;
						}  
						break;
					
					case "lorryMaxLoad":
						if (lorryMaxLoadFound) {
							System.out.println("Warning: Multiple inputs for lorryMaxLoad variable. Disregarding this input.");
						} else if (tokensLen < 2) {
							throw new InvalidInputFileException("Invalid input format in this line: " + line);
						} else {
							if (tokensLen > 2)		System.out.println("Warning: too many inputs in this line: " + line);
							lorryMaxLoad = Short.parseShort(tokens[1]);
							lorryMaxLoadFound = true;
						}
						break;
					
					case "binServiceTime":
						if (binServiceTimeFound) {
							System.out.println("Warning: Multiple inputs for binServiceTime variable. Disregarding this input.");
						} else if (tokensLen < 2) {
							throw new InvalidInputFileException("Invalid input format in this line: " + line);
						} else {
							if (tokensLen > 2)		System.out.println("Warning: too many inputs in this line: " + line);
							binServiceTime = Float.parseFloat(tokens[1]);
							binServiceTimeFound = true;
						}
						break;
	
					case "binVolume":
						if (binVolumeFound) {
							System.out.println("Warning: Multiple inputs for binVolume variable. Disregarding this input.");
						} else if (tokensLen < 2) {
							throw new InvalidInputFileException("Invalid input format in this line: " + line);
						} else {
							if (tokensLen > 2)		System.out.println("Warning: too many inputs in this line: " + line);
							binVolume = Float.parseFloat(tokens[1]);
							binVolumeFound = true;	
						}				
						break;
					
					case "disposalDistrRate":
						if (disposalDistrRateFound) {
							System.out.println("Warning: Multiple inputs for disposalDistrRate variable. Disregarding this input.");
						} else if (tokensLen >= 2) {
							if (tokens[1].equals("experiment")) {
								if (tokensLen <= 3)		throw new InvalidInputFileException("Invalid input format in this line: " + line);
								for (int i = 2; i < tokensLen; i++) {
									float ddr = Float.parseFloat(tokens[i]);
									disposalDistrRateExp.add(ddr);
									isExperiment = true;
								}
								disposalDistrRateFound = true;
							} else {
								if (tokensLen > 2)		System.out.println("Warning: too many inputs in this line: " + line);
								disposalDistrRate = Float.parseFloat(tokens[1]);
								disposalDistrRateFound = true;
							}
						}  else throw new InvalidInputFileException("Invalid input format in this line: " + line);
					break;
					
					case "disposalDistrShape":
						if (disposalDistrShapeFound) {
							System.out.println("Warning: Multiple inputs for disposalDistrShape variable. Disregarding this input.");
						} else if (tokensLen >= 2) {
							if (tokens[1].equals("experiment")) {
								if (tokensLen <= 3)		throw new InvalidInputFileException("Invalid input format in this line: " + line);
								for (int i = 2; i < tokensLen; i++) {
									short dds = Short.parseShort(tokens[i]);
									disposalDistrShapeExp.add(dds);
									isExperiment = true;
								}
								disposalDistrShapeFound = true;
							} else {
								if (tokensLen > 2)		System.out.println("Warning: too many inputs in this line: " + line);
								disposalDistrShape = Short.parseShort(tokens[1]);
								disposalDistrShapeFound = true;
							}
						}  else throw new InvalidInputFileException("Invalid input format in this line: " + line);
					break;
					
					case "bagVolume":
						if (bagVolumeFound) {
							System.out.println("Warning: Multiple inputs for bagVolume variable. Disregarding this input.");
						}  else if (tokensLen < 2) {
							throw new InvalidInputFileException("Invalid input format in this line: " + line);
						} else {
							if (tokensLen > 2)		System.out.println("Warning: too many inputs in this line: " + line);
							bagVolume = Float.parseFloat(tokens[1]);
							bagVolumeFound = true;
						}  
						break;
					
					case "bagWeightMin":
						if (bagWeightMinFound) {
							System.out.println("Warning: Multiple inputs for bagWeightMin variable. Disregarding this input.");
						} else if (tokensLen < 2) {
							throw new InvalidInputFileException("Invalid input format in this line: " + line);
						} else {
							if (tokensLen > 2)		System.out.println("Warning: too many inputs in this line: " + line);
							bagWeightMin = Float.parseFloat(tokens[1]);
							bagWeightMinFound = true;
						}  
						break;
					
					case "bagWeightMax":
						if (bagWeightMaxFound) {
							System.out.println("Warning: Multiple inputs for bagWeightMax variable. Disregarding this input.");
						} else if (tokensLen < 2) {
							throw new InvalidInputFileException("Invalid input format in this line: " + line);
						} else {
							if (tokensLen > 2)		System.out.println("Warning: too many inputs in this line: " + line);
							bagWeightMax = Float.parseFloat(tokens[1]);
							bagWeightMaxFound = true;
						}  
						break;
					
					case "stopTime":
						if (stopTimeFound) {
							System.out.println("Warning: Multiple inputs for stopTime variable. Disregarding this input.");
						} else if (tokensLen < 2) {
							throw new InvalidInputFileException("Invalid input format in this line: " + line);
						} else {
							if (tokensLen > 2)		System.out.println("Warning: too many inputs in this line: " + line);
							stopTime = Float.parseFloat(tokens[1]);
							stopTimeFound = true;
						}  
						break;
					
					case "warmUpTime":
						if (warmUpTimeFound) {
							System.out.println("Warning: Multiple inputs for warmUpTime variable. Disregarding this input.");
						} else if (tokensLen < 2) {
							throw new InvalidInputFileException("Invalid input format in this line: " + line);
						} else {
							if (tokensLen > 2)		System.out.println("Warning: too many inputs in this line: " + line);
							warmUpTime = Float.parseFloat(tokens[1]);
							warmUpTimeFound = true;
						}
						break;
						
					default: throw new InvalidInputFileException("Invalid input parameter in this line: " + line);
					}
				}
			}

			br.close();;
			
			// check that all inputs are present and valid:
			// identifying missing parameters and wrong order;
			if (!lorryVolumeFound || !lorryMaxLoadFound || !binServiceTimeFound || !binVolumeFound || 
					!disposalDistrRateFound || !disposalDistrShapeFound || !bagVolumeFound || !bagWeightMinFound || 
					!bagWeightMaxFound || !noAreasFound || !serviceAreasFound || !stopTimeFound || 
					!warmUpTimeFound) {
				throw new InvalidInputFileException("Missing inputs.");
				// create an array to store all booleans
				// use switch case to throw error 
				// do I need the serviceAreasFound tho?? already throw error in loop
			}
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	
	// run the simulator
	public static void runSimulator() {
		if (isExperiment) {
			// run experiments
		} else {
			int time = 0; // in seconds
			int max_time = Math.round(stopTime);
			while (time <= max_time) {
				// determine the set of events that may occur after the current state
				/*
				 * possible events:
				 * (i) a rubbish bag was disposed of in a bin
				 * (ii) the occupancy threshold of a bin was exceeded
				 * (iii)  bin overflowed
				 * (iv) a bin was emptied
				 * (v) a lorry was emptied
				 * (vi) a lorry arrived/departed from a location (bin or depot)
				 */
				// delay = choose a delay base on the nearest event
				int delay = 0;
				time += delay;
				// modify the state of the system based on the current event.
			}
		}
	}
	
	// for sanity check
	public static void myCheck() {
		// for checking (without experiment keyword..)
		System.out.println(lorryVolume);
		System.out.println(lorryMaxLoad);
		System.out.println(binServiceTime);
		System.out.println(binVolume);
		System.out.println(disposalDistrRate);
		System.out.println(disposalDistrShape);
		System.out.println(bagVolume);
		System.out.println(bagWeightMin);
		System.out.println(bagWeightMax);
		System.out.println(noAreas);
		for (ServiceArea sa : serviceAreas) sa.print();
		System.out.println(stopTime);
		System.out.println(warmUpTime);
		
		System.out.println(isExperiment);
		for (Float sa : disposalDistrRateExp) System.out.println("disposalDistrRateExp: " + sa);
		for (Short sa : disposalDistrShapeExp) System.out.println("disposalDistrShapeExp: " + sa);
		for (Float sa : serviceFreqExp) System.out.println("serviceFreqExp: " + sa);
	}
	
	public static void main(String[] args) throws FileNotFoundException, InvalidInputFileException {
		
		// II.4. The program displays usage information if no input files are given;
		if ((args.length == 0) || (args[0].length() <= 4)) {
			throw new IllegalArgumentException("\nFound missing or invalid input arguments. "
					+ "\nRun code using \"./simulate.sh <input_file_name> [OPTIONS]\"");
		} 
		
		String file_path = args[0];
		
		parseInputs(file_path);
		
		// for sanity check
		myCheck();
		
    } 
	
	
}