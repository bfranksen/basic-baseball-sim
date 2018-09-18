/*======================================

Project:       Baseball Simulator
File:          SeasonStats.java
Author:        Braden Franksen
Date:          Mar 19, 2017

======================================*/
package simulator.stats;

import java.text.DecimalFormat;
import java.util.ArrayList;

import simulator.players.Player;
import simulator.teams.Teams;
import simulator.utils.Quicksort;

/**
 * @author Braden
 *
 */
public class SeasonStats {
	
	public static double stbAB = 0, stbR = 0, stbH = 0, stbRBI = 0, stbBB = 0, stbK = 0, stbHR = 0;
	public static double stpIP = 0, stpR = 0, stpH = 0, stpER = 0, stpBB = 0, stpK = 0, stpHR = 0, stpTO = 0;
	private static ArrayList<Player> tempList = new ArrayList<>();

	public static void printSeasonStats(Teams team) {
		DecimalFormat avgF = new DecimalFormat(".000");
		String rightAlignFormat = "| %-30s | %5s | %5s | %5s | %5s | %5s | %5s | %5s | %6s |%n";
		System.out.println("\n" + team.toString() + " Season Statistics --- Wins: " + team.tWins + " - Losses: " + team.tLosses);
		// Season batting stats
		for(Player p : team.roster) {
			if((p.sbAB + p.sbBB) > 0) {
				if(!team.seasonBatters.contains(p)) {
					team.seasonBatters.add(p);
				}
			}
		}
		System.out.format("+--------------------------------+-------+-------+-------+-------+-------+-------+-------+--------+%n");
		System.out.format("|    BATTING                     |    AB |     R |     H |   RBI |    BB |     K |    HR |    AVG |%n");
		System.out.format("+--------------------------------+-------+-------+-------+-------+-------+-------+-------+--------+%n");
		stbAB = stbR = stbH = stbRBI = stbBB = stbK = stbHR = 0;
		Quicksort.quickSort(team.seasonBatters, 0, team.seasonBatters.size() - 1);
		// batter only section
		for(Player p : team.seasonBatters) {
			if((p.sbAB + p.sbBB) > 0 && !p.position.equals("P")) {
				if(p.position.startsWith("DH")) {
					p.prePosition = p.position;
					p.position = "DH";
				}
				System.out.format(rightAlignFormat, p.name + " (" + p.position + ")", " " + (int) p.sbAB," " +  (int) p.sbR," " +  (int) p.sbH," " +  (int) p.sbRBI," " +  (int) p.sbBB," " +  (int) p.sbK, " " + (int) p.sbHR, avgF.format(p.sbH / p.sbAB));
				// batter only totals
				stbAB += p.sbAB;
				stbR += p.sbR;
				stbH += p.sbH;
				stbRBI += p.sbRBI;
				stbBB += p.sbBB;
				stbK += p.sbK;
				stbHR += p.sbHR;
			}
		}
		System.out.format("+--------------------------------+-------+-------+-------+-------+-------+-------+-------+--------+%n");
		System.out.format(rightAlignFormat, "     w/o 'P' TOTALS", (int) stbAB, " " + (int) stbR," " + (int) stbH, " " + (int) stbRBI, " " + (int) stbBB, " " + (int) stbK, " " + (int) stbHR, avgF.format(stbH / stbAB));
		System.out.format("+--------------------------------+-------+-------+-------+-------+-------+-------+-------+--------+%n");
		// add pitchers
		for(Player p : team.seasonBatters) {
			if((p.sbAB + p.sbBB) > 0 && p.position.equals("P")) {
				System.out.format(rightAlignFormat, p.name + " (" + p.position + ")", " " + (int) p.sbAB," " +  (int) p.sbR," " +  (int) p.sbH," " +  (int) p.sbRBI," " +  (int) p.sbBB," " +  (int) p.sbK, " " + (int) p.sbHR, avgF.format(p.sbH / p.sbAB));
				// add pitchers to totals
				stbAB += p.sbAB;
				stbR += p.sbR;
				stbH += p.sbH;
				stbRBI += p.sbRBI;
				stbBB += p.sbBB;
				stbK += p.sbK;
				stbHR += p.sbHR;
			}
		}
		System.out.format("+--------------------------------+-------+-------+-------+-------+-------+-------+-------+--------+%n");
		System.out.format(rightAlignFormat, "     w/ 'P' TOTALS", (int) stbAB, " " + (int) stbR," " + (int) stbH, " " + (int) stbRBI, " " + (int) stbBB, " " + (int) stbK, " " + (int) stbHR, avgF.format(stbH / stbAB));
		System.out.format("+--------------------------------+-------+-------+-------+-------+-------+-------+-------+--------+%n");
		System.out.format("|=================================================================================================|%n");
		// Season pitching stats
		for(Player p : team.roster) {
			if((p.spIP + p.spBB + p.spH) > 0) {
				if(!team.seasonPitchers.contains(p)) {
					team.seasonPitchers.add(p);
				}
			}
		}
		DecimalFormat eraF = new DecimalFormat("#0.00");
		String strIP = "";
		System.out.format("+--------------------------------+-------+-------+-------+-------+-------+-------+-------+--------+%n");
		System.out.format("|    PITCHING                    |    IP |     H |     R |    ER |    BB |     K |    HR |    ERA |%n");
		System.out.format("+--------------------------------+-------+-------+-------+-------+-------+-------+-------+--------+%n");	
		stpIP = stpH = stpR = stpER = stpBB = stpK = stpHR = stpTO = 0;
		getMaxIP(team);
		for(Player p : team.seasonPitchers) {
			if((p.spIP + p.spBB + p.spH) > 0) {
				p.spIP = p.spTO / 3;
				strIP = Math.floorDiv((int) p.spTO, 3) + "." + (int) p.spTO % 3;
				System.out.format(rightAlignFormat, p, strIP, (int) p.spH, (int) p.spR, (int) p.spER, (int) p.spBB, (int) p.spK, (int) p.spHR, eraF.format(p.spER * 9 / p.spIP));
			}	
			// season pitching totals
			stpTO += p.spTO;
			stpIP += p.spIP;
			stpH += p.spH;
			stpR += p.spR;
			stpER += p.spER;
			stpBB += p.spBB;
			stpK += p.spK;
			stpHR += p.spHR;
		}
		strIP = "";
		System.out.format("+--------------------------------+-------+-------+-------+-------+-------+-------+-------+--------+%n");	
		strIP = Math.floorDiv((int) stpTO, 3) + "." + (int) stpTO % 3;
		System.out.format(rightAlignFormat, "            TOTALS", strIP, (int) stpH, (int) stpR, (int) stpER, (int) stpBB, (int) stpK, (int) stpHR, eraF.format(stpER * 9 / stpIP));
		System.out.format("+--------------------------------+-------+-------+-------+-------+-------+-------+-------+--------+%n");
		System.out.println();
	}

