import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Random;
import java.io.BufferedWriter;

public class ListRanker {
	public static void main(String[] args) throws IOException {
		
		File listFile = new File("list.txt");
		Scanner in = new Scanner(listFile);
		ArrayList<String> list = new ArrayList<String>();
		Random rand = new Random();
		while (in.hasNextLine()) {
			list.add(in.nextLine().split("\n")[0]);
		}
		in.close();
		
		//0 means a loss, 1 means a win, 2 means unranked, 3 means n/a i.e. same item from list
		int[][] rankMatrix = new int[list.size()][list.size()];
		
		in = new Scanner(System.in);
		
		File check = new File("matrixSave.txt");
		if (check.exists()) {
			rankMatrix = loadMatrix(rankMatrix);
		} else {
			rankMatrix = initializeMatrix(rankMatrix);
		}
		
		ArrayList<int[]> unrankedMatchups = new ArrayList<int[]>();
		unrankedMatchups = getUnrankedMatchups(unrankedMatchups, rankMatrix);
		int unrankedMatchupsSize = unrankedMatchups.size();
		if (unrankedMatchupsSize == 0) {
			System.out.println("Matrix complete!");
			ArrayList<String> orderedList = new ArrayList<String>();
			orderedList = getOrderedList(list, rankMatrix);
			
			System.out.println("How many ranks do you want?");
			System.out.println("Type 1 to say 5 ranks, 2 to say 6 ranks, 3 to say 8 ranks, and 4 to say 10 ranks");
			double[] ranks;
			Scanner rankProb;
			int[] numItemsPerTier;
			int total;
			switch(in.next().charAt(0)) {
				case '1':
					ranks = new double[5];
					rankProb = new Scanner(new File("5tier.txt"));
					for (int i = 0; i < ranks.length; i++) {
						ranks[i] = Double.parseDouble(rankProb.nextLine());
					}
					numItemsPerTier = new int[5];
					total = 0;
					for (int i = 0; i < numItemsPerTier.length; i++) {
						int insert = (int) Math.floor(ranks[i] * (double) list.size());
						total += insert;
						numItemsPerTier[i] = insert;
					}
					for (int i = 0; i < list.size() - total; i++) {
						numItemsPerTier[rand.nextInt(numItemsPerTier.length)]++;
					}
					saveTiersToFile(orderedList, numItemsPerTier);
					break;
				case '2':
					ranks = new double[6];
					rankProb = new Scanner(new File("6tier.txt"));
					for (int i = 0; i < ranks.length; i++) {
						ranks[i] = Double.parseDouble(rankProb.nextLine());
					}
					numItemsPerTier = new int[6];
					total = 0;
					for (int i = 0; i < numItemsPerTier.length; i++) {
						int insert = (int) Math.floor(ranks[i] * (double) list.size());
						total += insert;
						numItemsPerTier[i] = insert;
					}
					for (int i = 0; i < list.size() - total; i++) {
						numItemsPerTier[rand.nextInt(numItemsPerTier.length)]++;
					}
					saveTiersToFile(orderedList, numItemsPerTier);
					break;
				case '3':
					ranks = new double[8];
					rankProb = new Scanner(new File("8tier.txt"));
					for (int i = 0; i < ranks.length; i++) {
						ranks[i] = Double.parseDouble(rankProb.nextLine());
					}
					numItemsPerTier = new int[8];
					total = 0;
					for (int i = 0; i < numItemsPerTier.length; i++) {
						int insert = (int) Math.floor(ranks[i] * (double) list.size());
						total += insert;
						numItemsPerTier[i] = insert;
					}
					for (int i = 0; i < list.size() - total; i++) {
						numItemsPerTier[rand.nextInt(numItemsPerTier.length)]++;
					}
					saveTiersToFile(orderedList, numItemsPerTier);
					break;
				case '4':
					ranks = new double[10];
					rankProb = new Scanner(new File("10tier.txt"));
					for (int i = 0; i < ranks.length; i++) {
						ranks[i] = Double.parseDouble(rankProb.nextLine());
					}
					numItemsPerTier = new int[10];
					total = 0;
					for (int i = 0; i < numItemsPerTier.length; i++) {
						int insert = (int) Math.floor(ranks[i] * (double) list.size());
						total += insert;
						numItemsPerTier[i] = insert;
					}
					for (int i = 0; i < list.size() - total; i++) {
						numItemsPerTier[rand.nextInt(numItemsPerTier.length)]++;
					}
					saveTiersToFile(orderedList, numItemsPerTier);
					break;
			}
			return;
		} else {
			System.out.println(unrankedMatchupsSize + " matchups remaining");
		}
		
		ArrayList<Integer> yesList1 = new ArrayList<Integer>();
		ArrayList<Integer> yesList2 = new ArrayList<Integer>();
		ArrayList<Integer> noList1 = new ArrayList<Integer>();
		ArrayList<Integer> noList2 = new ArrayList<Integer>();
		
		boolean done = false;
		while(!done) {
			int[] matchup = generateRandomMatchup(unrankedMatchups);
			String name0 = list.get(matchup[0]);
			String name1 = list.get(matchup[1]);
			
			System.out.println("Is " + name0 + " better than " + name1 + "?");
			System.out.println("Type y to say yes, n to say no, and e to save and exit");
			
			switch(in.next().charAt(0)) {
				case 'e':
					done = true;
					saveMatrix(rankMatrix);
					break;
				case 'y':
					rankMatrix[matchup[0]][matchup[1]] = 1;
					rankMatrix[matchup[1]][matchup[0]] = 0;
					
					//find all matchups that matchup[1] beat
					for (int j = 0; j < rankMatrix.length; j++) {
						if (rankMatrix[matchup[1]][j] == 1) {
							yesList1.add(j);
						}
					}
					
					//find all matchups that matchup[0] lost to
					for (int j = 0; j < rankMatrix.length; j++) {
						if (rankMatrix[matchup[0]][j] == 0) {
							yesList2.add(j);
						}
					}
					break;
				case 'n':
					rankMatrix[matchup[0]][matchup[1]] = 0;
					rankMatrix[matchup[1]][matchup[0]] = 1;
					
					//find all matchups that matchup[1] lost to
					for (int j = 0; j < rankMatrix.length; j++) {
						if (rankMatrix[matchup[1]][j] == 0) {
							noList1.add(j);
						}
					}
					
					//find all matchups that matchup[0] beat
					for (int j = 0; j < rankMatrix.length; j++) {
						if (rankMatrix[matchup[0]][j] == 1) {
							noList2.add(j);
						}
					}
					break;
			}
			
			//set all matchups that matchup[1] beat so that matchup[0] beat them as well
			if (!yesList1.isEmpty()) {
				for (int i = 0; i < yesList1.size(); i++) {
					if (rankMatrix[matchup[0]][yesList1.get(i)] == 2) {
						rankMatrix[matchup[0]][yesList1.get(i)] = 1;
						rankMatrix[yesList1.get(i)][matchup[0]] = 0;
					}
				}
				yesList1.clear();
			}
			
			//set all matchups that matchup[0] lost to so that matchup[1] lost to them as well
			if (!yesList2.isEmpty()) {
				for (int i = 0; i < yesList2.size(); i++) {
					if (rankMatrix[matchup[1]][yesList2.get(i)] == 2) {
						rankMatrix[matchup[1]][yesList2.get(i)] = 0;
						rankMatrix[yesList2.get(i)][matchup[1]] = 1;
					}
				}
				yesList2.clear();
			}
			
			//set all matchups that matchup[1] lost to so that matchup[0] lost to them as well
			if (!noList1.isEmpty()) {
				for (int i = 0; i < noList1.size(); i++) {
					if (rankMatrix[matchup[0]][noList1.get(i)] == 2) {
						rankMatrix[matchup[0]][noList1.get(i)] = 0;
						rankMatrix[noList1.get(i)][matchup[0]] = 1;
					}
				}
				noList1.clear();
			}
			
			//set all matchups that matchup[0] beat so that matchup[1] beat them as well
			if (!noList2.isEmpty()) {
				for (int i = 0; i < noList2.size(); i++) {
					if (rankMatrix[matchup[1]][noList2.get(i)] == 2) {
						rankMatrix[matchup[1]][noList2.get(i)] = 1;
						rankMatrix[noList2.get(i)][matchup[1]] = 0;
					}
				}
				noList2.clear();
			}	
			
			unrankedMatchups = getUnrankedMatchups(unrankedMatchups, rankMatrix);
			System.out.println(unrankedMatchups.size() + " matchups remaining");
			if (unrankedMatchups.size() == 0) {
				done = true;
				saveMatrix(rankMatrix);
			}
		}
		
		in.close();
		System.out.println("Okay bet, you're done");
	}
	
