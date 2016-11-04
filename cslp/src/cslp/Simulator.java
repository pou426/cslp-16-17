package cslp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.PriorityQueue;

/**
 * Simulator class performs the following:
 * 1. input parsing
 * 2. input validation
 * 3. initialize all parameters and create a Simulator instance
 * 4. run simulator
 * 5. collect statistics and run analysis 
 *
 */
public class Simulator {
	
	// input parameters
	private static short lorryVolume;
	private static int lorryMaxLoad;
	private static int binServiceTime;
	private static float binVolume;
	private static float disposalDistrRate; // expressed as avg. no. of disposal events per hour
	private static short disposalDistrShape;
	private static float bagVolume;
	private static float bagWeightMin;
	private static float bagWeightMax;
	private static short noAreas;
	private static ArrayList<ServiceArea> serviceAreas = new ArrayList<ServiceArea>(); // serviceFreq experessed as x trips per hour
	private static float stopTime;
	private static float warmUpTime;
	
	// Arraylists for storing experiment input parameters
	private static boolean isExperiment = false; // indicator for experiment inputs
	private static ArrayList<Float> disposalDistrRateExp = new ArrayList<Float>();
	private static ArrayList<Short> disposalDistrShapeExp = new ArrayList<Short>();
	private static ArrayList<Float> serviceFreqExp = new ArrayList<Float>();
	
	// check that all input parameters represent
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

	/* 
	 * methods to catch exceptions when parsing int/short/float strings from file
	 * Returns boolean for the program to handle
	 * methods are called whenever the input parameter is supposed to be an int or short or float
	 */
	private boolean tryParseInt(String value) {  
	     try {  
	         Integer.parseInt(value);  
	         return true;  
	      } catch (NumberFormatException e) {  
	         return false;  
	      }  
	}
	private boolean tryParseShort(String value) {  
	     try {  
	         Short.parseShort(value);  
	         return true;  
	      } catch (NumberFormatException e) {  
	         return false;  
	      }  
	}
	private boolean tryParseFloat(String value) {  
	     try {  
	         Float.parseFloat(value);  
	         return true;  
	      } catch (NumberFormatException e) {  
	         return false;  
	      }  
	}

