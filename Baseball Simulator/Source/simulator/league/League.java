/*======================================

Project:       Baseball Simulator
File:          League.java
Author:        Braden Franksen
Date:          Mar 15, 2017

======================================*/
package simulator.league;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import simulator.simulate.FullSeason;
import simulator.teams.TeamBuilder;
import simulator.teams.Teams;

/**
 * @author Braden
 *
 */
public class League {
	
	private static Scanner leagueScanner, scheduleScanner, input;
	public static ArrayList<Teams> amEast = new ArrayList<>(), amCent = new ArrayList<>(), amWest = new ArrayList<>();
	public static ArrayList<Teams> naEast = new ArrayList<>(), naCent = new ArrayList<>(), naWest = new ArrayList<>();
    public static Map<String, List<String>> gamesOnDate = new LinkedHashMap<String, List<String>>();
    public static List<String> leagueTeamStrings = new ArrayList<String>();
    public static List<String> todaysGames = new ArrayList<String>();
	public static List<Teams> leagueTeams = new ArrayList<Teams>();
	public static int leagueSize = 0, numGames = 0;
    public double LIP = 0, LH = 0, L2B = 0, L3B = 0, LHR = 0, LBB = 0, LHBP = 0, LK = 0;
	public static String scheduleDate, scheduleGames, scheduleDateBefore;
	public static String[] games = new String[4860];
    
    public League() {}
    
	public static void playLeagueGames() throws IOException {
		createLeague();
		input = new Scanner(System.in);
		System.out.println("Which year do you want to play out? (2016 or 2017)");
		String year = input.next();
		createSchedule(year);	
		setTeamsForGameOnDate();
		scheduleScanner.close();
	}
	
    public static void createLeague() throws FileNotFoundException {
    	leagueTeamStrings.addAll(Teams.americanTeams);
    	leagueTeamStrings.addAll(Teams.nationalTeams);
    	TeamBuilder teamBuilder = new TeamBuilder();
    	leagueSize = leagueTeamStrings.size();
    	for(int i = 0; i < leagueSize; i++) {
			leagueTeams.add(teamBuilder.buildTeam(leagueTeamStrings.get(i)));
    	}
		for(Teams t : leagueTeams) {
			if(t != null) {
				if(Teams.isAmerican(t.tAcronym)) {
					FullSeason.amTeams.add(t);
				}
				else {
					FullSeason.naTeams.add(t);
				}
			}
		}
    }

	public static void createSchedule(String year) throws IOException {
		String path = "./Schedule/" + year + "Schedule";
		scheduleScanner = new Scanner(new File(path));
		int runs = 0;
		scheduleDate = "Sun";
		while(scheduleScanner.hasNextLine()) {
			if(scheduleDate.endsWith(year)) {
				scheduleDateBefore = scheduleDate;
			}
			if(scheduleDate == "Sun") {
				scheduleDate = scheduleScanner.nextLine();
				scheduleDateBefore = scheduleDate;
				scheduleScanner.nextLine();
			}
			else {
				scheduleDate = scheduleScanner.nextLine();
				scheduleScanner.nextLine();
			}
			scheduleGames = scheduleDate;
			if(runs > 0 && (scheduleDate.endsWith(year))){
				gamesOnDate.put(scheduleDateBefore, todaysGames);
				todaysGames = new ArrayList<String>();
				scheduleDateBefore = scheduleDate;

			}
			else if(runs > 0) {
				todaysGames.add(scheduleGames);
			}
			runs++;
		}
	}
	
	public static void setTeamsForGameOnDate() throws IOException {
		Set<String> gamesToday = gamesOnDate.keySet();
		for(int k = 0; k < 1; k++) {
			for(int i = 0; i < gamesOnDate.size(); i++) {
				for(int j = 0; j < gamesOnDate.get(gamesToday.toArray()[i]).size(); j++) {
					games[k++] = gamesOnDate.get(gamesToday.toArray()[i]).get(j);
				}
			}
		}
/*		for(String key : gamesToday) {
			//System.out.println("Key = " + key);
			//System.out.println("Values = " + gamesOnDate.get(key));
			for(String games : gamesOnDate.get(key)) {
				away = leagueTeams.get(leagueTeamStrings.indexOf(games.substring(0, 3)));
				home = leagueTeams.get(leagueTeamStrings.indexOf(games.substring(6, 9)));
				if(away != null && home != null) {
					awayName = away.tName;
					awayAcronym = away.tAcronym;
					homeName = home.tName;
					homeAcronym = home.tAcronym;
					Game.playGame(away, home);
				}
			}
		}*/
	}
	
	public static void createDivisions() {
		for(Teams t : leagueTeams) {
			if(t != null) {
				if(Teams.isAmerican(t.tAcronym)) {
					if(Teams.isEast(t.tAcronym)) {
						amEast.add(t);
					}
					else if(Teams.isCentral(t.tAcronym)) {
						amCent.add(t);
					}
					else {
						amWest.add(t);
					}
				}
				else {
					if(Teams.isEast(t.tAcronym)) {
						naEast.add(t);
					}
					else if(Teams.isCentral(t.tAcronym)) {
						naCent.add(t);
					}
					else {
						naWest.add(t);
					}
				}
			}
		}
	}
	
	public void leagueAvgs() throws FileNotFoundException {
		leagueScanner = new Scanner(new File("./LeaguePitchingStats/2016Stats"));
		LIP = leagueScanner.nextDouble(); leagueScanner.next();
		LH = leagueScanner.nextDouble(); leagueScanner.next();
		L2B = leagueScanner.nextDouble(); leagueScanner.next();
		L3B = leagueScanner.nextDouble(); leagueScanner.next();
		LHR = leagueScanner.nextDouble(); leagueScanner.next();
		LBB = leagueScanner.nextDouble(); leagueScanner.next();
		LHBP = leagueScanner.nextDouble(); leagueScanner.next();
		LK = leagueScanner.nextDouble(); leagueScanner.next();
		leagueScanner.close();
	}
}