	public static void saveTiersToFile(ArrayList<String> orderedList, int[] numItemsPerTier) throws IOException {
		File check = new File("tieredList.txt");
		check.createNewFile();
		
		PrintWriter save = new PrintWriter(new BufferedWriter(new FileWriter("tieredList.txt")));
		int index = 0;
		for (int i = 0; i < numItemsPerTier.length; i++) {
			save.write("Tier " + i);
			save.println();
			save.write("-----------------------------------------------------------------------");
			save.println();
			for (int j = 0; j < numItemsPerTier[i]; j++) {
				save.write("" + orderedList.get(index));
				save.println();
				index++;
			}
			save.println();
		}
		
		save.close();
	}
	
	@SuppressWarnings("unchecked")
	public static ArrayList<String> getOrderedList(ArrayList<String> list, int[][] matrix) {
		ArrayList<Integer> tally = new ArrayList<Integer>();
		for (int i = 0; i < matrix.length; i++) {
			tally.add(0);
			for (int j = 0; j < matrix.length; j++) {
				if (matrix[i][j] == 1) {
					tally.set(i, tally.get(i)+1);
				}
			}
		}
		
		ArrayList<String> listcopy = (ArrayList<String>) list.clone();
		ArrayList<String> orderedList = new ArrayList<String>();
		for (int i = 0; i < list.size(); i++) {
			int maxPos = 0;
			for (int j = 1; j < listcopy.size(); j++) {
				if (tally.get(j) > tally.get(maxPos)) {
					maxPos = j;
				} 
			}
			orderedList.add(listcopy.get(maxPos));
			listcopy.remove(maxPos);
			tally.remove(maxPos);
		}
		
		//make sure items with same number of wins are sorted based on their matchup
		for (int i = 1; i < orderedList.size(); i++) {
			int a = list.indexOf(orderedList.get(i-1));
			int b = list.indexOf(orderedList.get(i));
			if (matrix[a][b] != 1) {
				String temp = orderedList.get(i-1);
				orderedList.set(i-1, orderedList.get(i));
				orderedList.set(i, temp);
			}
		}
		
		return orderedList;
	}
	