	/**
	 * reads a file and parses all the inputs
	 * stores them into variables
	 * checks invalid inputs or input formats
	 */
	public void parseInputs(String file_path) throws FileNotFoundException, InvalidInputFileException {	
		try {
			File file = new File(file_path);
			FileReader fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);
			String line;
			
			while ((line = br.readLine())!=null) {
				if (!(line.startsWith("#") || line.isEmpty())) { // ignore comments and blank lines
					String[] tokens = line.split("\\s+");
					int tokensLen = tokens.length;
					
					boolean canParse;		// boolean for int/short/float inputs
					switch (tokens[0]) {	// check the first word in each line for input parameter
					case "noAreas":
						if (noAreasFound) {
							System.out.println("Warning: noAreas variable has already been found. Overwriting the previous input.");
						} else if (tokensLen < 2) {
							System.out.println("Warning: Missing input in this line: " + line);
						} else {
							if (tokensLen > 2)		System.out.println("Warning: Too many inputs for noAreas variable.");
							canParse = tryParseShort(tokens[1]);
							if (!canParse)			throw new InvalidInputFileException("Error: noAreas input type mismatch at line = "+line);
							noAreas = Short.parseShort(tokens[1]);
							if (noAreas < 0)		throw new InvalidInputFileException("Error: noAreas cannot be negative.");
							if (noAreas == 0)		System.out.println("Warning: The noAreas variable is zero.");
							noAreasFound = true;
							
							while ((!serviceAreasFound) && (line = br.readLine()) != null) {	// this block looks for service area specifications
								if (!(line.startsWith("#") || line.isEmpty())) {
									String[] areaTokens = line.split("\\s+");
									if (!areaTokens[0].equals("areaIdx")) {
										throw new InvalidInputFileException("\nError: Invalid format."
												+ "\nThe line following noAreas variable should be in the format:"
												+ "\nareaIdx <uint8_t> serviceFreq <float> thresholdVal <float> noBins <uint16_t>");
									} else if (!((areaTokens.length == 8) && (areaTokens[2].equals("serviceFreq")) &&
	                                        (areaTokens[4].equals("thresholdVal")) && (areaTokens[6].equals("noBins")))) {
										throw new InvalidInputFileException("Error: Invalid format for service area description line: " + line);
									} else {
										canParse = tryParseShort(areaTokens[1]);
										if (!canParse)			throw new InvalidInputFileException("Error: areaIdx input type mismatch at line = "+line);
										short areaIdx = Short.parseShort(areaTokens[1]);
										if (areaIdx < 0)	throw new InvalidInputFileException("Error: areaIdx cannot be negative.");
	                                    
										canParse = tryParseFloat(areaTokens[3]);
										if (!canParse)			throw new InvalidInputFileException("Error: serviceFreq input type mismatch at line = "+line);
										float serviceFreq = Float.parseFloat(areaTokens[3]);
	                                    if (serviceFreq < 0)	throw new InvalidInputFileException("Error: serviceFreq cannot be negative.");
	                                    
	                                    canParse = tryParseFloat(areaTokens[5]);
	                                    if (!canParse)			throw new InvalidInputFileException("Error: thresholdVal input type mismatch at line = "+line);
	                                    float thresholdVal = Float.parseFloat(areaTokens[5]);
	                                    if (thresholdVal < 0)	throw new InvalidInputFileException("Error: thresholdVal cannot be negative.");
	                                    
	                                    canParse = tryParseInt(areaTokens[7]);
	                                    if (!canParse)			throw new InvalidInputFileException("Error: noBins input type mismatch at line = "+line);
	                                    int noBins = Integer.parseInt(areaTokens[7]);
	                                    if (noBins < 0)	throw new InvalidInputFileException("Error: noBins cannot be negative.");
	                                    if (noBins > 65535) {
	                                    	throw new InvalidInputFileException("Error: noBins exceeds maximum 65,535.");
	                                    }
	                                    int m = noBins + 1; 
	                                    short[][] roadLayout = new short[m][m];
	                                    boolean areaFound = false;
	                                    
	                                    while ((!areaFound) && (line = br.readLine()) != null) {	// this block looks for roadlayout matrix
	                                    	if (!(line.startsWith("#") || line.isEmpty())) {
	                                    		String[] layoutTokens = line.split("\\s+");
	                                    		if (!(layoutTokens[0].equals("roadsLayout"))) {
	                                    			throw new InvalidInputFileException("Error: Incorrect format."
	                                    					+ "\nThe line following area specification should be the roadsLayout "
	                                    					+ "parameter.\nError in areaIdx = " + areaIdx);
	                                    		} 
	                                    		int count = 0;
	                                    		while ((count < m) && ((line=br.readLine()) != null)) {
	                                    			String[] matrixTokens = line.trim().split("\\s+");
	                                    			if (!((matrixTokens[0].equals("#")) || line.isEmpty())) {
	                                    				if (matrixTokens.length != m) {
		                                                    throw new InvalidInputFileException("Error: Incorrect number of row "
		                                                    		+ "elements in roadlayout for areaIdx = " + areaIdx);
	                                    				} else {
	                                    					int index = 0;
		                                                    for (int col = 0; col < m; col++) {
		                                                    	canParse = tryParseShort(matrixTokens[index]);
		                                                    	if (!canParse)		throw new InvalidInputFileException("Element type mismatch in roadLayout matrix for areaIdx = "+ areaIdx);
		                                                        roadLayout[count][col] = Short.parseShort(matrixTokens[index]);                                                 
		                                                        index++;
		                                                    }
		                                                    count++;
		                                                }
	                                    			}
	                                    		}
	                                            
	                                            if (count == m) {	// check that sufficient area information has been obtained
	                                                for (int d = 0; d < m; d++) {	// check the matrix is diagonally zeros
	                                                    if (roadLayout[d][d] != 0) {
	                                                        throw new InvalidInputFileException("Error: Roadlayout matrix not diagonally zero "
	                                                        		+ "for areaIdx = " + areaIdx);
	                                                    }
	                                                }
	                                                areaFound = true;
	                                            } else {
	                                                throw new InvalidInputFileException("Error: Incorrect no. of columns for the "
	                                                		+ "roadLayout matrix for areaIdx = " + areaIdx);
	                                            }
	                                    	}
	                                    }
	                                    ServiceArea curr_sa = new ServiceArea(areaIdx,serviceFreq,thresholdVal,noBins,roadLayout);
	                                    serviceAreas.add(curr_sa);	// append new service area to list of service areas 
	                                    if (serviceAreas.size() == noAreas)		serviceAreasFound = true;	//check whether sufficient no. of service areas have been found
									}
								}
							}
						}
						break;
						
					case "serviceFreq":		// experimentation
						if (noAreasFound) {
							System.out.println("Warning: Multiple inputs for noAreas variable. "
									+ "Previously stored area information will be overwritten.");
						} else if (!(tokens[1].equals("experiment"))) {
							throw new InvalidInputFileException("Error: Invalid format: missing keyword \"experiment\" in this line: " + line);
						} else if (tokensLen < 3) {
							// if missing input for experimentation
							// continue to parse the files
							System.out.println("Warning: Missing input in this line: " + line);
						} else {
							if (tokensLen == 3)		System.out.println("Warning: Only one input has been found for serviceFreq experiment.");
							for (int i = 2; i < tokensLen; i++) {
								canParse = tryParseFloat(tokens[i]);
								if (!canParse)		throw new InvalidInputFileException("Error: serviceFreq input type mismatch at line = "+line);
								float serviceFreq = Float.parseFloat(tokens[i]);
								if (serviceFreq < 0)	throw new InvalidInputFileException("Error: serviceFreq cannot be negative.");
								serviceFreqExp.add(serviceFreq);
							}
							isExperiment = true;
							while ((!noAreasFound) && ((line = br.readLine()) != null)) {
								if (!(line.startsWith("#") || line.isEmpty())) {
									String[] noAreasTokens = line.split("\\s+");
									if (!(noAreasTokens[0].equals("noAreas"))) {
										// TODO write this better
										throw new InvalidInputFileException("\nError: Invalid format."
												+ "\nThe noArea input should be in the line following serviceFreq experiment.");
									} else {
										if (noAreasTokens.length > 2) 	System.out.println("Warning: too many inputs for noAreas.");
										canParse = tryParseShort(noAreasTokens[1]);
										if (!canParse)		throw new InvalidInputFileException("Error: noAreas input type mismatch at line = "+line);
										noAreas = Short.parseShort(noAreasTokens[1]);
										if (noAreas == 0)	System.out.println("Warning: no input for noAreas variable.");
										if (noAreas < 0)	throw new InvalidInputFileException("Error: noAreas cannot be negative.");
										noAreasFound = true;
									}
								}
							}

							while ((!serviceAreasFound) && (line = br.readLine()) != null) {
								if (!(line.startsWith("#") || line.isEmpty())) {
									String[] areaTokens = line.split("\\s+");
									if (!areaTokens[0].equals("areaIdx")) {
										throw new InvalidInputFileException("\nError: Invalid format."
												+ "\nThe line following noAreas variable should be in the format:"
												+ "\nareaIdx <uint8_t> serviceFreq <float> thresholdVal <float> noBins <uint16_t>");
									} else if (!((areaTokens.length == 8) && (areaTokens[2].equals("serviceFreq")) &&
	                                        (areaTokens[4].equals("thresholdVal")) && (areaTokens[6].equals("noBins")))) {
										throw new InvalidInputFileException("Invalid format for service area description line: " + line);
									} else {
										canParse = tryParseShort(areaTokens[1]);
										if (!canParse)		throw new InvalidInputFileException("Error: areaIdx input type mismatch at line = "+line);
										short areaIdx = Short.parseShort(areaTokens[1]);
										if (areaIdx < 0)	throw new InvalidInputFileException("Error: areaIdx cannot be negative.");
										
										canParse = tryParseFloat(areaTokens[3]);
										if (!canParse)		throw new InvalidInputFileException("Error: serviceFreq input type mismatch at line = "+line);
										float serviceFreq = Float.parseFloat(areaTokens[3]);
	                                    if (serviceFreq < 0)	throw new InvalidInputFileException("Error: serviceFreq cannot be negative.");
	                                    
										canParse = tryParseFloat(areaTokens[5]);
										if (!canParse)		throw new InvalidInputFileException("Error: thresholdVal input type mismatch at line = "+line);
	                                    float thresholdVal = Float.parseFloat(areaTokens[5]);
	                                    if (thresholdVal < 0)	throw new InvalidInputFileException("Error: thresholdVal cannot be negative.");
	                                   
										canParse = tryParseInt(areaTokens[7]);
										if (!canParse)		throw new InvalidInputFileException("Error: noBins input type mismatch at line = "+line);
	                                    int noBins = Integer.parseInt(areaTokens[7]);
	                                    if (noBins < 0)	throw new InvalidInputFileException("Error: noBins cannot be negative.");
	                                    if (noBins > 65535) {
	                                    	throw new InvalidInputFileException("Error: noBins exceeds maximum 65,535.");
	                                    }
	                                    int m = noBins + 1; 
	                                    short[][] roadLayout = new short[m][m];
	                                    boolean areaFound = false;

	                                    while ((!areaFound) && (line = br.readLine()) != null) {
	                                    	if (!(line.startsWith("#") || line.isEmpty())) {
	                                    		String[] layoutTokens = line.split("\\s+");
	                                    		if (!(layoutTokens[0].equals("roadsLayout"))) {
	                                    			throw new InvalidInputFileException("Error: Incorrect format."
	                                    					+ "\nThe line following area specification should be the roadsLayout "
	                                    					+ "parameter.\nError in areaIdx = " + areaIdx);
	                                    		} 
	                                    		int count = 0;
	                                    		while ((count < m) && ((line=br.readLine()) != null)) {
	                                    			String[] matrixTokens = line.trim().split("\\s+");
	                                    			if (!((matrixTokens[0].equals("#")) || line.isEmpty())) {
	                                    				if (matrixTokens.length != m) {
		                                                    throw new InvalidInputFileException("Incorrect number of row elements in roadlayout "
		                                                    		+ "for areaIdx = " + areaIdx);
	                                    				} else {
	                                    					int index = 0;
		                                                    for (int col = 0; col < m; col++) {
		                                                    	canParse = tryParseShort(matrixTokens[index]);
		                                                    	if (!canParse)		throw new InvalidInputFileException("Element type mismatch in roadLayout matrix for areaIdx = "+ areaIdx);
		                                                        roadLayout[count][col] = Short.parseShort(matrixTokens[index]);                                                 
		                                                        index++;
		                                                    }
		                                                    count++;
		                                                }
	                                    			}
	                                    		}

	                                            if (count == m) {	// check that valid and sufficient area information has been obtained 
	        	                                    for (int d = 0; d < m; d++) {	// check the matrix is diagonally zeros
	        	                                        if (roadLayout[d][d] != 0) {
	        	                                            throw new InvalidInputFileException("Error: Roadlayout matrix not diagonally zero "
	        	                                            		+ "for areaIdx = " + areaIdx);
	        	                                        }
	        	                                    }
	                                                areaFound = true;
	                                            } else {
	                                                throw new InvalidInputFileException("Error: Incorrect no. of columns for the roadLayout matrix "
	                                                		+ "for areaIdx = " + areaIdx + ".");
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
							System.out.println("Warning: lorryVolume variable has already been found. Disregarding this input.");
						} else if (tokensLen < 2) {
							System.out.println("Warning: Missing input in this line: " + line);
						} else {
							if (tokensLen > 2)		System.out.println("Warning: too many inputs for lorryVolume variable.");
							canParse = tryParseShort(tokens[1]);
							if (!canParse)			throw new InvalidInputFileException("Error: lorryVolume input type mismatch at line = "+line);
							lorryVolume = Short.parseShort(tokens[1]);
							if (lorryVolume < 0)	throw new InvalidInputFileException("Error: lorryVolume cannot be negative.");
							if (lorryVolume == 0)	System.out.println("Warning: The lorryVolume variable is zero.");
							lorryVolumeFound = true;
						}  
						break;
					
					case "lorryMaxLoad":
						if (lorryMaxLoadFound) {
							System.out.println("Warning: lorryMaxLoad variable has already been found. Disregarding this input.");
						} else if (tokensLen < 2) {
							System.out.println("Warning: Missing input in this line: " + line);
						} else {
							if (tokensLen > 2)		System.out.println("Warning: too many inputs for lorryMaxLoad variable.");
							canParse = tryParseInt(tokens[1]);
							if (!canParse)			throw new InvalidInputFileException("Error: lorryMaxLoad input type mismatch at line = "+line);
							lorryMaxLoad = Integer.parseInt(tokens[1]);
							if (lorryMaxLoad < 0)	throw new InvalidInputFileException("Error: lorryMaxLoad cannot be negative.");
							if (lorryMaxLoad == 0)	System.out.println("Warning: The lorryMaxLoad variable is zero.");
							lorryMaxLoadFound = true;
						}
						break;
					
					case "binServiceTime":
						if (binServiceTimeFound) {
							System.out.println("Warning: binServiceTime variable has already been found. Disregarding this input.");
						} else if (tokensLen < 2) {
							System.out.println("Warning: Missing input in this line: " + line);
						} else {
							if (tokensLen > 2)		System.out.println("Warning: too many inputs for lorryMaxLoad variable.");
							canParse = tryParseInt(tokens[1]);
							if (!canParse)			throw new InvalidInputFileException("Error: binServiceTime input type mismatch at line = "+line);
							binServiceTime = Integer.parseInt(tokens[1]);
							if (binServiceTime < 0)		throw new InvalidInputFileException("Error: binServiceTime cannot be negative.");
							if (binServiceTime == 0)	System.out.println("Warning: The binServiceTime variable is zero.");
							binServiceTimeFound = true;
						}
						break;
	
					case "binVolume":
						if (binVolumeFound) {
							System.out.println("Warning: binVolume variable has already been found. Disregarding this input.");
						} else if (tokensLen < 2) {
							System.out.println("Warning: Missing input in this line: " + line);
						} else {
							if (tokensLen > 2)		System.out.println("Warning: too many inputs for binVolume variable");
							canParse = tryParseFloat(tokens[1]);
							if (!canParse)			throw new InvalidInputFileException("Error: binVolume input type mismatch at line = "+line);
							binVolume = Float.parseFloat(tokens[1]);
							if (binVolume < 0)		throw new InvalidInputFileException("Error: binVolume cannot be negative.");
							if (binVolume == 0)		System.out.println("Warning: The binVolume variable is zero.");
							binVolumeFound = true;	
						}				
						break;
					
					case "disposalDistrRate":
						if (disposalDistrRateFound) {
							System.out.println("Warning: disposalDistrRate variable has already been found. Disregarding this input.");
						} else if (tokensLen < 2) {
							System.out.println("Warning: Missing input in this line: " + line);
						} else if (tokensLen >= 2) {
							if (tokens[1].equals("experiment")) {	// experimentation
								if (tokensLen <= 3) {
									System.out.println("Warning: Missing input in this line: " + line);
								} else {
									for (int i = 2; i < tokensLen; i++) {
										canParse = tryParseFloat(tokens[i]);
										if (!canParse)			throw new InvalidInputFileException("Error: disposalDistrRate input type mismatch at line = "+line);
										float ddr = Float.parseFloat(tokens[i]);
										if (ddr < 0)	throw new InvalidInputFileException("Error: disposalDistrRate cannot be negative.");
										if (ddr == 0)		System.out.println("Warning: The disposalDistrRate variable is zero.");
										disposalDistrRateExp.add(ddr);
										isExperiment = true;
									}
									disposalDistrRateFound = true;
								}
							} else {	// not experimentation
								if (tokensLen > 2)		System.out.println("Warning: too many inputs for disposalDistrRate variable");
								canParse = tryParseFloat(tokens[1]);
								if (!canParse)			throw new InvalidInputFileException("Error: disposalDistrRate input type mismatch at line = "+line);
								disposalDistrRate = Float.parseFloat(tokens[1]);
								if (disposalDistrRate < 0)		throw new InvalidInputFileException("Error: disposalDistrRate cannot be negative.");
								if (disposalDistrRate == 0)		System.out.println("Warning: The disposalDistrRate variable is zero.");
								disposalDistrRateFound = true;
							}
						}
						break;
					
					case "disposalDistrShape":
						if (disposalDistrShapeFound) {
							System.out.println("Warning: disposalDistrShape variable has already been found. Disregarding this input.");
						} else if (tokensLen < 2) {
							System.out.println("Warning: Missing input in this line: " + line);
						} else if (tokensLen >= 2) {
							if (tokens[1].equals("experiment")) {	// experimentation
								if (tokensLen <= 3) {
									System.out.println("Warning: Missing input in this line: " + line);
								} else {
									for (int i = 2; i < tokensLen; i++) {
										canParse = tryParseShort(tokens[i]);
										if (!canParse)			throw new InvalidInputFileException("Error: disposalDistrShape input type mismatch at line = "+line);
										short dds = Short.parseShort(tokens[i]);
										if (dds < 0)	throw new InvalidInputFileException("Error: disposalDistrShape cannot be negative.");
										if (dds == 0)		System.out.println("Warning: The disposalDistrShape variable is zero.");
										disposalDistrShapeExp.add(dds);
										isExperiment = true;
									}
									disposalDistrShapeFound = true;
								}
							} else {	// not experimentation
								if (tokensLen > 2)		System.out.println("Warning: too many inputs for disposalDistrShape variable");
								canParse = tryParseShort(tokens[1]);
								if (!canParse)			throw new InvalidInputFileException("Error: disposalDistrShape input type mismatch at line = "+line);
								disposalDistrShape = Short.parseShort(tokens[1]);
								if (disposalDistrShape < 0)		throw new InvalidInputFileException("Error: disposalDistrShape cannot be negative.");
								if (disposalDistrShape == 0)		System.out.println("Warning: The disposalDistrShape variable is zero.");
								disposalDistrShapeFound = true;
							}
						}  
						break;
					
					case "bagVolume":
						if (bagVolumeFound) {
							System.out.println("Warning: bagVolume variable has already been found. Disregarding this input.");
						}  else if (tokensLen < 2) {
							System.out.println("Warning: Missing input in this line: " + line);
						} else {
							if (tokensLen > 2)		System.out.println("Warning: too many inputs for bagVolume variable");
							canParse = tryParseFloat(tokens[1]);
							if (!canParse)			throw new InvalidInputFileException("Error: bagVolume input type mismatch at line = "+line);
							bagVolume = Float.parseFloat(tokens[1]);
							if (bagVolume < 0)		throw new InvalidInputFileException("Error: bagVolume cannot be negative.");
							if (bagVolume == 0)		System.out.println("Warning: The bagVolume variable is zero.");
							bagVolumeFound = true;
						}  
						break;
					
					case "bagWeightMin":
						if (bagWeightMinFound) {
							System.out.println("Warning: bagWeightMin variable has already been found. Disregarding this input.");
						} else if (tokensLen < 2) {
							System.out.println("Warning: Missing input in this line: " + line);
						} else {
							if (tokensLen > 2)		System.out.println("Warning: too many inputs for bagWeightMin variable");
							canParse = tryParseFloat(tokens[1]);
							if (!canParse)			throw new InvalidInputFileException("Error: bagWeightMin input type mismatch at line = "+line);
							bagWeightMin = Float.parseFloat(tokens[1]);
							if (bagWeightMin < 0)		throw new InvalidInputFileException("Error: bagWeightMin cannot be negative.");
							if (bagWeightMin == 0)		System.out.println("Warning: The bagWeightMin variable is zero.");
							bagWeightMinFound = true;
						}  
						break;
					
					case "bagWeightMax":
						if (bagWeightMaxFound) {
							System.out.println("Warning: bagWeightMax variable has already been found. Disregarding this input.");
						} else if (tokensLen < 2) {
							System.out.println("Warning: Missing input in this line: " + line);
						} else {
							if (tokensLen > 2)		System.out.println("Warning: too many inputs for bagWeightMax variable");
							canParse = tryParseFloat(tokens[1]);
							if (!canParse)			throw new InvalidInputFileException("Error: bagWeightMax input type mismatch at line = "+line);
							bagWeightMax = Float.parseFloat(tokens[1]);
							if (bagWeightMax < 0)		throw new InvalidInputFileException("Error: bagWeightMax cannot be negative.");
							if (bagWeightMax == 0)		System.out.println("Warning: The bagWeightMax variable is zero.");
							bagWeightMaxFound = true;
						}  
						break;
					
					case "stopTime":
						if (stopTimeFound) {
							System.out.println("Warning: stopTimeFound variable has already been found. Disregarding this input.");
						} else if (tokensLen < 2) {
							System.out.println("Warning: Missing input in this line: " + line);
						} else {
							if (tokensLen > 2)		System.out.println("Warning: too many inputs for stopTime variable");
							canParse = tryParseFloat(tokens[1]);
							if (!canParse)			throw new InvalidInputFileException("Error: stopTime input type mismatch at line = "+line);
							stopTime = Float.parseFloat(tokens[1]);
							if (stopTime < 0)		throw new InvalidInputFileException("Error: stopTime cannot be negative.");
							if (stopTime == 0)		System.out.println("Warning: The stopTime variable is zero.");
							stopTimeFound = true;
						}  
						break;
					
					case "warmUpTime":
						if (warmUpTimeFound) {
							System.out.println("Warning: warmUpTime variable has already been found. Disregarding this input.");
						} else if (tokensLen < 2) {
							System.out.println("Warning: Missing input in this line: " + line);
							// TODO add a line saying we will keep searching for the input???
						} else {
							if (tokensLen > 2)		System.out.println("Warning: too many inputs for warmUpTime variable");
							canParse = tryParseFloat(tokens[1]);
							if (!canParse)			throw new InvalidInputFileException("Error: warmUpTime input type mismatch at line = "+line);
							warmUpTime = Float.parseFloat(tokens[1]);
							if (warmUpTime < 0)		throw new InvalidInputFileException("Error: warmUpTime cannot be negative.");
							if (warmUpTime == 0)		System.out.println("Warning: The warmUpTime variable is zero.");
							warmUpTimeFound = true;
						}
						break;
						
					default: System.out.println("Warning: Unrecognised input parameter: '" + tokens[0] + "'. This line will be ignored.");
					}
				}
			}
			br.close();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			System.out.println("Error: Input file not found. Program will terminate");
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	
	/**
	 * identifies missing inputs and terminate program if missing
	 * if all found, checks if all inputs make sense
	 */
	public void validation() throws InvalidInputFileException {
		// System.out.println("Validating input paramters:");
		boolean isMissing = false;
		if (!lorryVolumeFound) {
			System.out.println("Error: Missing parameter lorryVolume."); isMissing = true;
		}
		if (!lorryMaxLoadFound)	{
			System.out.println("Error: Missing parameter lorryMaxLoad."); isMissing = true;
		}
		if (!binServiceTimeFound) {
			System.out.println("Error: Missing parameter binServiceTime."); isMissing = true;
		}
		if (!binVolumeFound) {
			System.out.println("Error: Missing parameter binVolume."); isMissing = true;
		}
		if (!disposalDistrRateFound) {
			System.out.println("Error: Missing parameter disposalDistrRate."); isMissing = true;
		}
		if (!disposalDistrShapeFound) {
			System.out.println("Error: Missing parameter disposalDistrShape."); isMissing = true;
		}
		if (!bagVolumeFound) {
			System.out.println("Error: Missing parameter bagVolume."); isMissing = true;
		}
		if (!bagWeightMinFound) {
			System.out.println("Error: Missing parameter bagWeightMin."); isMissing = true;
		}
		if (!bagWeightMaxFound)	{
			System.out.println("Error: Missing parameter bagWeightMax."); isMissing = true;
		}
		if (!noAreasFound) {
			System.out.println("Error: Missing parameter noAreas."); isMissing = true;
		}
		if (!serviceAreasFound) {
			System.out.println("Error: Insufficient or missing service areas."); 
			System.out.println("Number of service areas: " + noAreas);
			System.out.println("Number of service area specifications found: " + serviceAreas.size());
			isMissing = true;
		}
		if (!stopTimeFound)	{
			System.out.println("Error: Missing parameter stopTime."); isMissing = true;
		}
		if (!warmUpTimeFound) {
			System.out.println("Error: Missing parameter warmUpTime."); isMissing = true;
		}
		if (isMissing) { // terminates program by throwing an exception
			throw new InvalidInputFileException("Error: Missing one or more parameters.");
		} else {
			// check if parameters make sense
			if (stopTime < warmUpTime) {
				System.out.println("Warning: 'stopTime' parameter smaller than 'warmUpTime'. The simulation will continue.");
			}
			if (bagWeightMin > bagWeightMax) {
				System.out.println("Warning: 'bagWeightMax' parameter smaller than 'bagWeightMin'. The simulation will continue.");
			}
			if (bagWeightMax > lorryMaxLoad) {
				System.out.println("Warning: 'lorryMaxLoad' parameter smaller than 'bagWeightMax'. The simulation will continue.");
			}
			if (bagVolume > binVolume) {
				System.out.println("Warning: 'binVolume' parameter smaller than 'bagVolume'. The simulation will continue.");
			}
			if ((binVolume/2) > lorryVolume) {	// lorry compresses bin volume by half its original value
				System.out.println("Warning: 'lorryVolume' parameter smaller than 'binVolume'. The simulation will continue.");
			}
			for (ServiceArea sa : serviceAreas) {
				float serviceFreq = sa.getServiceFreq();
				short areaIdx = sa.getAreaIdx();
				// TODO convert disposalDistrRate from hour to second!!!!
				if (disposalDistrRate > serviceFreq) {
					System.out.println("Warning: 'disposalDistrRate' parameter bigger than 'serviceFreq' parameter for service area with areaIdx = "+areaIdx);
				}
			}
			System.out.println("All parameters found. Simulation continues");
		}
	}
	
	/*
	public void printAllInputs() {
		// print all inputs
		System.out.println("lorryVolume = "+lorryVolume);
		System.out.println("lorryMaxLoad = "+lorryMaxLoad);
		System.out.println("binServiceTime = "+binServiceTime);
		System.out.println("binVolume = "+binVolume);
		System.out.println("disposalDistrRate = "+disposalDistrRate);
		System.out.println("disposalDistrShape = "+disposalDistrShape);
		System.out.println("bagVolume = "+bagVolume);
		System.out.println("bagWeightMin = "+bagWeightMin);
		System.out.println("bagWeightMax = "+bagWeightMax);
		System.out.println("noAreas = "+noAreas);
		System.out.println("Service Areas information: ");
		for (ServiceArea sa : serviceAreas) System.out.println(sa.toString());
		System.out.println("stopTime = "+stopTime);
		System.out.println("warmUpTime = "+warmUpTime);
		// check whether it is an experimentation input file
		System.out.println("isExperiment = "+isExperiment);
		System.out.println("All disposalDistrRate:");
		for (Float sa : disposalDistrRateExp) System.out.println("disposalDistrRateExp: " + sa);
		System.out.println("All disposalDistrShape:");
		for (Short sa : disposalDistrShapeExp) System.out.println("disposalDistrShapeExp: " + sa);
		System.out.println("All serviceFreq:");
		for (Float sa : serviceFreqExp) System.out.println("serviceFreqExp: " + sa);
	}*/
	
	// Simulator implementation
	PriorityQueue<AbstractEvent> events = new PriorityQueue<AbstractEvent>(); // for storing upcoming events
	int time;	// current simulation time
	int now() {
		return time;
	}
	public void doAllEvents() {		// extracts all events from 'events' queue and execute them
		AbstractEvent e;
        while ((e = (AbstractEvent) events.poll()) != null) {
            time = e.getTime();
            e.execute(this);
        }
    }
	public void insert(AbstractEvent e) {	// insert event into queue
		this.events.add(e);
	}
	
	/** 
	 * assign a lorry to each service areas
	 * create bins in each service areas
	 */
	public void initialiseCity() {
		for (ServiceArea sa : serviceAreas) {
			sa.setLorry(new Lorry());
			sa.setBins();
		}
	}
	
	/**
	 * Starts simulator
	 * If this is an experiment, several simulations will be run with different parameters.
	 */
	public void start() {
		if (isExperiment) {
			// at the moment, only one set of parameters will be run
			// because no implementation for experiments yet
			// run experiments
			System.out.println("This is an experiment and will be run differently.");
			System.out.println("No support for experimentation yet.");
			System.out.println("Run program with only one input per parameter.");
			if (disposalDistrRateExp.size() > 0) {
				disposalDistrRate = disposalDistrRateExp.get(0);
			}
			if (disposalDistrShapeExp.size() > 0) {
				disposalDistrShape = disposalDistrShapeExp.get(0);
			}
			System.out.println("Simulation starts:");
			
			// set attributes again
			Random.setDisposalDistrRate(disposalDistrRate);
			Random.setDisposalDistrShape(disposalDistrShape);
			
			for (ServiceArea sa : serviceAreas) {	// generate disposal events for each bin
				for (Bin bin : sa.getBins()) {
					DisposalEvent disposalEventGenerator = new DisposalEvent(Random.erlangk(), bin);
					this.events.add(disposalEventGenerator);
				}
			}
			doAllEvents();	// execute all events from priority queue
		} else {
			System.out.println("Simulation starts:");

			for (ServiceArea sa : serviceAreas) {	// generate disposal events for each bin
				for (Bin bin : sa.getBins()) {
					DisposalEvent disposalEventGenerator = new DisposalEvent(Random.erlangk(), bin);
					this.events.add(disposalEventGenerator);
				}
			}
			doAllEvents();	// execute all events from priority queue
		}
	}
	
	/**
	 * Last stage of the simulation is statistical analysis
	 */
	public void statsAnalysis() {
		System.out.println("Simulation finishs. Statisitic Analysis starts:");
		System.out.println("No implementation yet");
	}
	public static void main(String[] args) throws FileNotFoundException, InvalidInputFileException {
		String file_path = args[0];
		Simulator citySimulator = new Simulator();
		
		citySimulator.parseInputs(file_path);	// parse inputs
		
		citySimulator.validation();		// check that all inputs exist

		//citySimulator.printAllInputs();
		
		// initialize all variables
		Bag.setBagVolume(bagVolume);
		Bag.setBagWeightMax(bagWeightMax);
		Bag.setBagWeightMin(bagWeightMin);
		Bin.setBinVolume(binVolume); 
		Random.setDisposalDistrRate(disposalDistrRate);
		Random.setDisposalDistrShape(disposalDistrShape);	
		Lorry.setBinServiceTime(binServiceTime);
		Lorry.setLorryMaxLoad(lorryMaxLoad);
		Lorry.setLorryVolume(lorryVolume);
		AbstractEvent.setStopTime(stopTime);
		
		citySimulator.initialiseCity();		// set up all service areas
		
		// should maybe implement this into two parts for experiments and non experiment
		citySimulator.start();
		citySimulator.statsAnalysis();
		
    } 	
}