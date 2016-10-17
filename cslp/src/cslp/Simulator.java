package cslp;

import java.awt.List;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Simulator {
	
	public static void main(String[] args) throws InvalidInputFileException, FileNotFoundException {
		
		// II.4. The program displays usage information if no input files are given;
		if ((args.length == 0) || (args[0].length() <= 4)) {
			throw new IllegalArgumentException("\nFound missing or invalid input arguments. "
					+ "\nRun code using \"./simulate.sh <input_file_name> [OPTIONS]\"");
		} 
		
		/*
		else if (!args[0].endsWith(".txt")) {
			throw new IllegalArgumentException("\nFound missing input arguments. "
					+ "\nRun code using \"./simulate.sh <input_file_name> [OPTIONS]\""
					+ "\nInput file should be a text file.");
		}
		*/
		
		String file_path = args[0];
		
		int lorryVolume = 0; // add upper and lower bounds?
		boolean lorryVolumeFound = false;
		int lorryMaxLoad = 0;
		boolean lorryMaxLoadFound = false;
		double binServiceTime = 0.0;
		boolean binServiceTimeFound = false;
		double binVolume = 0.0;
		boolean binVolumeFound = false;
		double disposalDistrRate = 0.0;
		boolean disposalDistrRateFound = false;
		int disposalDistrShape = 0;
		boolean disposalDistrShapeFound = false;
		double bagVolume = 0.0;
		boolean bagVolumeFound = false;
		double bagWeightMin = 0.0;
		boolean bagWeightMinFound = false;
		double bagWeightMax = 0.0;
		boolean bagWeightMaxFound = false;
		int noAreas = 0;
		boolean noAreasFound = false;
		ArrayList<ServiceArea> serviceAreaList = new ArrayList<ServiceArea>();
		double stopTime = 0.0;
		boolean stopTimeFound = false;
		double warmUpTime = 0.0;
		boolean warmUpTimeFound = false;

		
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
		}
		
    }
	
	
	
}