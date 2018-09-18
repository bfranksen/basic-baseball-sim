/*======================================

Project:       Baseball Simulator
File:          TestLeague.java
Author:        Braden Franksen
Date:          Mar 20, 2017

======================================*/
package tests;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;

import org.junit.Test;

import simulator.league.League;
import simulator.teams.Teams;

/**
 * @author Braden
 *
 */
public class TestLeague {

	@Test
	public void testCreateLeague() throws FileNotFoundException {
		League.createLeague();
		for(int i = 0; i < League.leagueTeams.size(); i++) {
			if(League.leagueTeams.get(i) == null) {}
			else{
				assertEquals(League.leagueTeamStrings.get(i), League.leagueTeams.get(i).tAcronym);
				assertEquals(League.leagueTeamStrings.size(), League.leagueTeams.size());
				assertEquals(League.leagueTeams.get(League.leagueTeamStrings.indexOf(League.leagueTeamStrings.get(i))).tName, League.leagueTeams.get(i).tName); 
				Teams team = League.leagueTeams.get(League.leagueTeamStrings.indexOf(League.leagueTeamStrings.get(i)));
				assertEquals(team.roster, League.leagueTeams.get(i).roster);
				assertEquals(team.bench, League.leagueTeams.get(i).bench);
				assertEquals(team.bullpen, League.leagueTeams.get(i).bullpen);
			}
		}
	}
}
