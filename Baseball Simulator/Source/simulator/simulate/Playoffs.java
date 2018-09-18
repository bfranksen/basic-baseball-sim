/*======================================

Project:       Baseball Simulator
File:          Playoffs.java
Author:        Braden Franksen
Date:          Mar 30, 2017

======================================*/
package simulator.simulate;

import java.io.IOException;
import java.util.ArrayList;

import simulator.game.Game;
import simulator.league.League;
import simulator.stats.GameStats;
import simulator.teams.Teams;

/**
 * @author Braden
 *
 */
public class Playoffs {

	private static ArrayList<Teams> amPlayoffTeams = new ArrayList<>();
	private static ArrayList<Teams> naPlayoffTeams = new ArrayList<>();
	private static Teams amFirst, amSecond, amThird, amFourth, amFifth;
	private static Teams naFirst, naSecond, naThird, naFourth, naFifth;
	private static Teams amWC, naWC, amDS1, amDS2, naDS1, naDS2, amCS, naCS, tWS;
	private static Teams temp = null, seed = null, winner;
	
	public static void getPlayoffTeams() {
		Teams placeHolder = null;
		for(Teams t : League.amEast) {
			placeHolder = getMaxWins(t);
		}
		amPlayoffTeams.add(placeHolder);
		temp = null;
		for(Teams t : League.amCent) {
			placeHolder = getMaxWins(t);
		}
		temp = null;
		amPlayoffTeams.add(placeHolder);
		for(Teams t : League.amWest) {
			placeHolder = getMaxWins(t);
		}
		temp = null;
		amPlayoffTeams.add(placeHolder);
		for(Teams t : League.naEast) {
			placeHolder = getMaxWins(t);
		}
		naPlayoffTeams.add(placeHolder);
		temp = null;
		for(Teams t : League.naCent) {
			placeHolder = getMaxWins(t);
		}
		temp = null;
		naPlayoffTeams.add(placeHolder);
		for(Teams t : League.naWest) {
			placeHolder = getMaxWins(t);
		}
		temp = null;
		naPlayoffTeams.add(placeHolder);
		setDivisionalLeaders();
		temp = null;
		for(int i = 0; i < 2; i++) {
			for(Teams t : League.leagueTeams) {
				if(Teams.isAmerican(t.tAcronym) && !amPlayoffTeams.contains(t) && !naPlayoffTeams.contains(t)) {
					placeHolder = getMaxWins(t);
				}
			}
			temp = null;
			amPlayoffTeams.add(placeHolder);
		}
		for(int i = 0; i < 2; i++) {
			for(Teams t : League.leagueTeams) {
				if(Teams.isNational(t.tAcronym) && !naPlayoffTeams.contains(t) && !amPlayoffTeams.contains(t)) {
					placeHolder = getMaxWins(t);
				}
			}
			temp = null;
			naPlayoffTeams.add(placeHolder);
		}
		amPlayoffTeams.remove(amFirst);
		amPlayoffTeams.remove(amSecond);
		amPlayoffTeams.remove(amThird);
		naPlayoffTeams.remove(naFirst);
		naPlayoffTeams.remove(naSecond);
		naPlayoffTeams.remove(naThird);
		setWildcards();
	}

	private static void setDivisionalLeaders() {
		for(int i = 0; i < 3; i++) {
			temp = null;
			seed = null;
			for(Teams t : amPlayoffTeams) {
				seed = getMaxWins(t);
			}
			if(i == 0)
				amFirst = seed;
			else if(i == 1)
				amSecond = seed;
			else if(i == 2)
				amThird = seed;
			amPlayoffTeams.remove(seed);
		}
		amPlayoffTeams.add(amFirst);
		amPlayoffTeams.add(amSecond);
		amPlayoffTeams.add(amThird);
		for(int i = 0; i < 3; i++) {
			temp = null;
			seed = null;
			for(Teams t : naPlayoffTeams) {
				seed = getMaxWins(t);
			}
			if(i == 0)
				naFirst = seed;
			else if(i == 1)
				naSecond = seed;
			else if(i == 2)
				naThird = seed;
			naPlayoffTeams.remove(seed);
		}
		naPlayoffTeams.add(naFirst);
		naPlayoffTeams.add(naSecond);
		naPlayoffTeams.add(naThird);
	}
	
