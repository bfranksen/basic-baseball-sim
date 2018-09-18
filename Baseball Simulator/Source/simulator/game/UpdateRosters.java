/*======================================

Project:       Baseball Simulator
File:          UpdateRosters.java
Author:        Braden Franksen
Date:          Mar 24, 2017

======================================*/
package simulator.game;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import simulator.players.Player;
import simulator.teams.Teams;

/**
 * @author Braden
 *
 */
public class UpdateRosters {

	private static Teams away, home;
	private static String americanPath = "./UpdateTeamRosters/2016/American/";
	private static String nationalPath = "./UpdateTeamRosters/2016/National/";
	private static  String nextLine;
	private static BufferedWriter output;
	private static File file;
	
	public static void updateSeasonStats() throws IOException {
		away = Game.away;
		if(Teams.isAmerican(away.tAcronym))
			file = new File(americanPath + away.tAcronym);
		else 
			file = new File(nationalPath + away.tAcronym);
		output = new BufferedWriter(new FileWriter(file));
		updateStats(away);
		updateRoster(away);
		output.close();
		home = Game.home;
		if(Teams.isAmerican(home.tAcronym))
			file = new File(americanPath + home.tAcronym);
		else 
			file = new File(nationalPath + home.tAcronym);
		output = new BufferedWriter(new FileWriter(file));
		updateStats(home);
		updateRoster(home);
		output.close();
	}
	
	private static void updateStats(Teams team) {
		
	}
	
	private static void updateRoster(Teams team) throws IOException {
		nextLine = team.tAcronym + " " + team.tYear + " " + team.tName + " ";
		nextLine += "B	Age	G	PA	AB	R	H	2B	3B	HR	RBI	SB	CS	BB	SO	BA		OBP		SLG		OPS		OPS+ TB	GDP	HBP	SH	SF	IBB	PO	 A	 E\n";
		for(int i = 0; i < 8; i++) {
			Player p = team.roster.get(i);
			nextLine += p.role + " " + p.position + " " + p.name + " " + p.LR + " " + p.age + " ";
			nextLine += p.sbG + " " + p.sbPA + " " + p.sbAB + " " + p.sbR + " " + p.sbH + " " + p.sb2B + " " + p.sb3B + " " + p.sbHR + " ";
			nextLine += p.sbRBI + " " + p.sbSB + " " + p.sbCS + " " + p.sbBB + " " + p.sbAVG + " " + p.sbOBP + " " + p.sbSLG + " ";
			nextLine += p.sbOPS + " " + p.sbOPSplus + " " + p.sbTB + " " + p.sbGDP + " " + p.sbHBP + " " + p.sbSH + " " + p.sbSF + " " + p.sbIBB + " ";
			nextLine += p.sfPO + " " + p.sfA + " " + p.sfE + "\n";
		}
		output.write(nextLine);
	}
}