	public static int[] generateRandomMatchup(ArrayList<int[]> matchups) {
		Random rand = new Random();
		int random = rand.nextInt(matchups.size());
		int[] temp = matchups.get(random);
		int[] ret = {temp[0],temp[1]};
		return ret;
	}
	
	public static void saveMatrix(int[][] matrix) throws IOException {
		PrintWriter save = new PrintWriter(new BufferedWriter(new FileWriter("matrixSave.txt")));
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix.length; j++) {
				save.write("" + matrix[i][j]);
				save.write(" ");
			}
			save.println();
		}
		
		save.close();
	}
	
	public static ArrayList<int[]> getUnrankedMatchups(ArrayList<int[]> unrankedMatchups, int[][] rankMatrix) {
		unrankedMatchups.clear();
		for (int i = 0; i < rankMatrix.length; i++) {
			for (int j = 0; j < rankMatrix.length; j++) {
				if (rankMatrix[i][j] == 2) {
					int[] pair = {i,j};
					unrankedMatchups.add(pair);
				}
			}
		}
		
		return unrankedMatchups;
	}
	
	public static int[][] loadMatrix(int[][] matrix) throws FileNotFoundException {
		Scanner load = new Scanner(new File("matrixSave.txt"));
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix.length; j++) {
				matrix[i][j] = load.nextInt();
			}
			load.nextLine();
		}
		
		load.close();
		
		return matrix;
	}
	
	public static int[][] initializeMatrix(int[][] matrix) throws IOException {
		File create = new File("matrixSave.txt");
		create.createNewFile();
		
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix.length; j++) {
				if (i==j) {
					matrix[i][j] = 3;
				} else {
					matrix[i][j] = 2;
				}
			}
		}
		
		return matrix;
	}
}