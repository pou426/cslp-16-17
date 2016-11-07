package cslp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.PriorityQueue;

/**
 * Simulator class performs the following:
 * 1. input parsing (identify experiment keywords)
 * 2. input validation
 * 3. initialize all parameters and create a Simulator instance
 * 4. run simulator
 * 5. collect statistics and run analysis 
 *
 */
public class Simulator {
		
	// change input parameters to instance variable
	// change experimental inputs to class variable
	// ?????
	
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
	private static HashMap<Short,ServiceArea> serviceAreas = new HashMap<Short,ServiceArea>();
	//private static ArrayList<ServiceArea> serviceAreas = new ArrayList<ServiceArea>(); // serviceFreq experessed as x trips per hour
	private static float stopTime;
	private static float warmUpTime;
	
	// Arraylists for storing experiment input parameters
	private static boolean isExperiment = false; // indicator for experiment inputs
	private static ArrayList<Float> disposalDistrRateExp = new ArrayList<Float>();
	private static ArrayList<Short> disposalDistrShapeExp = new ArrayList<Short>();
	private static ArrayList<Float> serviceFreqExp = new ArrayList<Float>();
	
	// check that all input parameters present
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
	
	/** 
	 * methods to catch exceptions when parsing byte/int/short/float strings from file
	 * Returns boolean for the program to handle
	 * methods are called whenever the input parameter is supposed to be an int/short/float/byte
	 */
	// optimize this into one for all int/float/short
	// and check for maximum value and non negative and zero value
	// pass int/float/short and the name of parameter as value?! 
	// print error message and exit program?! 
	// pass a boolean for can be negative values??? like roadsLayout matrix?!
	// use uint8 uint16 int8..????? 
	private static boolean tryParseByte(String value) {
		try {
			Byte.parseByte(value);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}
	private static boolean tryParseInt(String value) {		
	     try {  
	         Integer.parseInt(value);  
	         return true;  
	      } catch (NumberFormatException e) {  
	         return false;  
	      } 
	}
	private static boolean tryParseShort(String value) {  
	     try {  
	         Short.parseShort(value);  
	         return true;  
	      } catch (NumberFormatException e) {  
	         return false;  
	      }  
	}
	private static boolean tryParseFloat(String value) {  
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
	public static void parseInputs(String file_path) throws FileNotFoundException {	
		try {
			File file = new File(file_path);
			FileReader fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);
			String line;
			
			while ((line = br.readLine())!=null) {
				if (!(line.startsWith("#") || line.isEmpty())) { // ignore comments and blank lines
					String[] tokens = line.split("\\s+");
					int tokensLen = tokens.length;
					
					boolean canParse;		// boolean for int/byte/short/float inputs
					switch (tokens[0]) {	// check the first word in each line for input parameter
					case "noAreas":
						if (tokensLen < 2) {
							System.out.println("Warning: Missing input for 'noArea' parameter.");
						} else { 
							if (noAreasFound)		System.out.println("Warning: 'noAreas' parameter has already been found. Overwritting the previous input.");
							noAreasFound = false;	// reset relevant parameters
							serviceAreasFound = false;
							serviceAreas.clear();
							if (tokensLen > 2)		System.out.println("Warning: Too many inputs for 'noAreas' parameter. Only the first input will be parsed.");
							canParse = tryParseShort(tokens[1]);
							if (!canParse)			Error.throwError("Error: 'noAreas' input type mismatch at line = "+line); 
							noAreas = Short.parseShort(tokens[1]);
							if (noAreas < 0)		Error.throwError("Error: 'noAreas' cannot be negative.");
							if (noAreas == 0) {
								System.out.println("Warning: Input for 'noAreas' parameter is 0.");
								serviceAreasFound = true;
							}
							if (noAreas > 255)		Error.throwError("Error: 'noAreas' input exceeds maximum value 255.");
							noAreasFound = true;
							
							while ((!serviceAreasFound) && (line = br.readLine()) != null) {	// this block looks for service area specifications
								if (!(line.startsWith("#") || line.isEmpty())) {
									String[] areaTokens = line.split("\\s+");
									if (!areaTokens[0].equals("areaIdx")) {
										int totalServiceAreasFound = serviceAreas.size();
										if (totalServiceAreasFound == 0) {
											Error.throwError("Error: Invalid format in the line following the noArea paramter in line: "+line);
										} else if (totalServiceAreasFound < noAreas) {
											Error.throwError("Error: Expecting more service area information. Invalid format in line: "+line);
										}										
									} else if (!((areaTokens.length >= 8) && (areaTokens[2].equals("serviceFreq")) &&
	                                        (areaTokens[4].equals("thresholdVal")) && (areaTokens[6].equals("noBins")))) {
										Error.throwError("Error: Invalid format for service area description line: " + line);
									} else {
										if (areaTokens.length > 8)		System.out.println("Warning: too many inputs in this line: " + line);
										
										canParse = tryParseShort(areaTokens[1]);	// areaIdx
										if (!canParse)			Error.throwError("Error: 'areaIdx' input type mismatch at line = "+line);
										short areaIdx = Short.parseShort(areaTokens[1]);
										if (areaIdx < 0)	Error.throwError("Error: 'areaIdx' cannot be negative.");
	                                    if (areaIdx > 255)	Error.throwError("Error: 'areaIdx' input exceeds maximum value 255.");
	                                    
										canParse = tryParseFloat(areaTokens[3]);	// serviceFreq
										if (!canParse)			Error.throwError("Error: 'serviceFreq' input type mismatch at line = "+line);
										float serviceFreq = Float.parseFloat(areaTokens[3]);
	                                    if (serviceFreq < 0)	Error.throwError("Error: 'serviceFreq' cannot be negative.");
	                                    if (serviceFreq == 0)	System.out.println("Warning: Input for 'serviceFreq' is 0 for areaIdx = "+areaIdx);
	                                    
	                                    canParse = tryParseFloat(areaTokens[5]);	// thresholdVal
	                                    if (!canParse)			Error.throwError("Error: 'thresholdVal' input type mismatch at line = "+line);
	                                    float thresholdVal = Float.parseFloat(areaTokens[5]);
	                                    if (thresholdVal < 0)	Error.throwError("Error: 'thresholdVal' cannot be negative.");
	                                    if (thresholdVal == 0)	System.out.println("Warning: Input for 'thresholdVal' is 0 for areaIdx = "+areaIdx);
	                                    if (thresholdVal >= 1)	System.out.println("Warning: 'thresholdVal' is greater than or equal to 1 for areaIdx = "+areaIdx);
	                                    
	                                    canParse = tryParseInt(areaTokens[7]);		// noBins
	                                    if (!canParse)			Error.throwError("Error: 'noBins' input type mismatch at line = "+line);
	                                    int noBins = Integer.parseInt(areaTokens[7]);
	                                    if (noBins < 0)			Error.throwError("Error: 'noBins' cannot be negative.");
	                                    if (noBins > 65535) 	Error.throwError("Error: 'noBins' exceeds maximum value 65,535.");
	                                    if (noBins == 0)		System.out.println("Warning: Input for 'noBins' parameter is 0 for areaIdx = "+areaIdx);
	                                    
	                                    int m = noBins + 1; 
	                                    byte[][] roadsLayout = new byte[m][m];
	                                    boolean areaFound = false;
	                                    
	                                    while ((!areaFound) && (line = br.readLine()) != null) {	// this block looks for roadsLayout matrix
	                                    	if (!(line.startsWith("#") || line.isEmpty())) {
	                                    		String[] layoutTokens = line.split("\\s+");
	                                    		if (!(layoutTokens[0].equals("roadsLayout"))) {
	                                    			Error.throwError("Error: Incorrect format."
	                                    					+ "\nThe line following area specification should be the roadsLayout "
	                                    					+ "parameter.\nError in areaIdx = " + areaIdx);
	                                    		} 
	                                    		int count = 0;
	                                    		while ((count < m) && ((line=br.readLine()) != null)) {
	                                    			String[] matrixTokens = line.trim().split("\\s+");
	                                    			if (!((matrixTokens[0].equals("#")) || line.isEmpty())) {
	                                    				if (matrixTokens.length != m) {
		                                                    Error.throwError("Error: Incorrect number of row "
		                                                    		+ "elements in roadsLayout for areaIdx = " + areaIdx);
	                                    				} else {
	                                    					int index = 0;
		                                                    for (int col = 0; col < m; col++) {
		                                                    	canParse = tryParseByte(matrixTokens[index]);
		                                                    	if (!canParse)		Error.throwError("Error: Element type mismatch in roadsLayout matrix for areaIdx = "+areaIdx);
		                                                        byte element = Byte.parseByte(matrixTokens[index]);
		                                                    	roadsLayout[count][col] = element;
		                                                        index++;
		                                                    }
		                                                    count++;
		                                                }
	                                    			}
	                                    		}
	                                            
	                                            if (count == m) {	// check that sufficient area information has been obtained
	                                            	boolean isDiagonallyZero = true;
	                                                for (int d = 0; d < m; d++) {	// check the matrix is diagonally zeros
	                                                    if (roadsLayout[d][d] != 0) {
	                                                    	isDiagonallyZero = false;
	                                                    }
	                                                }
	                                                if (!isDiagonallyZero)		System.out.println("Warning: 'roadsLayout' matrix is not diagonally 0 for areaIdx = "+areaIdx);
	                                                areaFound = true;
	                                            } else {
	                                                Error.throwError("Error: Incorrect no. of columns for the "
	                                                		+ "roadsLayout matrix for areaIdx = " + areaIdx);
	                                            }
	                                    	}
	                                    }
	                                    // if service area with the specific areaIdx already exists, overwrite.
	                                    if (serviceAreas.get(areaIdx) != null) {
	                                    	System.out.println("Warning: Service area information for areaIdx = "+areaIdx+" has been found. Overwritting the previously stored service area information.");
	                                    }
	                                    ServiceArea curr_sa = new ServiceArea(areaIdx,serviceFreq,thresholdVal,noBins,roadsLayout);
	                                    serviceAreas.put(areaIdx, curr_sa);	// append new service area to list of service areas 
	                                    if (serviceAreas.size() == noAreas)		serviceAreasFound = true;	//check whether sufficient no. of service areas have been found
									}
								}
							}
						}
						break;
						
					case "serviceFreq":		// experimentation
						if (!(tokens[1].equals("experiment"))) {
							Error.throwError("Error: Invalid format: missing keyword 'experiment' in this line: " + line);
						} if (tokensLen == 2) {
							System.out.println("Warning: Missing input in this line: "+line+"\nThis line will be disregarded.");
						} else {
							if (noAreasFound)	System.out.println("Warning: Multiple inputs for 'noAreas' parameter. Previously stored area information will be overwritten.");
							noAreasFound = false;		// reset relevant parameters
							serviceAreasFound = false;
							serviceAreas.clear();
							for (int i = 2; i < tokensLen; i++) {
								canParse = tryParseFloat(tokens[i]);
								if (!canParse)		Error.throwError("Error: 'serviceFreq' input type mismatch at line = "+line);
								float serviceFreq = Float.parseFloat(tokens[i]);
								if (serviceFreq < 0)	Error.throwError("Error: 'serviceFreq' cannot be negative.");
								if (serviceFreq == 0)	System.out.println("Warning: Input for 'serviceFreq' experimentation parameter equals to 0.");
								serviceFreqExp.add(serviceFreq);
							}
							if (serviceFreqExp.size() == 1) 	System.out.println("Warning: Only one experimentation input for 'serviceFreq'.");
							isExperiment = true;
							while ((!noAreasFound) && ((line = br.readLine()) != null)) {
								if (!(line.startsWith("#") || line.isEmpty())) {
									String[] noAreasTokens = line.split("\\s+");
									if (!(noAreasTokens[0].equals("noAreas"))) {
										// TODO write this better
										Error.throwError("\nError: Invalid format."
												+ "\nThe noArea input should be in the line following 'serviceFreq experiment'.");
									} else {
										if (noAreasTokens.length > 2) 	System.out.println("Warning: too many inputs for noAreas.");
										canParse = tryParseShort(noAreasTokens[1]);
										if (!canParse)		Error.throwError("Error: 'noAreas' input type mismatch at line = "+line);
										noAreas = Short.parseShort(noAreasTokens[1]);
										if (noAreas < 0)	Error.throwError("Error: 'noAreas' cannot be negative.");
										if (noAreas == 0) {
											System.out.println("Warning: Input for 'noAreas' parameter is 0.");
											serviceAreasFound = true;
										}
										if (noAreas > 255)	Error.throwError("Error: 'noAreas' input exceeds maximum value 255.");
										noAreasFound = true;
									}
								}
							}

							while ((!serviceAreasFound) && (line = br.readLine()) != null) {
								if (!(line.startsWith("#") || line.isEmpty())) {
									String[] areaTokens = line.split("\\s+");
									if (!areaTokens[0].equals("areaIdx")) {
										int totalServiceAreasFound = serviceAreas.size();
										if (totalServiceAreasFound == 0) {
											Error.throwError("Error: Invalid format in the line following the noArea paramter in line: "+line);
										} else if (totalServiceAreasFound < noAreas) {
											Error.throwError("Error: Expecting more service area information. Invalid format in line: "+line);
										}										
									} else if (!((areaTokens.length >= 8) && (areaTokens[2].equals("serviceFreq")) &&
	                                        (areaTokens[4].equals("thresholdVal")) && (areaTokens[6].equals("noBins")))) {
										Error.throwError("Invalid format for service area description line: " + line);
									} else {
										if (areaTokens.length > 8)		System.out.println("Warning: too many inputs in this line: " + line);
										
										canParse = tryParseShort(areaTokens[1]);	// areaIdx
										if (!canParse)		Error.throwError("Error: 'areaIdx' input type mismatch at line = "+line);
										short areaIdx = Short.parseShort(areaTokens[1]);
										if (areaIdx < 0)	Error.throwError("Error: 'areaIdx' cannot be negative.");
										if (areaIdx > 255) 	Error.throwError("Error: 'areaIdx' input exceeds maximum value 255.");
										
										canParse = tryParseFloat(areaTokens[3]);	// serviceFreq
										if (!canParse)			Error.throwError("Error: 'serviceFreq' input type mismatch at line = "+line);
										float serviceFreq = Float.parseFloat(areaTokens[3]);
	                                    if (serviceFreq < 0)	Error.throwError("Error: 'serviceFreq' cannot be negative.");
	                                    if (serviceFreq == 0) 	System.out.println("Warning: 'serviceFreq' is 0 for areaIdx = "+areaIdx);
	                                    
										canParse = tryParseFloat(areaTokens[5]);	// thresholdVal
										if (!canParse)			Error.throwError("Error: thresholdVal input type mismatch at line = "+line);
	                                    float thresholdVal = Float.parseFloat(areaTokens[5]);
	                                    if (thresholdVal < 0)	Error.throwError("Error: thresholdVal cannot be negative.");
	                                    if (thresholdVal == 0)	System.out.println("Warning: 'thresholdVal' is 0 for areaIdx = "+areaIdx);
	                                    if (thresholdVal >= 1)	System.out.println("Warning: 'thresholdVal' is greater than or qual to 1 for areaIdx = "+areaIdx);
										
	                                    canParse = tryParseInt(areaTokens[7]);		// noBins
										if (!canParse)			Error.throwError("Error: noBins input type mismatch at line = "+line);
	                                    int noBins = Integer.parseInt(areaTokens[7]);
	                                    if (noBins < 0)			Error.throwError("Error: noBins cannot be negative.");
	                                    if (noBins > 65535) 	Error.throwError("Error: noBins exceeds maximum value 65,535.");
	                                    if (noBins == 0) 		System.out.println("Warning: 'noBins' is 0 for areaIdx = "+areaIdx);
	                                    
	                                    int m = noBins + 1; 
	                                    byte[][] roadsLayout = new byte[m][m];
	                                    boolean areaFound = false;

	                                    while ((!areaFound) && (line = br.readLine()) != null) {
	                                    	if (!(line.startsWith("#") || line.isEmpty())) {
	                                    		String[] layoutTokens = line.split("\\s+");
	                                    		if (!(layoutTokens[0].equals("roadsLayout"))) {
	                                    			Error.throwError("Error: Incorrect format."
	                                    					+ "\nThe line following area specification should be the roadsLayout "
	                                    					+ "parameter.\nError in areaIdx = " + areaIdx);
	                                    		} 
	                                    		int count = 0;
	                                    		while ((count < m) && ((line=br.readLine()) != null)) {
	                                    			String[] matrixTokens = line.trim().split("\\s+");
	                                    			if (!((matrixTokens[0].equals("#")) || line.isEmpty())) {
	                                    				if (matrixTokens.length != m) {
		                                                    Error.throwError("Incorrect number of row elements in roadsLayout "
		                                                    		+ "for areaIdx = " + areaIdx);
	                                    				} else {
	                                    					int index = 0;
		                                                    for (int col = 0; col < m; col++) {
		                                                    	canParse = tryParseByte(matrixTokens[index]);
		                                                    	if (!canParse)		Error.throwError("Element type mismatch in roadsLayout matrix for areaIdx = "+ areaIdx);
		                                                        roadsLayout[count][col] = Byte.parseByte(matrixTokens[index]);                                                 
		                                                        index++;
		                                                    }
		                                                    count++;
		                                                }
	                                    			}
	                                    		}

	                                            if (count == m) {	// check that valid and sufficient area information has been obtained 
	        	                                    boolean isDiagonallyZero = true;
	                                            	for (int d = 0; d < m; d++) {	// check the matrix is diagonally zeros
	        	                                        if (roadsLayout[d][d] != 0) {
	        	                                        	isDiagonallyZero = false;
	        	                                        }
	        	                                    }
	                                            	if (!isDiagonallyZero)		System.out.println("Warning: 'roadsLayout' matrix is not diagonally zero for areaIdx = "+areaIdx);
	                                                areaFound = true;
	                                            } else {
	                                                Error.throwError("Error: Incorrect no. of columns for the 'roadsLayout' matrix "
	                                                		+ "for areaIdx = " + areaIdx + ".");
	                                            }
	                                    	}
	                                    }
	                                    // if service area with the specific areaIdx already exists, overwrite.
	                                    if (serviceAreas.get(areaIdx) != null) {
	                                    	System.out.println("Warning: Service area information for areaIdx = "+areaIdx+" has been found. Overwritting the previously stored service area information.");
	                                    }
	                                    ServiceArea curr_sa = new ServiceArea(areaIdx,serviceFreq,thresholdVal,noBins,roadsLayout);
	                                    serviceAreas.put(areaIdx,curr_sa);
	                                    if (serviceAreas.size() == noAreas)		serviceAreasFound = true;
									}
								}
							}
						}
						break;
						
					case "lorryVolume":
						if (lorryVolumeFound) {
							System.out.println("Warning: 'lorryVolume' parameter has already been found. Disregarding this input.");
						} else if (tokensLen < 2) {
							System.out.println("Warning: Missing input in this line: " + line);
						} else {
							if (tokensLen > 2)		System.out.println("Warning: too many inputs for 'lorryVolume' parameter.");
							canParse = tryParseShort(tokens[1]);
							if (!canParse)			Error.throwError("Error: 'lorryVolume' input type mismatch at line = "+line);
							lorryVolume = Short.parseShort(tokens[1]);
							if (lorryVolume < 0)	Error.throwError("Error: 'lorryVolume' parameter cannot be negative.");
							if (lorryVolume == 0)	System.out.println("Warning: 'lorryVolume' parameter is zero.");
							if (lorryVolume > 255)	Error.throwError("Error: 'lorryVolume' input exceeds maximum value 255 at line = "+line);
							lorryVolumeFound = true;
						}  
						break;
					
					case "lorryMaxLoad":
						if (lorryMaxLoadFound) {
							System.out.println("Warning: 'lorryMaxLoad' parameter has already been found. Disregarding this input.");
						} else if (tokensLen < 2) {
							System.out.println("Warning: Missing input in this line: " + line);
						} else {
							if (tokensLen > 2)			System.out.println("Warning: too many inputs for 'lorryMaxLoad' parameter.");
							canParse = tryParseInt(tokens[1]);
							if (!canParse)				Error.throwError("Error: 'lorryMaxLoad' input type mismatch at line = "+line);
							lorryMaxLoad = Integer.parseInt(tokens[1]);
							if (lorryMaxLoad < 0)		Error.throwError("Error: 'lorryMaxLoad' cannot be negative.");
							if (lorryMaxLoad == 0)		System.out.println("Warning: 'lorryMaxLoad' parameter is zero.");
							if (lorryVolume > 65535)	Error.throwError("Error: 'lorryMaxLoad' input exceeds maximum value 65,535 at line = "+line);
							lorryMaxLoadFound = true;
						}
						break;
					
					case "binServiceTime":
						if (binServiceTimeFound) {
							System.out.println("Warning: 'binServiceTime' parameter has already been found. Disregarding this input.");
						} else if (tokensLen < 2) {
							System.out.println("Warning: Missing input in this line: " + line);
						} else {
							if (tokensLen > 2)			System.out.println("Warning: too many inputs for 'binServiceTime' parameter.");
							canParse = tryParseInt(tokens[1]);
							if (!canParse)				Error.throwError("Error: 'binServiceTime' input type mismatch at line = "+line);
							binServiceTime = Integer.parseInt(tokens[1]);
							if (binServiceTime < 0)		Error.throwError("Error: 'binServiceTime' parameter cannot be negative.");
							if (binServiceTime == 0)	System.out.println("Warning: 'binServiceTime' parameter is zero.");
							if (binServiceTime > 65535)	Error.throwError("Error: 'binServiceTime' input exceeds maximum value 65,535 at line = "+line);
							binServiceTimeFound = true;
						}
						break;
	
					case "binVolume":
						if (binVolumeFound) {
							System.out.println("Warning: 'binVolume' parameter has already been found. Disregarding this input.");
						} else if (tokensLen < 2) {
							System.out.println("Warning: Missing input in this line: " + line);
						} else {
							if (tokensLen > 2)		System.out.println("Warning: too many inputs for 'binVolume' parameter");
							canParse = tryParseFloat(tokens[1]);
							if (!canParse)			Error.throwError("Error: 'binVolume' input type mismatch at line = "+line);
							binVolume = Float.parseFloat(tokens[1]);
							if (binVolume < 0)		Error.throwError("Error: 'binVolume' cannot be negative.");
							if (binVolume == 0)		System.out.println("Warning: 'binVolume' parameter is zero.");
							binVolumeFound = true;	
						}				
						break;
					
					case "disposalDistrRate":
						if (disposalDistrRateFound) {
							System.out.println("Warning: 'disposalDistrRate' parameter has already been found. Disregarding this input.");
						} else if (tokensLen < 2) {
							System.out.println("Warning: Missing input in this line: " + line);
						} else if (tokensLen >= 2) {
							if (tokens[1].equals("experiment")) {	// experimentation
								if (tokensLen == 2) {
									System.out.println("Warning: Missing input in this line: "+line+"\nThis line will be disregarded.");
								} else {
									for (int i = 2; i < tokensLen; i++) {
										canParse = tryParseFloat(tokens[i]);
										if (!canParse)			Error.throwError("Error: 'disposalDistrRate' input type mismatch at line = "+line);
										float ddr = Float.parseFloat(tokens[i]);
										if (ddr < 0)			Error.throwError("Error: 'disposalDistrRate' parameter cannot be negative.");
										if (ddr == 0)			System.out.println("Warning: 'disposalDistrRate' parameter is zero.");
										disposalDistrRateExp.add(ddr);
										isExperiment = true;
									}
									disposalDistrRateFound = true;
									if (disposalDistrRateExp.size()==1) 	System.out.println("Warning: Only one experimentation input for 'disposalDistrRate'.");	// continue as experiment
								}
							} else {	// not experimentation
								if (tokensLen > 2)				System.out.println("Warning: too many inputs for 'disposalDistrRate' parameter");
								canParse = tryParseFloat(tokens[1]);
								if (!canParse)					Error.throwError("Error: 'disposalDistrRate' input type mismatch at line = "+line);
								disposalDistrRate = Float.parseFloat(tokens[1]);
								if (disposalDistrRate < 0)		Error.throwError("Error: 'disposalDistrRate' parameter cannot be negative.");
								if (disposalDistrRate == 0)		System.out.println("Warning: 'disposalDistrRate' parameter is zero.");
								disposalDistrRateFound = true;
							}
						}
						break;
					
					case "disposalDistrShape":
						if (disposalDistrShapeFound) {
							System.out.println("Warning: 'disposalDistrShape' parameter has already been found. Disregarding this input.");
						} else if (tokensLen < 2) {
							System.out.println("Warning: Missing input in this line: " + line);
						} else if (tokensLen >= 2) {
							if (tokens[1].equals("experiment")) {	// experimentation
								if (tokensLen == 2) {
									System.out.println("Warning: Missing input in this line: "+line+"\nThis line will be disregarded.");
								} else {
									for (int i = 2; i < tokensLen; i++) {
										canParse = tryParseShort(tokens[i]);
										if (!canParse)			Error.throwError("Error: 'disposalDistrShape' input type mismatch at line = "+line);
										short dds = Short.parseShort(tokens[i]);
										if (dds < 0)			Error.throwError("Error: 'disposalDistrShape' parameter cannot be negative.");
										if (dds == 0)			System.out.println("Warning: The 'disposalDistrShape' parameter is zero.");
										if (dds > 255)	Error.throwError("Error: 'disposalDistrShape' input exceeds maximum value 255 at line = "+line);
										disposalDistrShapeExp.add(dds);
										isExperiment = true;
									}
									disposalDistrShapeFound = true;
									if (disposalDistrShapeExp.size()==1) 	System.out.println("Warning: Only one experimentation input for 'disposalDistrShape'.");	// continue as experiment
								}
							} else {	// not experimentation
								if (tokensLen > 2)				System.out.println("Warning: too many inputs for 'disposalDistrShape' parameter");
								canParse = tryParseShort(tokens[1]);
								if (!canParse)					Error.throwError("Error: 'disposalDistrShape' input type mismatch at line = "+line);
								disposalDistrShape = Short.parseShort(tokens[1]);
								if (disposalDistrShape < 0)		Error.throwError("Error: 'disposalDistrShape' parameter cannot be negative.");
								if (disposalDistrShape == 0)	System.out.println("Warning: 'disposalDistrShape' parameter is zero.");
								if (disposalDistrShape > 255)	Error.throwError("Error: 'disposalDistrShape' input exceeds maximum value 255 at line = "+line);
								disposalDistrShapeFound = true;
							}
						}  
						break;
					
					case "bagVolume":
						if (bagVolumeFound) {
							System.out.println("Warning: 'bagVolume' parameter has already been found. Disregarding this input.");
						}  else if (tokensLen < 2) {
							System.out.println("Warning: Missing input in this line: " + line);
						} else {
							if (tokensLen > 2)		System.out.println("Warning: too many inputs for 'bagVolume' parameter");
							canParse = tryParseFloat(tokens[1]);
							if (!canParse)			Error.throwError("Error: 'bagVolume' input type mismatch at line = "+line);
							bagVolume = Float.parseFloat(tokens[1]);
							if (bagVolume < 0)		Error.throwError("Error: 'bagVolume' parameter cannot be negative.");
							if (bagVolume == 0)		System.out.println("Warning: 'bagVolume' parameter is zero.");
							bagVolumeFound = true;
						}  
						break;
					
					case "bagWeightMin":
						if (bagWeightMinFound) {
							System.out.println("Warning: 'bagWeightMin' parameter has already been found. Disregarding this input.");
						} else if (tokensLen < 2) {
							System.out.println("Warning: Missing input in this line: " + line);
						} else {
							if (tokensLen > 2)			System.out.println("Warning: too many inputs for 'bagWeightMin' parameter");
							canParse = tryParseFloat(tokens[1]);
							if (!canParse)				Error.throwError("Error: 'bagWeightMin' input type mismatch at line = "+line);
							bagWeightMin = Float.parseFloat(tokens[1]);
							if (bagWeightMin < 0)		Error.throwError("Error: 'bagWeightMin' parameter cannot be negative.");
							if (bagWeightMin == 0)		System.out.println("Warning: 'bagWeightMin' parameter is zero.");
							bagWeightMinFound = true;
						}  
						break;
					
					case "bagWeightMax":
						if (bagWeightMaxFound) {
							System.out.println("Warning: 'bagWeightMax' parameter has already been found. Disregarding this input.");
						} else if (tokensLen < 2) {
							System.out.println("Warning: Missing input in this line: " + line);
						} else {
							if (tokensLen > 2)			System.out.println("Warning: too many inputs for 'bagWeightMax' parameter");
							canParse = tryParseFloat(tokens[1]);
							if (!canParse)				Error.throwError("Error: 'bagWeightMax' input type mismatch at line = "+line);
							bagWeightMax = Float.parseFloat(tokens[1]);
							if (bagWeightMax < 0)		Error.throwError("Error: 'bagWeightMax' parameter cannot be negative.");
							if (bagWeightMax == 0)		System.out.println("Warning: 'bagWeightMax' parameter is zero.");
							bagWeightMaxFound = true;
						}  
						break;
					
					case "stopTime":
						if (stopTimeFound) {
							System.out.println("Warning: 'stopTimeFound' parameter has already been found. Disregarding this input.");
						} else if (tokensLen < 2) {
							System.out.println("Warning: Missing input in this line: " + line);
						} else {
							if (tokensLen > 2)		System.out.println("Warning: too many inputs for 'stopTime' parameter");
							canParse = tryParseFloat(tokens[1]);
							if (!canParse)			Error.throwError("Error: 'stopTime' input type mismatch at line = "+line);
							stopTime = Float.parseFloat(tokens[1]);
							if (stopTime < 0)		Error.throwError("Error: 'stopTime' parameter cannot be negative.");
							if (stopTime == 0)		System.out.println("Warning: 'stopTime' parameter is zero.");
							stopTimeFound = true;
						}  
						break;
					
					case "warmUpTime":
						if (warmUpTimeFound) {
							System.out.println("Warning: 'warmUpTime' parameter has already been found. Disregarding this input.");
						} else if (tokensLen < 2) {
							System.out.println("Warning: Missing input in this line: " + line);
							// TODO add a line saying we will keep searching for the input???
						} else {
							if (tokensLen > 2)			System.out.println("Warning: too many inputs for 'warmUpTime' parameter");
							canParse = tryParseFloat(tokens[1]);
							if (!canParse)				Error.throwError("Error: 'warmUpTime' input type mismatch at line = "+line);
							warmUpTime = Float.parseFloat(tokens[1]);
							if (warmUpTime < 0)			Error.throwError("Error: 'warmUpTime' parameter cannot be negative.");
							if (warmUpTime == 0)		System.out.println("Warning: 'warmUpTime' parameters is zero.");
							warmUpTimeFound = true;
						}
						break;
						
					default: System.out.println("Warning: Unrecognised input parameter: '" + tokens[0] + "'. This line will be ignored.");
					}
				}
			}
			br.close();

		} catch (FileNotFoundException e) {
			Error.throwError("Error: Input file not found. The simulation will terminate");
			e.printStackTrace();
		} catch (IOException e) {
			Error.throwError("Error: IO Exception error.");
			e.printStackTrace();
		} 
	}
	
	/**
	 * Identifies missing inputs and terminate program if missing.
	 * If all found, checks if all inputs make sense.
	 * Terminates program if error exists.
	 */
	public static void validation() {
		if (!lorryVolumeFound) {
			Error.throwError("Error: Missing parameter 'lorryVolume'."); 
		}
		if (!lorryMaxLoadFound)	{
			Error.throwError("Error: Missing parameter 'lorryMaxLoad'."); 
		}
		if (!binServiceTimeFound) {
			Error.throwError("Error: Missing parameter 'binServiceTime'.");
		}
		if (!binVolumeFound) {
			Error.throwError("Error: Missing parameter 'binVolume'.");
		}
		if (!disposalDistrRateFound) {
			Error.throwError("Error: Missing parameter 'disposalDistrRate'.");
		}
		if (!disposalDistrShapeFound) {
			Error.throwError("Error: Missing parameter 'disposalDistrShape'.");
		}
		if (!bagVolumeFound) {
			Error.throwError("Error: Missing parameter 'bagVolume'.");
		}
		if (!bagWeightMinFound) {
			Error.throwError("Error: Missing parameter 'bagWeightMin'.");
		}
		if (!bagWeightMaxFound)	{
			Error.throwError("Error: Missing parameter 'bagWeightMax'.");
		}
		if (noAreas > 0) {
			if (!serviceAreasFound) {
				Error.throwError("Error: Insufficient or missing service areas."
						+ "\nNumber of service areas: " + noAreas
						+ "\nNumber of service area specifications found: " + serviceAreas.size()); 
			}
		}
		if (!noAreasFound) {
			Error.throwError("Error: Missing parameter 'noAreas'."); 
		}
		if (!stopTimeFound)	{
			Error.throwError("Error: Missing parameter 'stopTime'."); 
		}
		if (!warmUpTimeFound) {
			Error.throwError("Error: Missing parameter 'warmUpTime'."); 
		}
		// checks magnitudes of parameters
		if (bagWeightMin > bagWeightMax) {
			Error.throwError("Error: 'bagWeightMin' parameter greater than 'bagWeightMax'.");
		}
		if (stopTime < warmUpTime) {
			System.out.println("Warning: 'stopTime' parameter smaller than 'warmUpTime'. The simulation will continue.");
		}
		if (bagWeightMin == bagWeightMax) {
			System.out.println("Warning: 'bagWeightMax' parameter equals to 'bagWeightMin'. The simulation will continue.");
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
		for (ServiceArea sa : serviceAreas.values()) {
			float serviceFreq = sa.getServiceFreq();
			short areaIdx = sa.getAreaIdx();
			if (disposalDistrRate < serviceFreq) {
				System.out.println("Warning: 'disposalDistrRate' parameter less than 'serviceFreq' for service area with areaIdx = "+areaIdx);
			}
		}
	}
	
	/**
	 * Initialize all static class variables for all classes
	 */
	public static void initialiseClassVars() {
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
	}
	
	/** 
	 * assign a lorry to each service areas
	 */
	public static void initialiseCity() {
		for (ServiceArea sa : serviceAreas.values()) {
			sa.setLorry(new Lorry());
		}
	}
	
	/**
	 * method to print all parameter values in human readable format
	 */
	public static void printAllInputs() {
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
		for (ServiceArea sa : serviceAreas.values()) System.out.println(sa.toString());
		System.out.println("stopTime = "+stopTime);
		System.out.println("warmUpTime = "+warmUpTime);
		System.out.println("isExperiment = "+isExperiment);		// check whether it is an experimentation input file
		System.out.println("All disposalDistrRate:");
		for (Float sa : disposalDistrRateExp) System.out.println("disposalDistrRateExp: " + sa);
		System.out.println("All disposalDistrShape:");
		for (Short sa : disposalDistrShapeExp) System.out.println("disposalDistrShapeExp: " + sa);
		System.out.println("All serviceFreq:");
		for (Float sa : serviceFreqExp) System.out.println("serviceFreqExp: " + sa);
	}
	
	// Simulator implementation
	private PriorityQueue<AbstractEvent> events = new PriorityQueue<AbstractEvent>(); // for storing upcoming events
	private int time;	// current simulation time
	public int now() {
		return time;
	}
	public void insert(AbstractEvent e) {	// insert event into queue
		this.events.add(e);
	}
	
	/**
	 * extracts all events from 'events' priority queue and execute them
	 */
	public void doAllEvents() {		// extracts all events from 'events' queue and execute them
		AbstractEvent e;
        while ((e = (AbstractEvent) events.poll()) != null) {
            time = e.getTime();
            e.execute(this);
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
			if (disposalDistrRateExp.size() > 0) {
				disposalDistrRate = disposalDistrRateExp.get(0);
			}
			if (disposalDistrShapeExp.size() > 0) {
				disposalDistrShape = disposalDistrShapeExp.get(0);
			}

			// set attributes again for experiments
			Random.setDisposalDistrRate(disposalDistrRate);
			Random.setDisposalDistrShape(disposalDistrShape);
			
			for (ServiceArea sa : serviceAreas.values()) {	// generate an initial disposal event for each bin
				for (Bin bin : sa.getBins()) {
					DisposalEvent disposalEventGenerator = new DisposalEvent(Random.erlangk(), bin);
					this.events.add(disposalEventGenerator);
				}
			}
			doAllEvents();	// execute all events from priority queue
		} else {
			for (ServiceArea sa : serviceAreas.values()) {	// generate an initial disposal event for each bin
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
		//System.out.println("Simulation finishes. Statistical Analysis starts:");
		// System.out.println("Warning: No implementation yet for statistical analysis.");
	}
	
	public static void main(String[] args) throws FileNotFoundException {
		String file_path = args[0];
		Simulator citySimulator = new Simulator();
		
		Simulator.parseInputs(file_path);	// parse inputs

		Simulator.validation();		// check that all inputs exist
		
		Simulator.initialiseClassVars(); // set up all Classes		
		Simulator.initialiseCity();		// set up all service areas
		
		// should maybe implement this into two parts for experiments and non experiment
		citySimulator.start();
		citySimulator.statsAnalysis();
		
    } 	
}