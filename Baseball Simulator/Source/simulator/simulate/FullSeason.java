/*======================================

Project:       Baseball Simulator
File:          FullSeason.java
Author:        Braden Franksen
Date:          Mar 24, 2017

======================================*/
package simulator.simulate;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Scanner;

import simulator.game.Game;
import simulator.league.League;
import simulator.stats.GameStats;
import simulator.stats.SeasonStats;
import simulator.teams.Teams;

/**
 * @author Braden
 *
 */
public class FullSeason {
	
	public static ArrayList<Teams> amTeams = new ArrayList<>();
	public static ArrayList<Teams> naTeams = new ArrayList<>();
	private static ArrayList<Teams> tempList = new ArrayList<>();
	public static int num = 0;
	
	public static void runGames() throws IOException {
		League.playLeagueGames();
		League.createDivisions();
		GameStats.clearFiles();
		System.out.println("Playing games...");
		// 2430
		while(num < 243 && League.games[num] != null) {
			Game.away = League.leagueTeams.get(League.leagueTeamStrings.indexOf(League.games[num].substring(0, 3)));
			Game.home = League.leagueTeams.get(League.leagueTeamStrings.indexOf(League.games[num].substring(6, 9)));
			if(Game.away != null && Game.home != null) {
				Game.awayName = Game.away.tName;
				Game.awayAcronym = Game.away.tAcronym;
				Game.homeName = Game.home.tName;
				Game.homeAcronym =Game.home.tAcronym;
				Game.playGame(Game.away, Game.home);
				GameStats.readGamelog();
	    		Game.revertPlayerPositions(Game.away);
	    		Game.revertPlayerPositions(Game.home);
	            Game.clearGameStats(Game.away);
	            Game.clearGameStats(Game.home);
			}
			num++;
		}
		System.out.println("Done.\n");
	}
	
	public static void getStatsAfterSeason() throws IOException {
		Scanner stats = new Scanner(System.in);
		String bluh = "";
		
		while(!bluh.equalsIgnoreCase("quit")) { 
			System.out.println("\nWhat would you like to do now? (Type the word into the console)");
			System.out.println("\n\t'Quit'\n\t'Standings'\n\t'Playoffs' (Simulates playoffs through World Series)\n\t'Season Statistics' (Type the team's abbreviation to see that team's individual stats, i.e. 'SFG')\n");
			bluh = stats.next().toUpperCase();
			if(League.leagueTeamStrings.contains(bluh)) {
				SeasonStats.printSeasonStats(League.leagueTeams.get(League.leagueTeamStrings.indexOf(bluh)));
			}
			else if(bluh.equalsIgnoreCase("all")) {
				for(Teams t : League.leagueTeams) {
					if(t != null && (t.tWins + t.tLosses) > 0) {
						SeasonStats.printSeasonStats(t);
					}
				}
			}
			else if(bluh.equalsIgnoreCase("standings")) {
				setStandings();
			}
			else if(bluh.equalsIgnoreCase("playoffs")) {
				Playoffs.getPlayoffTeams();
				Playoffs.playWildcardGames();
				Playoffs.playDivisionalSeries();
				Playoffs.playConferenceSeries();
				Playoffs.playWorldSeries();
				break;
			}
		}
		stats.close();
	}
	
