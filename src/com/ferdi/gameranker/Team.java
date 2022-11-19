package com.ferdi.gameranker;

/**
 * 
 * Encapsulates a Team
 * 
 * @author ferdi
 *
 */
public class Team implements Comparable<Team> {
	private String teamName;
	private int points;
	
	/**
	 * Always construct a team with its name
	 * 
	 * @param teamName
	 */
	public Team(String teamName) {
		this.setTeamName(teamName);
		this.points = 0;
	}
	
	public void addPoints(int points) {
		this.points += points;
	}
	
	public int getPoints() {
		return points;
	}

	public String getTeamName() {
		return teamName;
	}

	private void setTeamName(String teamName) {
		this.teamName = teamName;
	}

	@Override
	public int compareTo(Team o) {
		return Integer.compare(this.points, o.points);
	}
	
}
