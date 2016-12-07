package cslp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

public class Parser {

	private static final Logger LOGGER = Logger.getLogger(Parser.class.getName());

	// input parameters
	private short lorryVolume;
	private int lorryMaxLoad;
	private int binServiceTime;
	private float binVolume;
	private float disposalDistrRate; // expressed as avg. no. of disposal events per hour
	private short disposalDistrShape;
	private float bagVolume;
	private float bagWeightMin;
	private float bagWeightMax;
	private short noAreas;
	private HashMap<Short,ServiceAreaInfo> serviceAreaInfos = new HashMap<Short,ServiceAreaInfo>();	// roadsLayout elements in seconds, serviceFreq in hour
	private float stopTime; // in second
	private float warmUpTime; // in second

	// Arraylists for storing experiment input parameters
	private boolean isExperiment = false; // indicator for experiment inputs
	private ArrayList<Float> disposalDistrRateExp = new ArrayList<Float>();
	private ArrayList<Short> disposalDistrShapeExp = new ArrayList<Short>();
	private ArrayList<Float> serviceFreqExp = new ArrayList<Float>();

	// check that all input parameters present
	private boolean lorryVolumeFound = false;
	private boolean lorryMaxLoadFound = false;
	private boolean binServiceTimeFound = false;
	private boolean binVolumeFound = false;
	private boolean disposalDistrRateFound = false;
	private boolean disposalDistrShapeFound = false;
	private boolean bagVolumeFound = false;
	private boolean bagWeightMinFound = false;
	private boolean bagWeightMaxFound = false;
	private boolean noAreasFound = false;
	private boolean serviceAreaInfosFound = false;
	private boolean stopTimeFound = false;
	private boolean warmUpTimeFound = false;

	/**
	 * methods to catch exceptions when parsing int type inputs from file
	 *
	 * @return boolean		true if the type matches
	 */
	private boolean tryParseInt(String value) {
	     try {
	         Integer.parseInt(value);
	         return true;
	      } catch (NumberFormatException e) {
	         return false;
	      }
	}

	/**
	 * methods to catch exceptions when parsing short type inputs from file
	 *
	 * @return boolean		true if the type matches
	 */
	private boolean tryParseShort(String value) {
	     try {
	         Short.parseShort(value);
	         return true;
	      } catch (NumberFormatException e) {
	         return false;
	      }
	}

