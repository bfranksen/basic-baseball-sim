/*======================================

Project:       Baseball Simulator
File:          GameStats.java
Author:        Braden Franksen
Date:          Mar 19, 2017

======================================*/
package simulator.stats;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Scanner;

import simulator.game.Game;
import simulator.players.Player;
import simulator.simulate.FullSeason;
import simulator.teams.Teams;

/**
 * @author Braden
 *
 */
public class GameStats {

    public static double gtbAB = 0, gtbR = 0, gtbH = 0, gtbRBI = 0, gtbBB = 0, gtbK = 0, gtbHR = 0;
    public static double gtpIP = 0, gtpR = 0, gtpH = 0, gtpER = 0, gtpBB = 0, gtpK = 0, gtpHR = 0, gtpTO = 0;
	private static String amc = "./SeasonGames/AmericanCentral/";
	private static String ame = "./SeasonGames/AmericanEast/";
	private static String amw = "./SeasonGames/AmericanWest/";
	private static String nac = "./SeasonGames/NationalCentral/";
	private static String nae = "./SeasonGames/NationalEast/";
	private static String naw = "./SeasonGames/NationalWest/";
	private static Scanner readGamelog;
	private static BufferedWriter output;
	private static File file;
   
    public GameStats() {}
    
	//private static void calculateWinLossSave() {
	// TODO	
	//}
    
	public static void clearFiles() {
		String[] myFiles;
		file = new File(amc);
		if(file.isDirectory()) {
			myFiles = file.list();
			for(int i = 0; i < myFiles.length; i++) {
				File myFile = new File(file, myFiles[i]);
				myFile.delete();
			}
		}
		file = new File(ame);
		if(file.isDirectory()) {
			myFiles = file.list();
			for(int i = 0; i < myFiles.length; i++) {
				File myFile = new File(file, myFiles[i]);
				myFile.delete();
			}
		}
		file = new File(amw);
		if(file.isDirectory()) {
			myFiles = file.list();
			for(int i = 0; i < myFiles.length; i++) {
				File myFile = new File(file, myFiles[i]);
				myFile.delete();
			}
		}
		file = new File(nac);
		if(file.isDirectory()) {
			myFiles = file.list();
			for(int i = 0; i < myFiles.length; i++) {
				File myFile = new File(file, myFiles[i]);
				myFile.delete();
			}
		}
		file = new File(nae);
		if(file.isDirectory()) {
			myFiles = file.list();
			for(int i = 0; i < myFiles.length; i++) {
				File myFile = new File(file, myFiles[i]);
				myFile.delete();
			}
		}
		file = new File(naw);
		if(file.isDirectory()) {
			myFiles = file.list();
			for(int i = 0; i < myFiles.length; i++) {
				File myFile = new File(file, myFiles[i]);
				myFile.delete();
			}
		}
	}
	
    public static void addGameStatsToSeason(Teams team) {
    	for(Player p : team.gameBatters) {
			// add player game stats to individual season stats
    		p.sbSB += p.gbSB;
    		p.sbCS += p.gbCS;
			p.sbAB += p.gbAB;
			p.sbR += p.gbR;
			p.sbH += p.gbH;
			p.sbRBI += p.gbRBI;
			p.sbBB += p.gbBB;
			p.sbK += p.gbK;
			p.sbHR += p.gbHR;
    	}
    	for(Player p : team.gamePitchers) {
			// add pitcher game stats to individual season stats
			p.spTO += p.gpTO;
			p.spIP += p.gpTO / 3;
			p.spH += p.gpH;
			p.spR += p.gpR;
			p.spER += p.gpER;
			p.spBB += p.gpBB;
			p.spK += p.gpK;
			p.spHR += p.gpHR;
    	}
    }
    