	public static void getMaxAB(Teams team) {
		for(Player p : team.seasonBatters) {
			tempList.add(p);
		}
		for(Player p: tempList) {
			team.seasonBatters.remove(p);
		}
		while(tempList.size() > 0) {
			maxAB(team);
		}
	}
	
	private static Player maxAB(Teams team) {
		double max = 0;
		Player maxAB = tempList.get(0);
		for(Player p : tempList) {
			if(p.sbAB > max && tempList.contains(p)) {
				max = p.sbAB;
				maxAB = p;
			}
		}
		tempList.remove(maxAB);
		team.seasonBatters.add(maxAB);
		return maxAB;
	}

	public static void getMaxIP(Teams team) {
		for(Player p : team.seasonPitchers) {
			tempList.add(p);
		}
		for(Player p: tempList) {
			team.seasonPitchers.remove(p);
		}
		while(tempList.size() > 0) {
			maxIP(team);
		}
	}

	private static Player maxIP(Teams team) {
		double max = 0;
		Player maxIP = tempList.get(0);
		for(Player p : tempList) {
			if(p.spIP > max && tempList.contains(p)) {
				max = p.spIP;
				maxIP = p;
			}
		}
		tempList.remove(maxIP);
		team.seasonPitchers.add(maxIP);
		return maxIP;
	}
}