	private static void setStandings() {
		String rightAlignFormat = "| %-22s | %6s | %6s | %6s |%s| %-22s | %6s | %6s | %6s |%n";
		DecimalFormat dF = new DecimalFormat("#.000");
		String spacer = "=====";
		Teams amTeam = null, naTeam = null;
		setEastStandings();
		setCentStandings();
		setWestStandings();
		System.out.format("+------------------------+--------+--------+--------+=====+------------------------+--------+--------+--------+%n");
		System.out.format("|  American League       |   Wins |  Loss  |   WPct |=====|  National League       |   Wins |  Loss  |   WPct |%n");
		System.out.format("+------------------------+--------+--------+--------+=====+------------------------+--------+--------+--------+%n");
		System.out.format("|     East               |--------|--------|--------|=====|     East               |--------|--------|--------|%n");
		System.out.format("+------------------------+--------+--------+--------+=====+------------------------+--------+--------+--------+%n");
		for(int i = 0; i < 5; i++) {
			amTeam = League.amEast.get(i);
			naTeam = League.naEast.get(i);
			System.out.format(rightAlignFormat, amTeam.tName, amTeam.tWins, amTeam.tLosses, dF.format((double) amTeam.tWins / (amTeam.tWins + amTeam.tLosses)),
							  			spacer, naTeam.tName, naTeam.tWins, naTeam.tLosses, dF.format((double) naTeam.tWins / (naTeam.tWins + naTeam.tLosses)));
		}
		System.out.format("+------------------------+--------+--------+--------+=====+------------------------+--------+--------+--------+%n");
		System.out.format("|     Central            |--------|--------|--------|=====|     Central            |--------|--------|--------|%n");
		System.out.format("+------------------------+--------+--------+--------+=====+------------------------+--------+--------+--------+%n");
		for(int i = 0; i < 5; i++) {
			amTeam = League.amCent.get(i);
			naTeam = League.naCent.get(i);
			System.out.format(rightAlignFormat, amTeam.tName, amTeam.tWins, amTeam.tLosses, dF.format((double) amTeam.tWins / (amTeam.tWins + amTeam.tLosses)),
							  			spacer, naTeam.tName, naTeam.tWins, naTeam.tLosses, dF.format((double) naTeam.tWins / (naTeam.tWins + naTeam.tLosses)));
		}
		System.out.format("+------------------------+--------+--------+--------+=====+------------------------+--------+--------+--------+%n");
		System.out.format("|     West               |--------|--------|--------|=====|     West               |--------|--------|--------|%n");
		System.out.format("+------------------------+--------+--------+--------+=====+------------------------+--------+--------+--------+%n");
		for(int i = 0; i < 5; i++) {
			amTeam = League.amWest.get(i);
			naTeam = League.naWest.get(i);
			System.out.format(rightAlignFormat, amTeam.tName, amTeam.tWins, amTeam.tLosses, dF.format((double) amTeam.tWins / (amTeam.tWins + amTeam.tLosses)),
							  			spacer, naTeam.tName, naTeam.tWins, naTeam.tLosses, dF.format((double) naTeam.tWins / (naTeam.tWins + naTeam.tLosses)));
		}
		System.out.format("+------------------------+--------+--------+--------+=====+------------------------+--------+--------+--------+%n");
	}

	private static void setEastStandings() {
		for(Teams a : League.amEast) {
			tempList.add(a);
		}
		for(Teams a : tempList) {
			League.amEast.remove(a);
		}
		while(tempList.size() > 0) {
			maxWins();
		}
		for(Teams n : League.naEast) {
			tempList.add(n);
		}
		for(Teams n : tempList) {
			League.naEast.remove(n);
		}
		while(tempList.size() > 0) {
			maxWins();
		}
	}
	
	private static void setCentStandings() {
		for(Teams a : League.amCent) {
			tempList.add(a);
		}
		for(Teams a : tempList) {
			League.amCent.remove(a);
		}
		while(tempList.size() > 0) {
			maxWins();
		}
		for(Teams n : League.naCent) {
			tempList.add(n);
		}
		for(Teams n : tempList) {
			League.naCent.remove(n);
		}
		while(tempList.size() > 0) {
			maxWins();
		}
	}
	
	private static void setWestStandings() {
		for(Teams a : League.amWest) {
			tempList.add(a);
		}
		for(Teams a : tempList) {
			League.amWest.remove(a);
		}
		while(tempList.size() > 0) {
			maxWins();
		}
		for(Teams n : League.naWest) {
			tempList.add(n);
		}
		for(Teams n : tempList) {
			League.naWest.remove(n);
		}
		while(tempList.size() > 0) {
			maxWins();
		}
	}
	
	private static void maxWins() {
		double max = 0;
		Teams maxTeam = tempList.get(0);
		for(Teams t : tempList) {
			if(t.tWins > max && tempList.contains(t)) {
				max = t.tWins;
				maxTeam = t;
			}
		}
		tempList.remove(maxTeam);
		if(Teams.isAmerican(maxTeam.tAcronym)) {
			if(Teams.isEast(maxTeam.tAcronym)) {
				League.amEast.add(maxTeam);
			}
			else if(Teams.isCentral(maxTeam.tAcronym)) {
				League.amCent.add(maxTeam);
			}
			else {
				League.amWest.add(maxTeam);
			}
		}
		else {
			if(Teams.isEast(maxTeam.tAcronym)) {
				League.naEast.add(maxTeam);
			}
			else if(Teams.isCentral(maxTeam.tAcronym)) {
				League.naCent.add(maxTeam);
			}
			else {
				League.naWest.add(maxTeam);
			}
		}
	}

}