	public static void readGamelog() throws IOException {
		readGamelog = new Scanner(new File("gamelog.txt"));
		output = null;
		String line = "";
        try {
        	if(Teams.centralTeams.contains(Game.homeAcronym) && Teams.isAmerican(Game.homeAcronym)) {
        		file = new File(amc + Game.away.tYear + "_" + Game.away.tAcronym + "vs" + Game.home.tAcronym + "_" + FullSeason.num);
        	}
        	else if(Teams.eastTeams.contains(Game.homeAcronym) && Teams.isAmerican(Game.homeAcronym)) {
        		file = new File(ame + Game.away.tYear + "_" + Game.away.tAcronym + "vs" + Game.home.tAcronym + "_" + FullSeason.num);
        	}
        	else if(Teams.westTeams.contains(Game.homeAcronym) && Teams.isAmerican(Game.homeAcronym)) {
        		file = new File(amw + Game.away.tYear + "_" + Game.away.tAcronym + "vs" + Game.home.tAcronym + "_" + FullSeason.num);
        	}
        	else if(Teams.centralTeams.contains(Game.homeAcronym) && Teams.isNational(Game.homeAcronym)) {
        		file = new File(nac + Game.away.tYear + "_" + Game.away.tAcronym + "vs" + Game.home.tAcronym + "_" + FullSeason.num);
        	}
        	else if(Teams.eastTeams.contains(Game.homeAcronym) && Teams.isNational(Game.homeAcronym)) {
        		file = new File(nae + Game.away.tYear + "_" + Game.away.tAcronym + "vs" + Game.home.tAcronym + "_" + FullSeason.num);
        	}
        	else if(Teams.westTeams.contains(Game.homeAcronym) && Teams.isNational(Game.homeAcronym)) {
        		file = new File(naw + Game.away.tYear + "_" + Game.away.tAcronym + "vs" + Game.home.tAcronym + "_" + FullSeason.num);
        	}
            output = new BufferedWriter(new FileWriter(file));
            while(readGamelog.hasNextLine()) {
            	line = readGamelog.nextLine();
                output.write(line + "\n");
            }
            printLineScore();
            printBoxScore(Game.away);
            printBoxScore(Game.home);
        } 
        catch ( IOException e ) {
            e.printStackTrace();
        } 
        finally {
          if ( output != null ) {
            output.close();
          }
       }
       readGamelog.close();
	}
	
	public static void printLineScore() throws IOException {
		int innings = Game.inning - 1;
		int nameWidth = 24;
		int hits = 0;
		int errors = 0;
		output.write("\n\n");
		for(int i = 0; i < nameWidth; i++) {
			output.write(" ");
		}
		for(int i = 0; i < innings; i++) {
			if(i > 8) {
				output.write((i + 1) + "   ");
				if(i % 3 == 2) {
					output.write("   ");
				}
			}
			else {
				output.write((i + 1) + "  ");
				if(i % 3 == 2) {
					output.write("  ");
				}
			}
		}
		output.write("  R   H   E");
		output.write("\n");
		for(int i = 0; i < nameWidth; i++) {
			output.write(" ");
		}
		for(int i = 0; i < innings; i++) {
			if(i > 8) {
				output.write("--  ");
				if(i % 3 == 2) 
					output.write("  ");
			}
			else {
				output.write("-- ");
				if(i % 3 == 2) 
					output.write("  ");
			}
		}
		output.write("  -   -   -");
		int awayDiff = nameWidth - Game.awayName.length();
		output.write("\n" + Game.awayName);
		for(int i = 0; i < awayDiff; i++) {
			output.write(" ");
		}
		for(int i = 0; i < innings; i++) {
			output.write(Game.aInnRuns[i] + "  ");
			if(i % 3 == 2) {
				output.write("  ");
			}
		}
		if(Game.aRuns > 9)
			output.write("  " + Game.aRuns + "  ");
		else
			output.write("  " + Game.aRuns + "   ");
		for(Player p : Game.away.gameBatters) {
			hits += p.gbH;
			errors += p.gE;
		}
		if(hits > 9) 
			output.write(hits + "  " + errors);
		else
			output.write(hits + "   " + errors);
		int homeDiff = nameWidth - Game.homeName.length();
		output.write("\n" + Game.homeName);
		for(int i = 0; i < homeDiff; i++) {
			output.write(" ");
		}
		for(int i = 0; i < innings; i++) {
			output.write(Game.hInnRuns[i] + "  ");
			if(i % 3 == 2) {
				output.write("  ");
			}
		}
		hits = 0;
		errors = 0;
		if(Game.hRuns > 9)
			output.write("  " + Game.hRuns + "  ");
		else
			output.write("  " + Game.hRuns + "   ");
		for(Player p : Game.home.gameBatters) {
			hits += p.gbH;
			errors += p.gE;
		}
		if(hits > 9) 
			output.write(hits + "  " + errors);
		else
			output.write(hits + "   " + errors);		
		output.write("\n\n");
	}
	