	/**
	 * methods to catch exceptions when parsing float type inputs from file
	 *
	 * @return boolean		true if the type matches
	 */
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
	 * converts warmUpTime and stopTime from hour to seconds
	 *
	 * @param file_path		absolute file path to a file
	 *
	 */
	public void parseInputs(String file_path) throws FileNotFoundException {
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
							serviceAreaInfosFound = false;
							serviceAreaInfos.clear();
							if (tokensLen > 2)		System.out.println("Warning: Too many inputs for 'noAreas' parameter. Only the first input will be parsed.");
							canParse = tryParseShort(tokens[1]);
							if (!canParse)			Error.throwError("Error: 'noAreas' input type mismatch at line = "+line);
							noAreas = Short.parseShort(tokens[1]);
							if (noAreas < 0)		Error.throwError("Error: 'noAreas' cannot be negative.");
							if (noAreas == 0) {
								System.out.println("Warning: Input for 'noAreas' parameter is 0.");
								serviceAreaInfosFound = true;
							}
							if (noAreas > 255)		Error.throwError("Error: 'noAreas' input exceeds maximum value 255.");
							noAreasFound = true;

							while ((!serviceAreaInfosFound) && (line = br.readLine()) != null) {	// this block looks for service area specifications
								if (!(line.startsWith("#") || line.isEmpty())) {
									String[] areaTokens = line.split("\\s+");
									if (!areaTokens[0].equals("areaIdx")) {
										int totalServiceAreasFound = serviceAreaInfos.size();
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
	                                    if (serviceFreq > Float.MAX_VALUE)	Error.throwError("Error: 'serviceFreq' input exceeds maximum value for type float at line = "+line);

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
	                                    if (noBins == 0)		System.out.println("Warning: Input for 'noBins' parameter is 0 for areaIdx = "+areaIdx);
	                                    if (noBins > 65535) 	Error.throwError("Error: 'noBins' exceeds maximum value 65,535.");

	                                    int m = noBins + 1;
	                                    int[][] roadsLayout = new int[m][m];
	                                    boolean areaFound = false;

	                                    while ((!areaFound) && (line = br.readLine()) != null) {	// this block looks for roadsLayout matrix
	                                    	if (!(line.startsWith("#") || line.isEmpty())) {
	                                    		String[] layoutTokens = line.split("\\s+");
	                                    		if (!(layoutTokens[0].equals("roadsLayout"))) {
	                                    			Error.throwError("Error: Incorrect format."
	                                    					+ "\nThe line following area specification should be the roadsLayout "
	                                    					+ "parameter.\nError in areaIdx = " + areaIdx);
	                                    		}
	                                    		if (layoutTokens.length != 1) {
	                                    			System.out.println("Warning: unrecognised token(s) after 'roadsLayout' parameter. This token will be ignored.");
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
		                                                    	canParse = tryParseInt(matrixTokens[index]);
		                                                    	if (!canParse)		Error.throwError("Error: Element type mismatch in 'roadsLayout' matrix for areaIdx = "+areaIdx);
		                                                        int element = Integer.parseInt(matrixTokens[index]); // in Minutes
		                                                        if (element > 127)	Error.throwError("Error: Element in 'roadsLayout' matrix exceeds maximum value for type int8 (127) at line = "+line);
		                                                        if (element < -1)	Error.throwError("Error: Element in 'roadsLayout' can only be either positive integers or -1 to indicate no direct link. Error in line = "+line);
		                                                    	if ((element == 0) && (count != col)) Error.throwError("Warning: the distance between two bins is 0.");
		                                                    	if (element != -1)	element = element *60;  // convert to second
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
	                                                Error.throwError("Error: Incorrect no. of rows for the "
	                                                		+ "roadsLayout matrix for areaIdx = " + areaIdx);
	                                            }
	                                    	}
	                                    }
	                                    // if service area with the specific areaIdx already exists, overwrite.
	                                    if (serviceAreaInfos.get(areaIdx) != null) {
	                                    	System.out.println("Warning: Service area information for areaIdx = "+areaIdx+" has been found. Overwritting the previously stored service area information.");
	                                    }
	                                    ServiceAreaInfo curr_sa_info = new ServiceAreaInfo(areaIdx,serviceFreq,thresholdVal,noBins,roadsLayout);
	                                    serviceAreaInfos.put(areaIdx, curr_sa_info);	// append new service area to list of service areas
	                                    if (serviceAreaInfos.size() == noAreas)		serviceAreaInfosFound = true;	//check whether sufficient no. of service areas have been found
									}
								}
							}
						}
						break;