	private static void setWildcards() {
		for(int i = 0; i < 2; i++) {
			temp = null;
			seed = null;
			for(Teams t : amPlayoffTeams) {
				seed = getMaxWins(t);
			}
			if(i == 0)
				amFourth = seed;
			else if(i == 1)
				amFifth = seed;
			amPlayoffTeams.remove(seed);
		}
		for(int i = 0; i < 2; i++) {
			temp = null;
			seed = null;
			for(Teams t : naPlayoffTeams) {
				seed = getMaxWins(t);
			}
			if(i == 0)
				naFourth = seed;
			else if(i == 1)
				naFifth = seed;
			naPlayoffTeams.remove(seed);
		}
		System.out.println(amFirst + "   " + amSecond + "   " + amThird + "   " + amFourth + "   " + amFifth);
		System.out.println(naFirst + "   " + naSecond + "   " + naThird + "   " + naFourth + "   " + naFifth);
	}
	
	private static Teams getMaxWins(Teams team) {
		if(temp == null)
			temp = team;
		else if(team.tWins > temp.tWins) {
			temp = team;
		}
		return temp;
	}
	
	private static void playPlayoffGame(Teams away, Teams home) throws IOException {
		winner = null;
		Game.playGame(away, home);
		winner = Game.getWinner();
		GameStats.readGamelog();
		Game.revertPlayerPositions(away);
		Game.revertPlayerPositions(home);
        Game.clearGameStats(away);
        Game.clearGameStats(home);
	}
	
	public static void playWildcardGames() throws IOException {
		printPreBracket();
		playPlayoffGame(amFifth, amFourth);
		amWC = winner;
		playPlayoffGame(naFifth, naFourth);
		naWC = winner;
		printPostWCBracket();
	}
	
	private static void printPreBracket() {
		System.out.println("\n     " + amFirst.tAcronym + "    \\_________ ");
		System.out.println("   " + amFourth.tAcronym + "/" + amFifth.tAcronym + "  /         \\_________ ");
		System.out.println("     " + amSecond.tAcronym + " " + "   \\_________/         \\");
		System.out.println("     " + amThird.tAcronym + " " + "   /                    \\_____________");
		System.out.println("     " + naFirst.tAcronym + "    \\_________           /");
		System.out.println("   " + naFourth.tAcronym + "/" + naFifth.tAcronym +  "  /         \\_________/");
		System.out.println("     " + naSecond.tAcronym + " " + "   \\_________/ ");
		System.out.println("     " + naThird.tAcronym + " " + "   /\n");
	}
	
	private static void printPostWCBracket() {
		System.out.println("\n     " + amFirst.tAcronym + "    \\_________ ");
		System.out.println("     " + amWC.tAcronym + " " + "   /         \\_________");
		System.out.println("     " + amSecond.tAcronym + " " + "   \\_________/         \\");
		System.out.println("     " + amThird.tAcronym + " " + "   /                    \\_____________");
		System.out.println("     " + naFirst.tAcronym + "    \\_________           /");
		System.out.println("     " + naWC.tAcronym + " " + "   /         \\_________/");
		System.out.println("     " + naSecond.tAcronym + " " + "   \\_________/ ");
		System.out.println("     " + naThird.tAcronym + " " + "   /\n");
	}
	
	public static void playDivisionalSeries() throws IOException {
		for(int i = 0; i < 5; i++) {
			if(i == 0 || i == 1 || i == 4)
				playPlayoffGame(amWC, amFirst);
			else
				playPlayoffGame(amFirst, amWC);			
			winner.tSeriesWins++;
			if(winner.tSeriesWins == 3) {
				amDS1 = winner;
				break;
			}
		}
		for(int i = 0; i < 5; i++) {
			if(i == 0 || i == 1 || i == 4)
				playPlayoffGame(amThird, amSecond);
			else
				playPlayoffGame(amSecond, amThird);
			winner.tSeriesWins++;
			if(winner.tSeriesWins == 3) {
				amDS2 = winner;
				break;
			}
		}
		for(int i = 0; i < 5; i++) {
			if(i == 0 || i == 1 || i == 4)
				playPlayoffGame(naWC, naFirst);
			else
				playPlayoffGame(naFirst, naWC);			
			winner.tSeriesWins++;
			if(winner.tSeriesWins == 3) {
				naDS1 = winner;
				break;
			}
		}
		for(int i = 0; i < 5; i++) {
			if(i == 0 || i == 1 || i == 4)
				playPlayoffGame(naThird, naSecond);
			else
				playPlayoffGame(naSecond, naThird);
			winner.tSeriesWins++;
			if(winner.tSeriesWins == 3) {
				naDS2 = winner;
				break;
			}
		}
		printPostDSBracket();
	}
	