	public static void printBoxScore(Teams team) throws IOException {
		DecimalFormat avgF = new DecimalFormat(".000");
		String bxLine;
		String rightAlignFormat = "| %-28s | %4s | %4s | %4s | %4s | %4s | %4s | %4s | %5s |%n";
		bxLine = String.format("\n" + team.tName + " Box Score\n");
		output.write(bxLine);
		// Batting boxscore
		bxLine = String.format("+------------------------------+------+------+------+------+------+------+------+-------+%n");
		output.write(bxLine);
		bxLine = String.format("|    BATTING                   |   AB |    R |    H |  RBI |   BB |    K |   HR |   AVG |%n");
		output.write(bxLine);
		bxLine = String.format("+------------------------------+------+------+------+------+------+------+------+-------+%n");
		output.write(bxLine);
		gtbAB = gtbR = gtbH = gtbRBI = gtbBB = gtbK = gtbHR = 0;
		for(Player p : team.gameBatters) {
			if(p.gbAB + p.gbBB > 0) {
				if(p.equals(Game.awayStarter) || p.equals(Game.homeStarter)) {
					bxLine = String.format(rightAlignFormat, p.name + " (" + p.position + ")", (int) p.gbAB, (int) p.gbR, (int) p.gbH, (int) p.gbRBI, (int) p.gbBB, (int) p.gbK, (int) p.gbHR, avgF.format(p.gbH / p.gbAB));
					output.write(bxLine);
				}
				else if (p.position.equals("P") || (!p.position.startsWith("DH") && p.role.equals("b"))) {
					if(!p.position.equals("P")) {
						if(!p.position.equals(p.prePosition))
							Game.revertPlayerPositions(team);
						p.prePosition = p.position;
						p.position = "PH";
					}
					bxLine = String.format(rightAlignFormat, "   " + p.name + " (" + p.position + ")", " " + (int) p.gbAB," " +  (int) p.gbR," " +  (int) p.gbH," " +  (int) p.gbRBI," " +  (int) p.gbBB," " +  (int) p.gbK, " " + (int) p.gbHR, avgF.format(p.gbH / p.gbAB));
					output.write(bxLine);
				}
				else {
					bxLine = String.format(rightAlignFormat, p.name + " (" + p.position + ")", " " + (int) p.gbAB," " +  (int) p.gbR," " +  (int) p.gbH," " +  (int) p.gbRBI," " +  (int) p.gbBB," " +  (int) p.gbK, " " + (int) p.gbHR, avgF.format(p.gbH / p.gbAB));
					output.write(bxLine);
				}
			}
			// game batting totals
			gtbAB += p.gbAB;
			gtbR += p.gbR;
			gtbH += p.gbH;
			gtbRBI += p.gbRBI;
			gtbBB += p.gbBB;
			gtbK += p.gbK;
			gtbHR += p.gbHR;
		}
		bxLine = String.format("+------------------------------+------+------+------+------+------+------+------+-------+%n");
		output.write(bxLine);
		bxLine = String.format(rightAlignFormat, "           TOTALS", (int) gtbAB, " " + (int) gtbR," " + (int) gtbH, " " + (int) gtbRBI, " " + (int) gtbBB, " " + (int) gtbK, " " + (int) gtbHR, avgF.format(gtbH / gtbAB));
		output.write(bxLine);
		bxLine = String.format("+------------------------------+------+------+------+------+------+------+------+-------+%n");
		output.write(bxLine);
		bxLine = String.format("|=======================================================================================|%n");
		output.write(bxLine);
		// Pitching boxscore
		DecimalFormat eraF = new DecimalFormat("#0.00");
		String strIP = "";
		bxLine = String.format("+------------------------------+------+------+------+------+------+------+------+-------+%n");
		output.write(bxLine);
		bxLine = String.format("|    PITCHING                  |   IP |    H |    R |   ER |   BB |    K |   HR |   ERA |%n");
		output.write(bxLine);
		bxLine = String.format("+------------------------------+------+------+------+------+------+------+------+-------+%n");
		output.write(bxLine);
		gtpIP = gtpH = gtpR = gtpER = gtpBB = gtpK = gtpTO = gtpHR = 0;
		for(Player p : team.gamePitchers) {	
			p.gpIP = p.gpTO / 3;
			strIP = Math.floorDiv((int) p.gpTO, 3) + "." + (int) p.gpTO % 3;
			bxLine = String.format(rightAlignFormat, p.name, strIP, (int) p.gpH, (int) p.gpR, (int) p.gpER, (int) p.gpBB, (int) p.gpK, (int) p.gpHR, eraF.format(p.gpER * 9 / p.gpIP));
			output.write(bxLine);
			// game pitching totals
			gtpTO += p.gpTO;
			gtpIP += p.gpIP;
			gtpH += p.gpH;
			gtpR += p.gpR;
			gtpER += p.gpER;
			gtpBB += p.gpBB;
			gtpK += p.gpK;
			gtpHR += p.gpHR;
		}
		strIP = Math.floorDiv((int) gtpTO, 3) + "." + (int) gtpTO % 3;
		bxLine = String.format("+------------------------------+------+------+------+------+------+------+------+-------+%n");
		output.write(bxLine);
		bxLine = String.format(rightAlignFormat, "           TOTALS", strIP, (int) gtpH, (int) gtpR, (int) gtpER, (int) gtpBB, (int) gtpK, (int) gtpHR, eraF.format(gtpER * 9 / gtpIP));
		output.write(bxLine);
		bxLine = String.format("+------------------------------+------+------+------+------+------+------+------+-------+%n");
		output.write(bxLine);
	}
}
