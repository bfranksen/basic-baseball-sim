/*======================================

Project:       Baseball Simulator
File:          Teams.java
Author:        Braden Franksen
Date:          Mar 12, 2017

======================================*/
package simulator.teams;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import simulator.players.Player;


/**
 * @author Braden
 *
 */
public class Teams {
	
	public String tName = "", tAcronym = "";
	public int tYear = 0, tWins = 0, tLosses = 0;
	public int tSeriesWins = 0;
	protected int cachedHashCode;
	public ArrayList<Player> roster, bench, bullpen;
	public ArrayList<Player> gameBatters, gamePitchers, seasonBatters, seasonPitchers;
	
	public Teams() {
		this.roster = new ArrayList<Player>();
		this.bench = new ArrayList<Player>();
		this.bullpen = new ArrayList<Player>();
		this.seasonBatters = new ArrayList<Player>();
		this.seasonPitchers = new ArrayList<Player>();
		this.cachedHashCode = computeHashCode();
	}

	private int computeHashCode() {
		int result = 1;
		result = 31 * result + ((tAcronym == null) ? 0 : tAcronym.hashCode());
		result = 31 * result + ((tName == null) ? 0 : tName.hashCode());
		result = 31 * result + tYear;
		return result;
	}
	
	@Override
	public boolean equals(Object other) {
		if(this == other) {
			return true;
		}
		if(!(other instanceof Teams) || other == null)
			return false;
		Teams t = (Teams) other;
		return tName == t.tName && tAcronym == t.tAcronym && tYear == t.tYear;
	}

	@Override
	public int hashCode() {
		return this.cachedHashCode;
	}
	
	public void setInfo(String name, String acronym, int year) {
		this.tName = name;
		this.tAcronym = acronym;
		this.tYear = year;
	}
	
	public void addPlayer(Player player) {
		roster.add(player);
		if(player.role.equals("b") && !player.role.equals(null) || (player.role.equals("1B")))
			bench.add(player);
		if(player.position.equals("P"))
			bullpen.add(player);
	}
	
	public Player getPlayer(String name) {
		Player target = roster.get(0);
		for(Player p : roster) {
			if(p.name.equals(name))
				target = p;
		}
		return target;
	}
	
	public String printRoster() {
		String result = "" + tYear + " " + tName + " Roster:\n";
		for(Player p : this.roster)
			result += p + " (" + p.position + ") " + p.name.hashCode() + "  " + p.position.hashCode() + "  " + p.LR.hashCode() + "  " + p.hashCode() + "\n";
		return result;
	}
	
	public void setAverages() {
		for(Player p : roster) {
			p.setBaseAvg();
		}
	}

	@Override
	public String toString() {
		return this.tName.replaceAll("-", " ");
	}

/* ====================================================================================================== */
/* ====================================================================================================== */
/* ====================================================================================================== */
	
	public final static List<String> americanTeams = Arrays.asList(
			"BOS", "BAL", "TOR", "NYY", "TBR",
			"CLE", "DET", "KCR", "CHW", "MIN",
			"TEX", "SEA", "HOU", "LAA", "OAK");
	
	public final static List<String> nationalTeams = Arrays.asList(
			"WSN", "NYM", "MIA", "PHI", "ATL",
			"CHC", "STL", "PIT", "MIL", "CIN",
			"LAD", "SFG", "COL", "ARI", "SDP");

	public static boolean isNational(String team) {
		return (nationalTeams.contains(team));
	}
	
	public static boolean isAmerican(String team) {
		return (americanTeams.contains(team));
	}
	
	public final static List<String> eastTeams = Arrays.asList(
			"BOS", "BAL", "TOR", "NYY", "TBR",
			"WSN", "NYM", "MIA", "PHI", "ATL");

	public final static List<String> centralTeams = Arrays.asList(
			"CLE", "DET", "KCR", "CHW", "MIN",
			"CHC", "STL", "PIT", "MIL", "CIN");
	
	public final static List<String> westTeams = Arrays.asList(
			"TEX", "SEA", "HOU", "LAA", "OAK",
			"LAD", "SFG", "COL", "ARI", "SDP");
	
	public static boolean isEast(String team) {
		return (eastTeams.contains(team));
	}
	
	public static boolean isCentral(String team) {
		return (centralTeams.contains(team));
	}
	
	public static boolean isWest(String team) {
		return (westTeams.contains(team));
	}
}
