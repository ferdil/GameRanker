/**
 * 
 */
package com.ferdi.gameranker;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

/**
 * 
 * @author ferdi
 *
 * The Game Ranking engine per the Problem Statement
 * 
 */
public class GameRanker {

	static String inFileName = ""; 						///> Used for the input file name if any
	static String outFileName = "";						///> Used for the output file name if any
	
	static FileInputStream fileInput = null;					///> Gets mapped to System.in if needed
	static PrintStream output = null;							///> Gets mapped to System.out if needed

	/**
	 * 
	 * Runs from here
	 * @param args - See Usage below
	 * 
	 */
	public static void main(String[] args) {
		// Validate arguments
		checkArgs(args);
		if (args.length == 0) {
			System.err.print("Usage:\n");
			System.err.print("GameRanker -infile inputfilename -outfile outputfilename:\n");
			System.err.print("If either parameter is not supplied, input/output will be from the terminal:\n");
			System.err.print("Press Ctrl-D to run the ranking after your data entry\n");
		}

		// Set the input/output streams based on the arguments
		setInOutStreams();
		
		// Get the input into a in-memory List - Would probably use memcached or redis rather
		List<Game> theGames = getInput();

		// Build a list of teams from the inputs
		Map <String, Team> theTeams = buildTeamList(theGames);
		
		// Run the ranking engine
		List<Team> rankedTeams = rankTeams(theTeams);
        
		// And creat a List from the ranked results
		List<Team> rankedList = new ArrayList<Team>();
	    Iterator<Team> it = rankedTeams.listIterator();
	    it.forEachRemaining(rankedList::add);

	    // Output to System.out the results
	    for (int rank=0; rank < rankedList.size(); rank++) {
	    	Team theTeam = rankedList.get(rank);
	    	System.out.printf("%d. %s, %d %s\n", rank+1, theTeam.getTeamName(), theTeam.getPoints(), theTeam.getPoints() == 1 ? "pt":"pts");
	    }
	}

	/**
	 * Given a list of teams, this ranks them using the point system for the exercise
	 * 
	 * @param theTeams
	 * @return The teams tanked by points
	 */
	static List<Team> rankTeams(Map <String,Team> theTeams) {

        Comparator<Team> byPoints = (Team team1, Team team2) -> team1.compareTo(team2);
        Comparator<Team> byName = (Team team1, Team team2) -> team1.getTeamName().compareTo(team2.getTeamName());
       
        // Note that the ranking is done on points, then on name
        LinkedHashMap<String, Team> rankedTeams = theTeams.entrySet().stream()
                .sorted(Map.Entry.<String, Team>comparingByValue(byPoints).thenComparing(Map.Entry.<String, Team>comparingByValue(byName).reversed()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

        ListIterator<Team> iterator = new ArrayList<Team>(rankedTeams.values()).listIterator(rankedTeams.size());

        List<Team> theList = new ArrayList<Team>();
        while (iterator.hasPrevious()) {
        	theList.add(iterator.previous());
        }
        return theList;
	}
	
	/**
	 * 
	 * Read the game data from the input (either System.in or a file)
	 * @return
	 */
	static List<Game> getInput() {

        List<Game> gameList = new ArrayList<Game>();

		Scanner gameInput = new Scanner(System.in);
		gameInput.useDelimiter("\n");
		
		// Uses a scanner cause I wanted to be able to handle a team name with a number in it.
		// Extracts the score by reversing the string and re-reversing to get the actual score number
		
		gameInput.forEachRemaining(stringIn -> {
			if (stringIn.length() == 0 || stringIn.compareTo(".") == 0) {
				return;
			}
			Scanner scanner = new Scanner(stringIn).useDelimiter(",");
			try {
				String lhs = scanner.next().trim();
				String rhs = scanner.next().trim();
				StringBuilder revScore1 = new StringBuilder(lhs).reverse();
				String score1 = new StringBuilder(revScore1.toString().substring(0, revScore1.indexOf(" "))).reverse().toString();
				String team1 = new StringBuilder(revScore1.toString().substring(revScore1.indexOf(" "))).reverse().toString().trim();
	
				StringBuilder revScore2 = new StringBuilder(rhs).reverse();
				String score2 = new StringBuilder(revScore2.toString().substring(0, revScore2.indexOf(" "))).reverse().toString();
				String team2 = new StringBuilder(revScore2.toString().substring(revScore2.indexOf(" "))).reverse().toString().trim();
	
				Game theGame = new Game(team1,Integer.parseInt(score1),team2,Integer.parseInt(score2));
				gameList.add(theGame);
			} catch (Exception exception) {
				System.err.print(exception);
			} finally {
				scanner.close();				
			}
		});
		gameInput.close();
		return gameList;
	}
	
	/**
	 * Build a list of teams from the game data
	 * 
	 * @param theGames
	 * @return a Map of the Teams in the games
	 * 
	 */
	static Map<String,Team> buildTeamList(List<Game> theGames) {
		Map<String, Team> teams = new HashMap<String, Team>();

        theGames.forEach((game) -> {
        	try {
            	Team team1 = teams.get(game.Team1);
            	if (team1 == null) {
            		team1 = new Team(game.Team1);
            		teams.put(game.Team1, team1);
            	}
            	Team team2 = teams.get(game.Team2);
            	if (team2 == null) {
            		team2 = new Team(game.Team2);
            		teams.put(game.Team2, team2);
            	}
        		if (game.Score1 > game.Score2)
        			team1.addPoints(3);
        		else if (game.Score1 == game.Score2) {
        			team1.addPoints(1);
        			team2.addPoints(1);
        		} else
        			team2.addPoints(3);
        			
        	} catch (Exception e) {
        		System.err.print(e);
        	}
        });
        
		return teams;
	}
	
	/**
	 * Helper function to check arguments
	 * @param args
	 */
	static void checkArgs(String [] args) {
		for (int i=0; i< args.length; i++) {
			try {
				if (args[i].charAt(0) == '-') {
					if ("infile".compareToIgnoreCase(args[i].substring(1)) == 0) {
						inFileName = args[i+1];
					}
					if ("outfile".compareToIgnoreCase(args[i].substring(1)) == 0) {
						outFileName = args[i+1];
					}
				}
			} catch (Exception ex) {
				System.err.print(ex);
			}
		}
	}
	
	/**
	 * Checks to see if the input is from a file or stdin and if output
	 * must go to a file or stdout
	 * 
	 * By using System.setIn and .setOut all I/O can be done using the System library
	 */
	static void setInOutStreams() {
		if (inFileName.length() > 0) {
			try {
				// If we have an input file set the stdio to it
				fileInput = new FileInputStream(new File(inFileName));
				System.setIn(fileInput);
			} catch (FileNotFoundException e) {
				System.err.print("File " + inFileName + " not found. Aborting.");
				System.exit(1);
			}
		} else {
			System.out.println("Enter Game results.");
			System.out.println("Each result should have the format Team1 Score(number), Team 2 Score(number)");
		}
		if (outFileName.length() > 0) {
			try {
				output = new PrintStream( new File(outFileName) );
				System.setOut(output);
			} catch (FileNotFoundException ignore) {
				// Ignore if no file exists
			}
		}
	}
}
