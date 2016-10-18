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
	public static void parseInputs(String file_path) throws FileNotFoundException, InvalidInputFileException {
		
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
							if (!noAreasFound)		noAreasFound = true;
							
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
	                                    short noBins = Short.parseShort(areaTokens[7]);
	                                    short m = noBins; 
	                                    m++; // size of roadLayout matrix
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
						
					case "ServiceFreq":
						
					case "lorryVolume":
						if (lorryVolumeFound) {
							System.out.println("Warning: Multiple inputs for lorryVolume variable. Disregarding this input.");
						} else if (tokensLen < 2) {
							throw new InvalidInputFileException("Invalid input format in this line: " + line);
						} else {
							if (tokensLen > 2)		System.out.println("Warning: too many inputs in this line: " + line);
							lorryVolume = Short.parseShort(tokens[1]);
							if (!lorryVolumeFound)		lorryVolumeFound = true;
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
							if (!lorryMaxLoadFound)		lorryMaxLoadFound = true;
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
							if (!binServiceTimeFound)		binServiceTimeFound = true;
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
							if (!binVolumeFound)		binVolumeFound = true;	
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
									if (!disposalDistrRateFound)		disposalDistrRateFound = true;
									if (!isExperiment)		isExperiment = true;
								}
							} else {
								if (tokensLen > 2)		System.out.println("Warning: too many inputs in this line: " + line);
								disposalDistrRate = Float.parseFloat(tokens[1]);
								if (!disposalDistrRateFound)		disposalDistrRateFound = true;
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
									if (!disposalDistrShapeFound)		disposalDistrShapeFound = true;
									if (!isExperiment)		isExperiment = true;
								}
							} else {
								if (tokensLen > 2)		System.out.println("Warning: too many inputs in this line: " + line);
								disposalDistrShape = Short.parseShort(tokens[1]);
								if (!disposalDistrShapeFound)		disposalDistrShapeFound = true;
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
							if (!bagVolumeFound)		bagVolumeFound = true;
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
							if (!bagWeightMinFound)		bagWeightMinFound = true;
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
							if (!bagWeightMaxFound)		bagWeightMaxFound = true;
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
							if (!stopTimeFound)		stopTimeFound = true;
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
							if (!warmUpTimeFound)		warmUpTimeFound = true;
						}
						break;
						
					default: throw new InvalidInputFileException("Invalid input parameter in this line: " + line);
					}
					
										
				}
			}
		
			br.close();;
			
			// check that all inputs are present and valid:
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
		
		/*
		SimSpec[] result = new SimSpec[simulators.size()];
		result = simulators.toArray(result);
		return result;
		*/
	}
	
	public static void main(String[] args) throws FileNotFoundException, InvalidInputFileException {
		
		// II.4. The program displays usage information if no input files are given;
		if ((args.length == 0) || (args[0].length() <= 4)) {
			throw new IllegalArgumentException("\nFound missing or invalid input arguments. "
					+ "\nRun code using \"./simulate.sh <input_file_name> [OPTIONS]\"");
		} 
		
		String file_path = args[0];
		
		parseInputs(file_path);
		
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
		
		
		/*
		try {
			File file = new File(file_path);
			FileReader fileReader = new FileReader(file);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			String line;
			
			while ((line = bufferedReader.readLine()) != null) {
				//System.out.println(line);
				String[] tokens = line.split("\\s+");
				
				// why if else instead of switch case
				if ((tokens[0].equals("#")) || line.isEmpty()) {
					// do nothing
				} 
				
				else if (tokens[0].equals("noAreas")) {
					if (tokens.length != 2) {
						// improve the sentence!
						throw new InvalidInputFileException("Invalid format in input text file in this line: " + line);
					} else {
						
						noAreas = Integer.parseInt(tokens[1]);
						noAreasFound = true;
						// check that the no. of areas is at least 1?? 
						int noAreas_extracted = 0;

						while ((noAreas_extracted < noAreas) && (line = bufferedReader.readLine()) != null) {
							String[] areaTokens = line.split("\\s+");
							
							if ((areaTokens[0].equals("#")) || line.isEmpty()) {
								// do nothing
							} 
							
							else if (areaTokens[0].equals("areaIdx")) {
								if ((areaTokens.length == 8) && (areaTokens[2].equals("serviceFreq")) &&
										(areaTokens[4].equals("thresholdVal")) && (areaTokens[6].equals("noBins"))) {
									
									int areaIdx = Integer.parseInt(areaTokens[1]);
									double serviceFreq = Double.parseDouble(areaTokens[3]);
									double thresholdVal = Double.parseDouble(areaTokens[5]);
									int noBins = Integer.parseInt(areaTokens[7]);
									int m = noBins + 1; // size of roadLayout matrix
									int[][] roadLayout = new int[m][m];
									boolean areaFound = false;
									
									while ((!areaFound) && (line = bufferedReader.readLine()) != null) {
										String[] layoutTokens = line.split("\\s+");
										
										if ((layoutTokens[0].equals("#")) || line.isEmpty()) {
											// do nothing
										} 
										
										else if ((layoutTokens.length == 1) && layoutTokens[0].equals("roadsLayout")) {
											int count = 0;
											while ((count < m) && ((line=bufferedReader.readLine()) != null)) {
												
												String[] matrixTokens = line.trim().split("\\s+");
												if ((matrixTokens[0].equals("#")) || line.isEmpty()) {
													// do nothing
												} 
												
												else if (matrixTokens.length != m) {
													throw new InvalidInputFileException("Incorrect number of row elements in roadlayout for areaIdx = " + areaIdx);
												}
												
												else {
													int index = 0;
													for (int col = 0; col < m; col++) {
														roadLayout[count][col] = Integer.parseInt(matrixTokens[index]);													
														index++;
													}
													count++;
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
												noAreas_extracted++;
												areaFound = true;
											} else {
												throw new InvalidInputFileException("Incorrect no. of columns for the roadLayout matrix for areaIdx = " + areaIdx + ".");
											}
										}
										
										else {
											throw new InvalidInputFileException("Incorrect format: roadsLayout keyword not in the next line of the areaIdx line for areaIdx = " + areaIdx + ".");
										}
									}
								} 
								
								else {
									throw new InvalidInputFileException("Invalid format for the area description line: " + line);
								}
							}
							
							else {
								throw new InvalidInputFileException("Insufficient service area information.");
							}
						}
						
						// check there is equivalent amount of area information
						if (noAreas_extracted != noAreas) {
							throw new InvalidInputFileException("Insufficient area information. Only " + noAreas_extracted + "out of " + noAreas + " areas have been found.");
						} else {
							// do nothing
						}
					}
				} 
				
				else if (tokens[0].equals("lorryVolume")) {
					if (tokens.length != 2) {
						throw new InvalidInputFileException("Invalid format in input text file in this line: " + line);
					} else {
						lorryVolume = Integer.parseInt(tokens[1]);
						lorryVolumeFound = true;
					}
				} 
				
				else if (tokens[0].equals("lorryMaxLoad")) {
					if (tokens.length != 2) {
						throw new InvalidInputFileException("Invalid format in input text file in this line: " + line);
					} else {
						lorryMaxLoad = Integer.parseInt(tokens[1]);
						lorryMaxLoadFound = true;
					}
				} 
				
				else if (tokens[0].equals("binServiceTime")) {
					if (tokens.length != 2) {
						throw new InvalidInputFileException("Invalid format in input text file in this line: " + line);
					} else { 
						binServiceTime = Double.parseDouble(tokens[1]);
						binServiceTimeFound = true;
					}
				} 
				
				else if (tokens[0].equals("binVolume")) {
					if (tokens.length != 2) {
						throw new InvalidInputFileException("Invalid format in input text file in this line: " + line);
					} else { 
						binVolume = Double.parseDouble(tokens[1]);
						binVolumeFound = true;
					}
				} 
				
				else if (tokens[0].equals("disposalDistrRate")) {
					if (tokens.length != 2) {
						throw new InvalidInputFileException("Invalid format in input text file in this line: " + line);
					} else { 
						disposalDistrRate = Double.parseDouble(tokens[1]);
						disposalDistrRateFound = true;
					}
				} 
				
				else if (tokens[0].equals("disposalDistrShape")) {
					if (tokens.length != 2) {
						throw new InvalidInputFileException("Invalid format in input text file in this line: " + line);
					} else {
						disposalDistrShape = Integer.parseInt(tokens[1]);
						disposalDistrShapeFound = true;
					}
				} 
				
				else if (tokens[0].equals("bagVolume")) {
					if (tokens.length != 2) {
						throw new InvalidInputFileException("Invalid format in input text file in this line: " + line);
					} else { 
						bagVolume = Double.parseDouble(tokens[1]);
						bagVolumeFound = true;
					}
				} 
				
				else if (tokens[0].equals("bagWeightMin")) {
					if (tokens.length != 2) {
						throw new InvalidInputFileException("Invalid format in input text file in this line: " + line);
					} else { 
						bagWeightMin = Double.parseDouble(tokens[1]);
						bagWeightMinFound = true;
						
					}
				} 
				
				else if (tokens[0].equals("bagWeightMax")) {
					if (tokens.length != 2) {
						throw new InvalidInputFileException("Invalid format in input text file in this line: " + line);
					} else { 
						bagWeightMax = Double.parseDouble(tokens[1]);
						bagWeightMaxFound = true;
					}
				} 
				
				else if (tokens[0].equals("stopTime")) {
					if (tokens.length != 2) {
						throw new InvalidInputFileException("Invalid format in input text file in this line: " + line);
					} else { 
						stopTime = Double.parseDouble(tokens[1]);
						stopTimeFound = true;
					}
				} 
				
				else if (tokens[0].equals("warmUpTime")) {
					if (tokens.length != 2) {
						throw new InvalidInputFileException("Invalid format in input text file in this line: " + line);
					} else { 
						warmUpTime = Double.parseDouble(tokens[1]);
						warmUpTimeFound = true;
					}
				} 
				
				else {
					throw new InvalidInputFileException("Invalid line: " + line);
				}
			}
			
			System.out.println("lorryVolume" + lorryVolume);
			System.out.println("lorryMaxLoad" + lorryMaxLoad);
			System.out.println("binServiceTime" + binServiceTime);
			System.out.println("binVolume" + binVolume);
			System.out.println("disposalDistrRate" + disposalDistrRate);
			System.out.println("disposalDistrShape"+ disposalDistrShape);
			System.out.println("bagVolume" + bagVolume);
			System.out.println("bagWeightMin" + bagWeightMin);
			System.out.println("bagWeightMax" + bagWeightMax);
			System.out.println("noAreas" + noAreas);
			System.out.println("stopTime" + stopTime);
			System.out.println("warmUpTime" + warmUpTime);
			
			// II.5(c) identifying missing parameters
			if (!lorryVolumeFound || !lorryMaxLoadFound || !binServiceTimeFound || !binVolumeFound ||
					!disposalDistrRateFound || !disposalDistrShapeFound || !bagVolumeFound || 
					!bagWeightMinFound || !bagWeightMaxFound || !noAreasFound || !stopTimeFound || 
					!warmUpTimeFound) {
				throw new InvalidInputFileException("Missing parameters.");
				// print out all missing parameters?
				// create an arraylist of missing parameters, 
				// or create an array for identfying missing parameters... 0 for missing and 1 for found
				// or use key-map?? hash map? really cba doing this part
			}
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			//System.out.println("Program will be terminated.\n"); 
			//System.exit(1);
		}*/
		
    } // end of main method
	
	
	
}