	private static void printPostDSBracket() {
		System.out.println("\n     " + amFirst.tAcronym + "    \\___" + amDS1.tAcronym + "___");
		System.out.println("     " + amWC.tAcronym + " " + "   /         \\_________");
		System.out.println("     " + amSecond.tAcronym + " " + "   \\___" + amDS2.tAcronym + "___/         \\");
		System.out.println("     " + amThird.tAcronym + " " + "   /                    \\_____________");
		System.out.println("     " + naFirst.tAcronym + "    \\___" + naDS1.tAcronym + "___           /");
		System.out.println("     " + naWC.tAcronym + " " + "   /         \\_________/");
		System.out.println("     " + naSecond.tAcronym + " " + "   \\___" + naDS2.tAcronym + "___/ ");
		System.out.println("     " + naThird.tAcronym + " " + "   /\n");
	}
	
	public static void playConferenceSeries() throws IOException {
		amDS1.tSeriesWins = amDS2.tSeriesWins = naDS1.tSeriesWins = naDS2.tSeriesWins = 0;
		for(int i = 0; i < 7; i++) {
			if(i == 0 || i == 1 || i == 5 || i == 6)
				playPlayoffGame(amDS2, amDS1);
			else
				playPlayoffGame(amDS1, amDS2);			
			winner.tSeriesWins++;
			if(winner.tSeriesWins == 4) {
				amCS = winner;
				break;
			}
		}
		for(int i = 0; i < 7; i++) {
			if(i == 0 || i == 1 || i == 5 || i == 6)
				playPlayoffGame(naDS2, naDS1);
			else
				playPlayoffGame(naDS1, naDS2);
			winner.tSeriesWins++;
			if(winner.tSeriesWins == 4) {
				naCS = winner;
				break;
			}
		}
		printPostCSBracket();
	}
	
	private static void printPostCSBracket() {
		System.out.println("\n     " + amFirst.tAcronym + "    \\___" + amDS1.tAcronym + "___");
		System.out.println("     " + amWC.tAcronym + " " + "   /         \\___" + amCS.tAcronym + "___");
		System.out.println("     " + amSecond.tAcronym + " " + "   \\___" + amDS2.tAcronym + "___/         \\");
		System.out.println("     " + amThird.tAcronym + " " + "   /                    \\_____________");
		System.out.println("     " + naFirst.tAcronym + "    \\___" + naDS1.tAcronym + "___           /");
		System.out.println("     " + naWC.tAcronym + " " + "   /         \\___" + naCS.tAcronym + "___/");
		System.out.println("     " + naSecond.tAcronym + " " + "   \\___" + naDS2.tAcronym + "___/ ");
		System.out.println("     " + naThird.tAcronym + " " + "   /\n");
	}
	
	public static void playWorldSeries() throws IOException {
		amCS.tSeriesWins = naCS.tSeriesWins = 0;
		for(int i = 0; i < 7; i++) {
			if(i == 0 || i == 1 || i == 5 || i == 6)
				playPlayoffGame(amCS, naCS);
			else
				playPlayoffGame(naCS, amCS);	
			winner.tSeriesWins++;
			if(winner.tSeriesWins == 4) {
				tWS = winner;
				break;
			}
		}
		printPostWSBracket();
	}
	
	public static Teams gettWS() {
		return tWS;
	}

	public static void settWS(Teams tWS) {
		Playoffs.tWS = tWS;
	}

	private static void printPostWSBracket() {
		System.out.println("\n     " + amFirst.tAcronym + "    \\___" + amDS1.tAcronym + "___");
		System.out.println("     " + amWC.tAcronym + " " + "   /         \\___" + amCS.tAcronym + "___");
		System.out.println("     " + amSecond.tAcronym + " " + "   \\___" + amDS2.tAcronym + "___/         \\");
		System.out.println("     " + amThird.tAcronym + " " + "   /                    \\_____" + tWS.tAcronym + "_____");
		System.out.println("     " + naFirst.tAcronym + "    \\___" + naDS1.tAcronym + "___           /");
		System.out.println("     " + naWC.tAcronym + " " + "   /         \\___" + naCS.tAcronym + "___/");
		System.out.println("     " + naSecond.tAcronym + " " + "   \\___" + naDS2.tAcronym + "___/ ");
		System.out.println("     " + naThird.tAcronym + " " + "   /\n");
	}
}
