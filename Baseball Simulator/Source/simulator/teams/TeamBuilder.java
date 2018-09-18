/*======================================

Project:       Baseball Simulator
File:          TeamBuilder.java
Author:        Braden Franksen
Date:          Mar 14, 2017

======================================*/
package simulator.teams;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import simulator.players.Player;

/**
 * @author Braden
 *
 */
public class TeamBuilder {

	static Scanner in;
	public static String tName = "", tAcronym = "", position = "", name = "", LR = "",role = "";
	public static int tYear = 0;
	public static String normalAmericanPath = "./TeamRosters/2016/American/";
	public static String normalNationalPath = "./TeamRosters/2016/National/";
	
	public TeamBuilder() {}
	
	public void createTeamScanner(String acronym) throws FileNotFoundException {
		try {
			if(Teams.isAmerican(acronym)) {
				in = new Scanner(new File(normalAmericanPath + acronym));
			}
			else {
				in = new Scanner(new File(normalNationalPath + acronym));
			}
		}
		catch (Exception e) {
			return;
		}
	}
	
	public Teams buildTeam(String acronym) throws FileNotFoundException {
		createTeamScanner(acronym);
		if(in.hasNext()) {
			getTeamInfo(in);
			Teams team = new Teams();
			team.setInfo(tName, tAcronym, tYear);
			in.nextLine();
			// builds roster without pitchers
			for(;;) {
				role = in.next();
				if(role.equals("*Pitching")) {
					break;
				}
				else if(role.equals("*Bench")) {
					in.nextLine();
				}
				else {
					position = in.next();
					name = (in.next().replaceAll("-", " ") + " " + in.next().replaceAll("-", " "));					
					LR = in.next();
					Player p = new Player(position, name, LR, role);
					setBattingStats(p);
					team.addPlayer(p);
					in.nextLine();
				}
			}
			in.nextLine();
			// finishes roster with pitchers
			while(in.hasNextLine()) {
				role = in.next();
				if(role.equals("*Bullpen")) {
					in.nextLine();
				}
				else {
					position = in.next();
					name = (in.next().replaceAll("-", " ") + " " + in.next().replaceAll("-", " "));
					LR = in.next();
					Player p = new Player(position, name, LR, role);
					setPitchingStats(p);
					team.addPlayer(p);
					if(in.hasNextLine())
						in.nextLine();
				}
			}
			in.close();
			team.setAverages();
			return team;
		}
		in.close();
		return null;
	}
	
	public void getTeamInfo(Scanner in) throws FileNotFoundException {
		String name1, name2, name3 = "";
		tAcronym = in.next();
		tYear = in.nextInt();
		name1 = in.next();
		name2 = in.next();
		name3 = in.next();
		if(!name3.equals("B"))
			tName = name1 + " " + name2 + " " + name3;
		else {
			tName = name1 + " " + name2;
		}
	}
	
	public void setBattingStats(Player p) {
		p.age = in.nextInt();
		p.bG = in.nextDouble();
		p.bPA = in.nextDouble();
		p.bAB = in.nextDouble();
		p.bR = in.nextDouble();
		p.bH = in.nextDouble();
		p.b2B = in.nextDouble();
		p.b3B = in.nextDouble();
		p.bHR = in.nextDouble();
		p.bRBI = in.nextDouble();
		p.bSB = in.nextDouble();
		p.bCS = in.nextDouble();
		p.bBB = in.nextDouble();
		p.bK = in.nextDouble();
		p.bAVG = in.nextDouble();
		p.bOBP = in.nextDouble();
		p.bSLG = in.nextDouble();
		p.bOPS = in.nextDouble();
		p.bOPSplus = in.nextDouble();
		p.bTB = in.nextDouble();
		p.bGDP = in.nextDouble();
		p.bHBP = in.nextDouble();
		p.bSH = in.nextDouble();
		p.bSF = in.nextDouble();
		p.bIBB = in.nextDouble();
		if(in.hasNextDouble()) {
			p.fPO = in.nextDouble();
			p.fA = in.nextDouble();
			p.fE = in.nextDouble();
		}
	}
	
	public void setPitchingStats(Player p) {
		in.next();
		p.pW = in.nextDouble();
		p.pL = in.nextDouble();
		for(int i = 0; i < 2; i++) {
			in.next();
		}
		p.pG = in.nextDouble();
		for(int i = 0; i < 4; i++) {
			in.next();
		}
		p.pS = in.nextDouble();
		p.pIP = in.nextDouble();
		p.pH = in.nextDouble();
		p.pR = in.nextDouble();
		p.pER = in.nextDouble();
		p.pHR = in.nextDouble();
		p.pBB = in.nextDouble();
		in.next();
		p.pK = in.nextDouble();
		p.pHBP = in.nextDouble();
		in.next();
		p.pWP = in.nextDouble();
		p.pBF = in.nextDouble();
		for(int i = 0; i < 8; i++) {
			in.next();
		}
	}
}
