package com.ferdi.gameranker;

/**
 * 
 * @author ferdi
 *
 * Encapsulates a Game
 * 
 */
public class Game implements Comparable<Game>  {

	String Team1;
	int Score1;
	String Team2;
	int Score2;

	/**
	 *  Create a game
	 * @param Team1 - Name of Team 1
	 * @param Score1 - Score of Team 1
	 * @param Team2 
	 * @param Score2
	 */
	public Game(String Team1, int Score1, String Team2, int Score2) {
		this.Team1 = Team1;
		this.Team2 = Team2;
		this.Score1= Score1;
		this.Score2 = Score2;
	}
	
	@Override
	public int compareTo(Game o) {
		return (this.Score1 > o.Score1) ? 0 : 1;
	}
	
}