					case "serviceFreq":		// experimentation
						if (!(tokens[1].equals("experiment"))) {
							System.out.println("Warning: Missing 'experiment' keyword or unrecognised input 'serviceFreq' in this line: " + line);
						} if (tokensLen == 2) {
							System.out.println("Warning: Missing input in this line: "+line+"\nThis line will be disregarded.");
						} else {
							if (noAreasFound)	System.out.println("Warning: Multiple inputs for 'noAreas' parameter. Previously stored area information will be overwritten.");
							noAreasFound = false;		// reset relevant parameters
							serviceAreaInfosFound = false;
							serviceAreaInfos.clear();
							for (int i = 2; i < tokensLen; i++) {
								canParse = tryParseFloat(tokens[i]);
								if (!canParse)		Error.throwError("Error: 'serviceFreq' input type mismatch at line = "+line);
								float serviceFreq = Float.parseFloat(tokens[i]);
								if (serviceFreq < 0)	Error.throwError("Error: 'serviceFreq' cannot be negative.");
								if (serviceFreq == 0)	System.out.println("Warning: Input for 'serviceFreq' experimentation parameter equals to 0.");
								if (serviceFreq > Float.MAX_VALUE)	Error.throwError("Error: 'serviceFreq' parameter exceed maximum value for type float at line = "+line);
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
											serviceAreaInfosFound = true;
										}
										if (noAreas > 255)	Error.throwError("Error: 'noAreas' input exceeds maximum value 255.");
										noAreasFound = true;
									}
								}
							}

							while ((!serviceAreaInfosFound) && (line = br.readLine()) != null) {
								if (!(line.startsWith("#") || line.isEmpty())) {
									String[] areaTokens = line.split("\\s+");
									if (!areaTokens[0].equals("areaIdx")) {
										int totalServiceAreasFound = serviceAreaInfos.size();
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
	    								if (serviceFreq > Float.MAX_VALUE)	Error.throwError("Error: 'serviceFreq' parameter exceed maximum value for type float at line = "+line);

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
	                                    if (noBins == 0) 		System.out.println("Warning: 'noBins' is 0 for areaIdx = "+areaIdx);
	                                    if (noBins > 65535) 	Error.throwError("Error: noBins exceeds maximum value 65,535.");

	                                    int m = noBins + 1;
	                                    int[][] roadsLayout = new int[m][m];
	                                    boolean areaFound = false;

	                                    while ((!areaFound) && (line = br.readLine()) != null) {
	                                    	if (!(line.startsWith("#") || line.isEmpty())) {
	                                    		String[] layoutTokens = line.split("\\s+");
	                                    		if (!(layoutTokens[0].equals("roadsLayout"))) {
	                                    			Error.throwError("Error: Incorrect format."
	                                    					+ "\nThe line following area specification should be the roadsLayout "
	                                    					+ "parameter.\nError in areaIdx = " + areaIdx);
	                                    		}
	                                    		if (layoutTokens.length != 1) {
	                                    			System.out.println("Warning: unrecognised token(s) after 'roadsLayout' parameter. This token will be ignored.");
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
		                                                    	canParse = tryParseInt(matrixTokens[index]);
		                                                    	if (!canParse)		Error.throwError("Error: Element type mismatch in 'roadsLayout' matrix for areaIdx = "+areaIdx);
		                                                        int element = Integer.parseInt(matrixTokens[index]); // in Minutes
		                                                        if (element > 127)	Error.throwError("Error: Element in 'roadsLayout' matrix exceeds maximum value for type int8 (127) at line = "+line);
		                                                        if (element < -1)	Error.throwError("Error: Element in 'roadsLayout' can only be either positive integers or -1 to indicate no direct link. Error in line = "+line);
		                                                    	if ((element == 0) && (count != col)) System.out.println("Warning: the distance between two bins is 0.");
		                                                    	if (element != -1)	element = element *60;  // convert to second
		                                                        roadsLayout[count][col] = element;
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
	                                    if (serviceAreaInfos.get(areaIdx) != null) {
	                                    	System.out.println("Warning: Service area information for areaIdx = "+areaIdx+" has been found. Overwritting the previously stored service area information.");
	                                    }
	                                    ServiceAreaInfo curr_sa_info = new ServiceAreaInfo(areaIdx,serviceFreq,thresholdVal,noBins,roadsLayout);
	                                    serviceAreaInfos.put(areaIdx,curr_sa_info);
	                                    if (serviceAreaInfos.size() == noAreas)		serviceAreaInfosFound = true;
									}
								}
							}
						}
						break;

					case "lorryVolume":
						if (lorryVolumeFound) {
							System.out.println("Warning: 'lorryVolume' parameter has already been found. Disregarding this input.");
						} else if (tokensLen < 2) {
							System.out.println("Warning: Missing 'lorryVolume' input in this line: " + line);
						} else {
							if (tokensLen > 2)		System.out.println("Warning: too many inputs for 'lorryVolume' parameter. Only the first input will be parsed.");
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
							System.out.println("Warning: Missing 'lorryMaxLoad' input in this line: " + line);
						} else {
							if (tokensLen > 2)			System.out.println("Warning: too many inputs for 'lorryMaxLoad' parameter. Only the first input will be parsed");
							canParse = tryParseInt(tokens[1]);
							if (!canParse)				Error.throwError("Error: 'lorryMaxLoad' input type mismatch at line = "+line);
							lorryMaxLoad = Integer.parseInt(tokens[1]);
							if (lorryMaxLoad < 0)		Error.throwError("Error: 'lorryMaxLoad' cannot be negative.");
							if (lorryMaxLoad == 0)		System.out.println("Warning: 'lorryMaxLoad' parameter is zero.");
							if (lorryMaxLoad > 65535)	Error.throwError("Error: 'lorryMaxLoad' input exceeds maximum value 65,535 at line = "+line);
							lorryMaxLoadFound = true;
						}
				 		break;

					case "binServiceTime":
						if (binServiceTimeFound) {
							System.out.println("Warning: 'binServiceTime' parameter has already been found. Disregarding this input.");
						} else if (tokensLen < 2) {
							System.out.println("Warning: Missing 'binServiceTime' input in this line: " + line);
						} else {
							if (tokensLen > 2)			System.out.println("Warning: too many inputs for 'binServiceTime' parameter. Only the first input will be parsed");
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
							System.out.println("Warning: Missing 'binVolume' input in this line: " + line);
						} else {
							if (tokensLen > 2)		System.out.println("Warning: too many inputs for 'binVolume' parameter. Only the first input will be parsed");
							canParse = tryParseFloat(tokens[1]);
							if (!canParse)			Error.throwError("Error: 'binVolume' input type mismatch at line = "+line);
							binVolume = Float.parseFloat(tokens[1]);
							if (binVolume < 0)		Error.throwError("Error: 'binVolume' cannot be negative.");
							if (binVolume == 0)		System.out.println("Warning: 'binVolume' parameter is zero.");
							if (binVolume > Float.MAX_VALUE)	Error.throwError("Error: 'binVolume' parameter exceeds maximum value for type float at line = "+line);
							binVolumeFound = true;
						}
						break;

					case "disposalDistrRate":
						if (disposalDistrRateFound) {
							System.out.println("Warning: 'disposalDistrRate' parameter has already been found. Disregarding this input.");
						} else if (tokensLen < 2) {
							System.out.println("Warning: Missing 'disposalDistrRate' input in this line: " + line);
						} else if (tokensLen >= 2) {
							if (tokens[1].equals("experiment")) {	// experimentation
								if (tokensLen == 2) {
									System.out.println("Warning: Missing 'disposalDistrRate' experimentation input in this line: "+line+"\nThis line will be disregarded.");
								} else {
									for (int i = 2; i < tokensLen; i++) {
										canParse = tryParseFloat(tokens[i]);
										if (!canParse)			Error.throwError("Error: 'disposalDistrRate' input type mismatch at line = "+line);
										float ddr = Float.parseFloat(tokens[i]);
										if (ddr < 0)			Error.throwError("Error: 'disposalDistrRate' parameter cannot be negative.");
										if (ddr == 0)			Error.throwError("Error: 'disposalDistrRate' parameter is zero. This will result in divison by zero in the erlang-k scale.");
										if (ddr > Float.MAX_VALUE)	Error.throwError("Error: 'disposalDistrRate' parameter exceeds maximum value for type float at line = "+line);
										disposalDistrRateExp.add(ddr);
										isExperiment = true;
									}
									disposalDistrRateFound = true;
									if (disposalDistrRateExp.size()==1) 	System.out.println("Warning: Only one experimentation input for 'disposalDistrRate'.");	// continue as experiment
								}
							} else {	// not experimentation
								if (tokensLen > 2)				System.out.println("Warning: too many inputs for 'disposalDistrRate' parameter. Only the first input will be parsed.");
								canParse = tryParseFloat(tokens[1]);
								if (!canParse)					Error.throwError("Error: 'disposalDistrRate' input type mismatch at line = "+line);
								disposalDistrRate = Float.parseFloat(tokens[1]);
								if (disposalDistrRate < 0)		Error.throwError("Error: 'disposalDistrRate' parameter cannot be negative.");
								if (disposalDistrRate == 0)			Error.throwError("Error: 'disposalDistrRate' parameter is zero. This will result in divison by zero in the erlang-k scale.");
								if (disposalDistrRate > Float.MAX_VALUE)	Error.throwError("Error: 'disposalDistrRate' parameter exceeds maximum value for type float at line ="+line);
								disposalDistrRateFound = true;
							}
						}
						break;

					case "disposalDistrShape":
						if (disposalDistrShapeFound) {
							System.out.println("Warning: 'disposalDistrShape' parameter has already been found. Disregarding this input.");
						} else if (tokensLen < 2) {
							System.out.println("Warning: Missing 'disposalDistrShape' input in this line: " + line);
						} else if (tokensLen >= 2) {
							if (tokens[1].equals("experiment")) {	// experimentation
								if (tokensLen == 2) {
									System.out.println("Warning: Missing 'disposalDistrShape' experimentation input in this line: "+line+"\nThis line will be disregarded.");
								} else {
									for (int i = 2; i < tokensLen; i++) {
										canParse = tryParseShort(tokens[i]);
										if (!canParse)			Error.throwError("Error: 'disposalDistrShape' input type mismatch at line = "+line);
										short dds = Short.parseShort(tokens[i]);
										if (dds < 0)			Error.throwError("Error: 'disposalDistrShape' parameter cannot be negative.");
										if (dds == 0)			Error.throwError("Error: The 'disposalDistrShape' parameter is zero. All disposal events would occur at time = 0.");
										if (dds > 255)			Error.throwError("Error: 'disposalDistrShape' input exceeds maximum value 255 at line = "+line);
										disposalDistrShapeExp.add(dds);
										isExperiment = true;
									}
									disposalDistrShapeFound = true;
									if (disposalDistrShapeExp.size()==1) 	System.out.println("Warning: Only one experimentation input for 'disposalDistrShape'.");	// continue as experiment
								}
							} else {	// not experimentation
								if (tokensLen > 2)				System.out.println("Warning: too many inputs for 'disposalDistrShape' parameter. Only the first input will be parsed.");
								canParse = tryParseShort(tokens[1]);
								if (!canParse)					Error.throwError("Error: 'disposalDistrShape' input type mismatch at line = "+line);
								disposalDistrShape = Short.parseShort(tokens[1]);
								if (disposalDistrShape < 0)		Error.throwError("Error: 'disposalDistrShape' parameter cannot be negative.");
								if (disposalDistrShape == 0)	Error.throwError("Error: The 'disposalDistrShape' parameter is zero. All disposal events would occur at time = 0.");
								if (disposalDistrShape > 255)	Error.throwError("Error: 'disposalDistrShape' input exceeds maximum value 255 at line = "+line);
								disposalDistrShapeFound = true;
							}
						}
						break;

					case "bagVolume":
						if (bagVolumeFound) {
							System.out.println("Warning: 'bagVolume' parameter has already been found. Disregarding this input.");
						}  else if (tokensLen < 2) {
							System.out.println("Warning: Missing 'bagVolume' input in this line: " + line);
						} else {
							if (tokensLen > 2)		System.out.println("Warning: too many inputs for 'bagVolume' parameter. Only the first input will be parsed.");
							canParse = tryParseFloat(tokens[1]);
							if (!canParse)			Error.throwError("Error: 'bagVolume' input type mismatch at line = "+line);
							bagVolume = Float.parseFloat(tokens[1]);
							if (bagVolume < 0)		Error.throwError("Error: 'bagVolume' parameter cannot be negative.");
							if (bagVolume == 0)		System.out.println("Warning: 'bagVolume' parameter is zero.");
							if (bagVolume > Float.MAX_VALUE)	Error.throwError("Error: 'bagVolume' input exceeds maximum value for type float at line = "+line);
							bagVolumeFound = true;
						}
						break;

					case "bagWeightMin":
						if (bagWeightMinFound) {
							System.out.println("Warning: 'bagWeightMin' parameter has already been found. Disregarding this input.");
						} else if (tokensLen < 2) {
							System.out.println("Warning: Missing 'bagWeightMin' input in this line: " + line);
						} else {
							if (tokensLen > 2)			System.out.println("Warning: too many inputs for 'bagWeightMin' parameter. Only the first input will be parsed");
							canParse = tryParseFloat(tokens[1]);
							if (!canParse)				Error.throwError("Error: 'bagWeightMin' input type mismatch at line = "+line);
							bagWeightMin = Float.parseFloat(tokens[1]);
							if (bagWeightMin < 0)		Error.throwError("Error: 'bagWeightMin' parameter cannot be negative.");
							if (bagWeightMin == 0)		System.out.println("Warning: 'bagWeightMin' parameter is zero.");
							if (bagWeightMin > Float.MAX_VALUE)		Error.throwError("Error: 'bagWeightMin' input exceeds maximum value for type float at line = "+line);
							bagWeightMinFound = true;
						}
						break;

					case "bagWeightMax":
						if (bagWeightMaxFound) {
							System.out.println("Warning: 'bagWeightMax' parameter has already been found. Disregarding this input.");
						} else if (tokensLen < 2) {
							System.out.println("Warning: Missing 'bagWeightMax' input in this line: " + line);
						} else {
							if (tokensLen > 2)			System.out.println("Warning: too many inputs for 'bagWeightMax' parameter. Only the first input will be parsed.");
							canParse = tryParseFloat(tokens[1]);
							if (!canParse)				Error.throwError("Error: 'bagWeightMax' input type mismatch at line = "+line);
							bagWeightMax = Float.parseFloat(tokens[1]);
							if (bagWeightMax < 0)		Error.throwError("Error: 'bagWeightMax' parameter cannot be negative.");
							if (bagWeightMax == 0)		System.out.println("Warning: 'bagWeightMax' parameter is zero.");
							if (bagWeightMax > Float.MAX_VALUE)		Error.throwError("Error: 'bagWeightMax' input exceeds maximum value for type float at line = "+line);
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
							stopTime = Float.parseFloat(tokens[1]); // in hour
							if (stopTime < 0)		Error.throwError("Error: 'stopTime' parameter cannot be negative.");
							if (stopTime == 0)		System.out.println("Warning: 'stopTime' parameter is zero.");
							if (stopTime > Float.MAX_VALUE)		Error.throwError("Error: 'stopTime' input exceeds maximum value for type float at line = "+line);
							stopTime = stopTime*60*60;		// convert from hour to second
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
							warmUpTime = Float.parseFloat(tokens[1]); // in hour
							if (warmUpTime < 0)			Error.throwError("Error: 'warmUpTime' parameter cannot be negative.");
							if (warmUpTime == 0)		System.out.println("Warning: 'warmUpTime' parameters is zero.");
							if (warmUpTime > Float.MAX_VALUE)	Error.throwError("Error: 'warmUpTime' input exceeds maximum value for type float at line = "+line);
							warmUpTime = warmUpTime*60*60;		// convert from hour to second
							warmUpTimeFound = true;
						}
						break;

					default: System.out.println("Warning: Unrecognised input parameter: '" + tokens[0] + "'. This line will be ignored.");
					}
				}
			}
			br.close();

		} catch (FileNotFoundException e) {
			Error.throwError("Error: Input file invalid.");
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
	public void validation() {
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
		if (noAreasFound && (noAreas > 0)) { //TODO: is this redundant?
			if (!serviceAreaInfosFound) {
				Error.throwError("Error: Insufficient or missing service areas."
						+ "\nNumber of service areas: " + noAreas
						+ "\nNumber of service area specifications found: " + serviceAreaInfos.size());
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
		if (bagWeightMin == bagWeightMax) {
			System.out.println("Warning: 'bagWeightMin' parameter equals to 'bagWeightMax'. The simulation will continue.");
		}
		if (stopTime < warmUpTime) {
			System.out.println("Warning: 'stopTime' parameter smaller than 'warmUpTime'. The simulation will continue.");
		}
		if (stopTime == warmUpTime) {
			System.out.println("Warning: 'stopTime' parameter equals to 'warmUpTime'. The simulation will continue.");
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
		for (ServiceAreaInfo saInfo : serviceAreaInfos.values()) {
			float serviceFreq = saInfo.getServiceFreq();
			short areaIdx = saInfo.getAreaIdx();
			if (disposalDistrRate < serviceFreq) {
				System.out.println("Warning: 'disposalDistrRate' parameter less than 'serviceFreq' for service area with areaIdx = "+areaIdx+". The simulation will continue.");
			}
		}
	}

	/**
	 * Initialize all static class variables for all classes
	 */
	public void initialiseClassVars() {
		AbstractEvent.setWarmUpTime(warmUpTime);
		AbstractEvent.setStopTime(stopTime);
		AbstractEvent.setIsExperiment(isExperiment);
		
		Bag.setBagVolume(bagVolume);
		Bag.setBagWeightMax(bagWeightMax);
		Bag.setBagWeightMin(bagWeightMin);
		
		Bin.setBinVolume(binVolume);
				
		Lorry.setBinServiceTime(binServiceTime);
		Lorry.setLorryMaxLoad(lorryMaxLoad);
		Lorry.setLorryVolume(lorryVolume);

		if (!isExperiment) {
			LOGGER.info("This is not an experiment. Both of Random static variables will be set here.");
			Random.setDisposalDistrRate(disposalDistrRate);
			Random.setDisposalDistrShape(disposalDistrShape);
		} else {
			LOGGER.info("This is an experiment.");
			if (disposalDistrRateExp.isEmpty()) {
				LOGGER.info("Random's ddr is not an experiment.");
				Random.setDisposalDistrRate(disposalDistrRate);
			}
			if (disposalDistrShapeExp.isEmpty()) {
				LOGGER.info("Random's dds is not an experiment.");
				Random.setDisposalDistrShape(disposalDistrShape);
			}
		}
		
	}

	/** 
	 * Creates a deep copy of the service area Hashmap instance and assign lorry...
	 * 
	 * @return	HashMap<Short,ServiceArea> 		new service area instance
	 */
	public HashMap<Short,ServiceArea> createServiceAreas() {
		LOGGER.info("Creating a deep copy of service area HashMap.");
		HashMap<Short,ServiceArea> serviceAreas = new HashMap<Short,ServiceArea>();
		for (ServiceAreaInfo saInfo : this.serviceAreaInfos.values()) {
			short areaIdx = saInfo.getAreaIdx();
			float serviceFreq = saInfo.getServiceFreq();
			float thresholdVal = saInfo.getThresholdVal();
			int noBins = saInfo.getNoBins();
			int[][] roadsLayout = saInfo.getRoadsLayout();
			ServiceArea sa = new ServiceArea(areaIdx, serviceFreq, thresholdVal, noBins, roadsLayout);
			serviceAreas.put(areaIdx, sa);
		}
		return serviceAreas;
	}
	
	/**
	 * method to print all parameter values in human readable format
	 */
	public void printAllInputs() {
		System.out.println("LOGGING INFO: ------------------- PRINTING ALL INPUTS FOR CHECKING ---------------------");
		System.out.println("lorryVolume : "+lorryVolume);
		System.out.println("lorryMaxLoad : "+lorryMaxLoad);
		System.out.println("binServiceTime : "+binServiceTime);
		System.out.println("binVolume : "+binVolume);
		System.out.println("disposalDistrRate (avg. per hour) : "+disposalDistrRate);
		System.out.println("disposalDistrShape : "+disposalDistrShape);
		System.out.println("bagVolume : "+bagVolume);
		System.out.println("bagWeightMin : "+bagWeightMin);
		System.out.println("bagWeightMax : "+bagWeightMax);
		System.out.println("noAreas : "+noAreas);
		System.out.println("Service Areas information: ");
		for (ServiceAreaInfo saInfo : serviceAreaInfos.values()) System.out.println(saInfo.toString());
		System.out.println("stopTime (in second) : "+stopTime);
		System.out.println("warmUpTime (in second) : "+warmUpTime);
		System.out.println("isExperiment : "+isExperiment);		// check whether it is an experimentation input file
		System.out.println("All disposalDistrRate (avg. per hour):");
		for (Float sa : disposalDistrRateExp) System.out.println("disposalDistrRateExp: " + sa);
		System.out.println("All disposalDistrShape:");
		for (Short sa : disposalDistrShapeExp) System.out.println("disposalDistrShapeExp: " + sa);
		System.out.println("All serviceFreq (expressed in hour):");
		for (Float sa : serviceFreqExp) System.out.println("serviceFreqExp: " + sa);
		System.out.println("LOGGING INFO: ----------------------------- FINISH PRINTING ----------------------------");
	}

	/**
	 * Method to parse and validate inputs from file.
	 * Set all static variables in other classes.
	 *
	 * @param filepath		an absolute path to the desired input file
	 * @throws FileNotFoundException
	 */
	public void runParser(String filepath) throws FileNotFoundException {
		LOGGER.info("Run parser: parse inputs -> validation -> initialise class variables.");
		parseInputs(filepath);
		validation();
		initialiseClassVars();
	}

	public short getLorryVolume() {
		return this.lorryVolume;
	}
	public int getLorryMaxLoad() {
		return this.lorryMaxLoad;
	}
	public int getBinServiceTime() {
		return this.binServiceTime;
	}
	public float getBinVolume() {
		return this.binVolume;
	}
	public float getDisposalDistrRate() {
		return this.disposalDistrRate;
	}
	public short getDisposalDistrShape() {
		return this.disposalDistrShape;
	}
	public float getBagVolume() {
		return this.bagVolume;
	}
	public float getBagWeightMin() {
		return this.bagWeightMin;
	}
	public float getBagWeightMax() {
		return this.bagWeightMax;
	}
	public short getNoAreas() {
		return this.noAreas;
	}
	public float getStopTime() {
		return this.stopTime;
	}
	public float getWarmUpTime() {
		return this.warmUpTime;
	}
	public boolean isExperiment() {
		return this.isExperiment;
	}
	public ArrayList<Float> getDisposalDistrRateExp() {
		return this.disposalDistrRateExp;
	}
	public ArrayList<Short> getDisposalDistrShapeExp() {
		return this.disposalDistrShapeExp;
	}
	public ArrayList<Float> getServiceFreqExp() {
		return this.serviceFreqExp;
	}
}
