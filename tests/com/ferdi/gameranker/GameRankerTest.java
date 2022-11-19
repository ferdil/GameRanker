/**
 * 
 * Span Digital Coding Exercise
 * 
 * November 2011
 * 
 * Ferdi Ladeira
 * 
 */
package com.ferdi.gameranker;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.jupiter.api.Assertions.*;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * @author ferdi ladeira
 *
 * This is a command line Game Ranking application
 *
 */
class GameRankerTest extends GameRanker {

    private static GameRanker gameRanker;

	/**
	 * @throws java.lang.Exception
	 * 
	 * Create an instance for use in the testing
	 */
	@BeforeAll
	static void setUpBeforeClass() throws Exception {
        setGameRanker(new GameRanker());
	}

	/**
	 * Test method for {@link com.ferdi.gameranker.GameRanker#checkArgs(java.lang.String[])}.
	 */
	@Test
	void testCheckArgs() {
		
		// Test for no args supplied
    	String[] noargs = {};
    	// test for arguments supplied
    	String[] arguments= {"-infile", "games.txt", "-outfile", "results.txt"};
    	
    	gameRanker = getGameRanker();
    	
    	// Check if no arguments are passed
    	gameRanker.checkArgs(noargs);
    	
    	assertSame( "Input from stdin", gameRanker.fileInput, null);
    	assertSame( "Output to stdout", gameRanker.output, null);

    	// Check if arguments are passed
    	gameRanker.checkArgs(arguments);

    	// The System.in should point to the input file
    	assertNotSame( "Input from file", gameRanker.fileInput, System.in);
    	assertNotSame( "Output to stdout", gameRanker.output, System.out);
	}

	/**
	 * Test method for {@link com.ferdi.gameranker.GameRanker#setInOutStreams()}.
	 */
	@Test
	void testSetInOutStreams() {
    	String[] noargs = {};

    	gameRanker = getGameRanker();
    	gameRanker.checkArgs(noargs);
    	gameRanker.setInOutStreams();

	}

	/**
	 * Test using standard data - see getTestData() 
	 */
	@Test
	void testGetInput() {
		
    	ByteArrayInputStream testIn = new ByteArrayInputStream(getTestData().getBytes());
    	System.setIn(testIn);
    	
    	gameRanker = getGameRanker();
    	List<Game> theGames = gameRanker.getInput();
    	
    	assertEquals("Check Game 1",theGames.get(0).Team1,"Lions");
    	assertEquals("Check Game 1",theGames.get(0).Score1,3);
    	assertEquals("Check Game 1",theGames.get(0).Team2,"Snakes");
    	assertEquals("Check Game 1",theGames.get(0).Score2,2);

    	assertEquals("Check Game 1",theGames.get(1).Team1,"Tarantulas");
    	assertEquals("Check Game 1",theGames.get(1).Score1,1);
    	assertEquals("Check Game 1",theGames.get(1).Team2,"FC Awesome");
    	assertEquals("Check Game 1",theGames.get(1).Score2,0);
	}
	
	@Test
	void testRankTeams() {
    	ByteArrayInputStream testIn = new ByteArrayInputStream(getTestData().getBytes());
    	System.setIn(testIn);
    	
    	gameRanker = getGameRanker();
    	List<Game> theGames = gameRanker.getInput();
		Map <String, Team> theTeams = buildTeamList(theGames);

		List<Team> rankedTeams = rankTeams(theTeams);
        
	    for (int rank=0; rank < rankedTeams.size(); rank++) {
	    	Team theTeam = rankedTeams.get(rank);
	    	System.out.printf("%d. %s, %d %s\n", rank+1, theTeam.getTeamName(), theTeam.getPoints(), theTeam.getPoints() == 1 ? "pt":"pts");
	    }

		assertEquals("Highest Rank", rankedTeams.get(0).getTeamName(),"Lions");
		assertEquals("Highest Score", rankedTeams.get(0).getPoints(),7);
	}
	
	@Test
	void testBuildTeamList() {
    	ByteArrayInputStream testIn = new ByteArrayInputStream(getTestData().getBytes());
    	System.setIn(testIn);
    	
    	gameRanker = getGameRanker();
    	List<Game> theGames = gameRanker.getInput();
		Map <String, Team> theTeams = buildTeamList(theGames);

		assertEquals("Check Teams",theTeams.get("Lions").getPoints(),7);
		assertEquals("Check Teams",theTeams.get("Snakes").getPoints(),0);
		assertEquals("Check Teams",theTeams.get("FC Awesome Points").getPoints(),1);
	}
	
	/**
	 * Some standard data
	 * 
	 * @return Test data as string
	 */
	private String getTestData() {
		return 	"Lions 3, Snakes 2\n" +
				"Tarantulas 1, FC Awesome 0\n" +
				"Lions 1, FC Awesome 1\n" + 
				"Tarantulas 3, Snakes 1\n" +
				"Lions 4, Grouches 0\n";
	}
	
	public GameRanker getGameRanker() {
		return gameRanker;
	}

	public static void setGameRanker(GameRanker gameRankerIn) {
		gameRanker = gameRankerIn;
	}

}
