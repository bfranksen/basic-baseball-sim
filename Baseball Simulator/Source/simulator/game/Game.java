/*======================================

Project:       Baseball Simulaor
File:          Game.java
Author:        Braden Franksen
Date:          Mar 11, 2017

======================================*/
package simulator.game;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Random;

import simulator.league.League;
import simulator.players.Player;
import simulator.stats.GameStats;
import simulator.teams.Teams;

/**
 * @author Braden
 *
 */
public class Game {

    private static final int SINGLE = 0;
    private static final int DOUBLE = 1;
    private static final int TRIPLE = 2;
    private static final int HOMERUN = 3;
    private static final int WALK = 4;
    private static final int HIT_BY_PITCH = 5;
    private static final int STRIKEOUT = 6;
    private static final int OUT = 7;
    private static double BBB, B1, B2, B3, B4, BHBP, BK;
    private static double PBB, P1, P2, P3, P4, PHBP, PK;
    private static double LBF, LBB, L1, L2, L3, L4, LHBP, LK;
    private static double CBB, C1, C2, C3, C4, CHBP, CK;
	private static int outsBefore, outsBeforeSB, outsAfterSB, aSpot, hSpot;
	private static PrintWriter w;
	private static Random rand;
	public static String awayName = "", homeName = "", awayAcronym = "", homeAcronym = "";
	public static Teams away, home, winner;
    public static Player[] homeOrder, awayOrder, homeField, awayField;
    public static Player homeP, awayP, awayStarter, homeStarter;
    public static int[] hInnRuns, aInnRuns;
    public static int inning, aRuns, hRuns;
	
	public Game() {}

	/**
	 * Sets all pregame values based on away/home team and
	 * then runs one instance (game).
	 * @param init_away
	 * @param init_home
	 * @throws IOException
	 */
	public static void playGame(Teams init_away, Teams init_home) throws IOException {
		w = new PrintWriter(new File("gamelog.txt"));
		away = init_away;
		home = init_home;

		awayField = new Player[9];
		awayOrder = new Player[9];
		homeField = new Player[9];
		homeOrder = new Player[9];

		away.gameBatters = new ArrayList<Player>();
		away.gamePitchers = new ArrayList<Player>();
		home.gameBatters = new ArrayList<Player>();
		home.gamePitchers = new ArrayList<Player>();
		
		// ready start of game
		setStartingPitchers();
		setBattingOrders();
		printBattingOrder();
		buildFields();
		
		aRuns = hRuns = aSpot = hSpot = 0;
		inning = 1;

		printLog("----------------------------------------------------------------------------------------------------------\n");
		printLog("Welcome to Baseball Sim. Today's game is between the " + awayName + " and the " + homeName + ".\n");
		printLog("\tThe starting pitcher for the " + awayName + " is " + awayP + ".\n\tThe starting pitcher for the " + homeName + " is " + homeP + ".\n");
		printLog("----------------------------------------------------------------------------------------------------------\n");
		
		playInnings();
		GameStats.readGamelog();
		w.close();
		GameStats.addGameStatsToSeason(away);
		GameStats.addGameStatsToSeason(home);
		UpdateRosters.updateSeasonStats();
	}
	
	private static void setStartingPitchers() {
		Player tempP = null;
		tempP = away.bullpen.get(4);
		if((away.tWins + away.tLosses) % 16 == 5) {
			for(Player p : away.bullpen) {
				if(p.role.equals("m")) {
					if((p.spER * 9 / p.spIP) < (tempP.spER * 9 / tempP.spIP)) {
						tempP = p;
					}
				}
			}
			awayP = tempP;
		}
		else {
			awayP = away.bullpen.get(0 + (away.tWins + away.tLosses) % 5);
		}
		tempP = home.bullpen.get(4);
		if((home.tWins + home.tLosses) % 16 == 0) {
			for(Player p : home.bullpen) {
				if(p.role.equals("m")) {
					if((p.spER * 9 / p.spIP) < (tempP.spER * 9 / tempP.spIP)) {
						tempP = p;
					}
				}
			}
			homeP = tempP;
		}
		else {
			homeP = home.bullpen.get(0 + (home.tWins + home.tLosses) %  5);
		}
		awayStarter = awayP;
		homeStarter = homeP;
		away.gamePitchers.add(awayP);
		home.gamePitchers.add(homeP);
	}
	
	private static void setBattingOrders() {
		boolean aDH  = false, hDH = false, moreDH = false;
		Player natDH = null, awayDH = null, homeDH = null, temp = null;
		for(int i = 0; i < 9; i++) {
			if(i == 8 && Teams.isNational(homeAcronym)) {
				awayOrder[i] = awayP;
				homeOrder[i] = homeP;
			}
			else if(Teams.isNational(homeAcronym)) {
				awayOrder[i] = away.roster.get(i);
				homeOrder[i] = home.roster.get(i);
			}
			else {
				if(Teams.isAmerican(awayAcronym)) {
					if(Integer.valueOf(away.bench.get(0).position.substring(2, 3)).equals(i)) {
						awayOrder[i] = away.bench.get(0);
						awayDH = awayOrder[i];
						aDH = true;
					}
					else {
						if(aDH == true) {
							awayOrder[i] = away.roster.get(i - 1);
						}
						else {
							awayOrder[i] = away.roster.get(i);
						}
					}
					if(Integer.valueOf(home.bench.get(0).position.substring(2, 3)).equals(i)) {
						homeOrder[i] = home.bench.get(0);
						homeDH = homeOrder[i];
						hDH = true;
					}
					else {
						if(hDH == true) {
							homeOrder[i] = home.roster.get(i - 1);
						}
						else {
							homeOrder[i] = home.roster.get(i);
						}
					}
				}
				else {
					if(Integer.valueOf(home.bench.get(0).position.substring(2, 3)).equals(i)) {
						homeOrder[i] = home.bench.get(0);
						homeDH = homeOrder[i];
						hDH = true;
					}
					else {
						if(hDH == true) {
							homeOrder[i] = home.roster.get(i - 1);
						}
						else {
							homeOrder[i] = home.roster.get(i);
						}
					}
					int slot = 0;
					natDH = nationalDH(away);
					if(natDH.isFast() && natDH.bOBP > away.roster.get(0).bOBP)
						slot = 0;
					else if(natDH.isFast() && natDH.bOBP > away.roster.get(1).bOBP)
						slot = 1;
					else if(natDH.bOPS > away.roster.get(2).bOPS)
						slot = 3;
					else if(natDH.bSLG > away.roster.get(3).bSLG)
						slot = 4;
					else if(natDH.bOPS > away.roster.get(4).bOPS)
						slot = 5;
					else
						slot = 8;
					if(i == slot) {
						awayOrder[i] = natDH;
						temp = away.roster.get(i);
						moreDH = true;
					}
					else {
						if(moreDH == true) {
							if(temp.bOPS > away.roster.get(i).bOPS) {
								awayOrder[i] = temp;
								temp = null;
								moreDH = false;
							}
							else if(i == 8) {
								awayOrder[i] = temp;
							}
							else
								awayOrder[i] = away.roster.get(i);
						}
						else {
							if(temp == null) {
								if(i < slot) 
									awayOrder[i] = away.roster.get(i);
								else {
									awayOrder[i] = away.roster.get(i - 1);
								}
							}
							else {
								awayOrder[i] = away.roster.get(i);
							}
						}
					}
				}
			}
			away.gameBatters.add(awayOrder[i]);
			home.gameBatters.add(homeOrder[i]);
		}
		if(awayDH != null) {
			awayDH.prePosition = awayDH.position;
			awayDH.position = "DH";
		}
		if(homeDH != null) {
			homeDH.prePosition = homeDH.position;
			homeDH.position = "DH";
		}
		if(natDH != null) {
			natDH.prePosition = natDH.position;
			natDH.position = "DH";
		}
	}
	
	private static void printBattingOrder() {
  		printLog(awayName + " Batting Order:\n");
		for(int i = 0; i < 9; i++) {
			printLog("\t" + (i + 1) + ": " + awayOrder[i].name + " (" + awayOrder[i].position + ")\n");
		}
		printLog("\n" + homeName + " Batting Order:\n");
		for(int i = 0; i < 9; i++) {
			printLog("\t" + (i + 1) + ": " + homeOrder[i].name + " (" + homeOrder[i].position + ")\n");
		}
	}
	
	private static Player nationalDH(Teams team) {
		double max = 0;
		Player temp = null;
		for(Player p : team.bench) {
			if(p.bOPS > max) {
				max = p.bOPS;
				temp = p;
			}
		}
		temp.prePosition = temp.position;
		temp.position = "DH";
		return temp;
	}

	public static void revertPlayerPositions(Teams team) {
		for(Player p : team.roster) {
			if(!p.position.equals(p.prePosition) && p.prePosition != null) {
				p.position = p.prePosition;
			}
		}
	}
	
	private static void playInnings() throws FileNotFoundException {
		Field diamond = new Field(w);
		aInnRuns = new int[99];
		hInnRuns = new int[99];
		while(aRuns == hRuns || inning < 10) {
			aInnRuns[inning - 1] = 0;
			printLog("\nTop of " + inning + ". " + awayAcronym + ": " + aRuns + " - " + homeAcronym + ": " + hRuns + "\n\n");
			checkBatterForSub(away, awayOrder[aSpot]);
			checkPitcherForSub(homeP);
			diamond.resetField(homeField);
			while((outsBefore = diamond.getOuts()) < 3) {
				checkBatterForSub(away, awayOrder[aSpot]);
				printLog(awayOrder[aSpot] + " is up to bat.\n");
				outsBeforeSB = diamond.getOuts();
				aRuns += diamond.updateStolenBase(awayOrder[aSpot]);
				outsAfterSB = diamond.getOuts();
				diamond.calcPitchCount();
				if(outsBeforeSB == outsAfterSB) {
					aRuns += diamond.updateField(matchup(homeP, awayOrder[aSpot], calcBattingAdjustment(homeP, awayOrder[aSpot])), awayOrder[aSpot]);
					diamond.calcPitchCount();
					getFieldOuts(diamond);
					if(diamond.getOuts() != 3)
						checkPitcherForSub(homeP);
					increment_aSpot();
				}
				if(diamond.getOuts() == 3) {
					diamond.clearBasepaths();
				}
			}
			if(hRuns > aRuns && inning > 8) {
				inning++;
				break;
			}
			else {
				hInnRuns[inning - 1] = 0;
				printLog("\nBottom of " + inning + ". " + awayAcronym + ": " + aRuns + " - " + homeAcronym + ": " + hRuns + "\n\n");
				checkBatterForSub(home, homeOrder[hSpot]);
				checkPitcherForSub(awayP);
				diamond.resetField(awayField);
			}
			while((outsBefore = diamond.getOuts()) < 3) {
				if(hRuns > aRuns && inning > 8)
					break;
				checkBatterForSub(home, homeOrder[hSpot]);
				printLog(homeOrder[hSpot] + " is up to bat.\n");
				outsBeforeSB = diamond.getOuts();
				hRuns += diamond.updateStolenBase(homeOrder[hSpot]);
				outsAfterSB = diamond.getOuts();
				diamond.calcPitchCount();
				if(outsBeforeSB == outsAfterSB) {
					hRuns += diamond.updateField(matchup(awayP, homeOrder[hSpot], calcBattingAdjustment(awayP, homeOrder[hSpot])), homeOrder[hSpot]);
					diamond.calcPitchCount();
					getFieldOuts(diamond);
					if(hRuns > aRuns && inning > 8) {
						// do nothing
					}
					else {
						if(diamond.getOuts() != 3)
							checkPitcherForSub(awayP);
					}
					increment_hSpot();
				}
				if(diamond.getOuts() == 3) {
					diamond.clearBasepaths();
				}
			}
			inning++;
		}
		if(aRuns > hRuns) {
			printLog("\n" + awayName + " win, " + aRuns + " to " + hRuns + "\n");
			away.tWins++;
			home.tLosses++;
			setWinner(away);
		}
		else {
			printLog("\n" + homeName + " win, " + hRuns + " to " + aRuns + "\n");
			home.tWins++;
			away.tLosses++;
			setWinner(home);
		}
		System.out.println(away + " vs " + home + "   ---   " + aRuns + " - " + hRuns);
	}
	
	private static void setWinner(Teams team) {
		winner = team;
	}
	
	public static Teams getWinner() {
		return winner;
	}
	
	public static void getFieldOuts(Field diamond) {
		if(outsBefore < diamond.getOuts()) {
			if(diamond.getOuts() == 1) 
				printLog("" + diamond.getOuts() + " out!\n");
			else
				printLog("" + diamond.getOuts() + " outs!\n");
		}
	}
	
	/**
	 * Adjusts matchup based on right/left handedness of pitcher & batter.
	 * @param pitcher
	 * @param batter
	 * @return double
	 */
	private static double calcBattingAdjustment(Player pitcher, Player batter) {
		if(pitcher.LR.equals("R")) {
			if(batter.LR.equals("R")) {
				return -.075;
			}
			else {
				return .025;
			}
		}
		else {
			if(batter.LR.equals("L")) {
				return -.075;
			}
			else {
				return .025;
			}
		}
	}

	/**
	 * Adjust matchup based on pitcher's pitch count.
	 * @param pitcher
	 * @return double
	 */
	private static double calcGamePitchCountAdjustment(Player pitcher) {
		return -.035 + (pitcher.gPitchCount * .001);
	}
	
	private static double calcSeasonIPAdjustment(Player pitcher) {
		if(pitcher.role.equals("s"))
			return pitcher.spIP / 42 * .0025;
		else
			return pitcher.spIP / 14 * .0025;
	}
	
	/**
	 * Values for single at-bat between pitcher and batter: return
	 * int (attached to desired outcomes) based on the combined
	 * pitcher/batter likelihoods of a certain outcome.
	 * @param pitcher
	 * @param batter
	 * @param adjustment
	 * @return integer
	 * @throws FileNotFoundException 
	 */
	private static int matchup(Player pitcher, Player batter, double adjustment) throws FileNotFoundException {
		/*	
		DecimalFormat dF = new DecimalFormat("#0.000");
		System.out.println(dF.format(1 - (C1 + C2 + C3 + C4 + CBB + CHBP)));
		System.out.println("BB: " + dF.format(BBB) + "    PB: " + dF.format(PBB) + "    LB: " + dF.format(LBB) + "    CB: " + dF.format(CBB));
		System.out.println("B1: " + dF.format(B1) + "    P1: " + dF.format(P1) + "    L1: " + dF.format(L1) + "    C1: " + dF.format(C1));
		System.out.println("B2: " + dF.format(B2) + "    P2: " + dF.format(P2) + "    L2: " + dF.format(L2) + "    C2: " + dF.format(C2));
		System.out.println("B3: " + dF.format(B3) + "    P3: " + dF.format(P3) + "    L3: " + dF.format(L3) + "    C3: " + dF.format(C3));
		System.out.println("B4: " + dF.format(B4) + "    P4: " + dF.format(P4) + "    L4: " + dF.format(L4) + "    C4: " + dF.format(C4));
		System.out.println("BH: " + dF.format(BHBP) + "    PH: " + dF.format(PHBP) + "    LH: " + dF.format(LHBP) + "    CH: " + dF.format(CHBP));
		System.out.println("BK: " + dF.format(BK) + "    PK: " + dF.format(PK) + "    LK: " + dF.format(LK) + "    CK: " + dF.format(CK));
*/
		League league = new League();
		
		// Batter stats
	    BBB = batter.bBBAvg;
	    B1 = batter.b1BAvg;
		B2 = batter.b2BAvg;
		B3 = batter.b3BAvg;
		B4 = batter.bHRAvg;
		BHBP = batter.bHBPAvg;
		BK = batter.bKAvg;
		
		// Adjust batter/pitcher handedness stats
		BBB += BBB * adjustment;
		B1 += B1 * adjustment;
		B2 += B2 * adjustment;
		B3 += B3 * adjustment;
		B4 += B4 * adjustment;
		BK += BK * adjustment;
		
		// Pitcher stats
		PBB = pitcher.pBBAvg;
		P1 = pitcher.p1BAvg;
		P2 = pitcher.p2BAvg;
		P3 = pitcher.p3BAvg;
		P4 = pitcher.pHRAvg;
		PHBP = pitcher.pHBPAvg;
		PK = pitcher.pKAvg;

		// Adjust pitcher stats based on pitch count
		double adjPitch = calcGamePitchCountAdjustment(pitcher);
		double adjIP = calcSeasonIPAdjustment(pitcher);
		PBB += ((PBB * adjPitch) + adjIP);
		P2 += ((P2 * adjPitch) + adjIP);
		P3 += ((P3 * adjPitch) + adjIP);
		P4 += ((P4 * adjPitch) + adjIP);
		P1 += ((P1 * adjPitch) + adjIP);
		PHBP += ((PHBP * adjPitch) + adjIP);
		PK += (PK * adjPitch) + adjIP;

		// League stats
		league.leagueAvgs();
		LBF = (league.LIP * 3) + league.LH + league.LBB + league.LHBP;
		LBB = league.LBB /  LBF;
		L2 = league.L2B / LBF;
		L3 = league.L3B / LBF;
		L4 = league.LHR / LBF;
		L1 = (league.LH / LBF) - L4 - L3 - L2;
		LHBP = league.LHBP / LBF;
		LK = league.LK / LBF;
	
		if(batter.bPA < 20 || batter.position.equals("P")) {
			CBB = PBB * (PBB / LBB) / 2;
			C1 = P1 * (P1 / L1) / 2;
			C2 = P2 * (P2 / L2) / 4;
			C3 = P3 * (P3 / L3) / 6;
			C4 = P4 * (P4 / L4) / 8;
			CHBP = PHBP * (PHBP / LHBP);
			CK = PK * (PK / LK) * 2;
		}
		else {
			CBB = (BBB * (PBB / LBB));
			C1 = (B1 * (P1 / L1) * .9);
			C2 = (B2 * (P2 / L2) * .9);
			C3 = (B3 * (P3 / L3) * .9);
			C4 = (B4 * (P4 / L4) * .9);
			CHBP = (BHBP * (PHBP / LHBP));
			CK = (BK * (PK / LK));
		}

		// run at-bat sim
		rand = new Random();
		double sim = rand.nextDouble();
		int result = 0;
		
		if(sim <= C1) {
			result = SINGLE;	
		}
		else if(sim <= C1 + C2) {
			result = DOUBLE;
		}
		else if(sim <= C1 + C2 + C3) {
			result = TRIPLE;
		}
		else if(sim <= C1 + C2 + C3 + C4) {
			result = HOMERUN;
		}
		else if(sim <= C1 + C2 + C3 + C4 + CBB) {
			result = WALK;
		}
		else if(sim <= C1 + C2 + C3 + C4 + CBB + CHBP) {
			result = HIT_BY_PITCH;
		}
		else if(sim <= C1 + C2 + C3 + C4 + CBB + CHBP + CK) {
			result = STRIKEOUT;
		}
		else {
			result = OUT;
		}
		return result;
	}
	
	/**
	 * Checks if certain conditions are present, if a condition is
	 * met, a new pitcher will enter the game.
	 * @param pitcher
	 */
	private static void checkPitcherForSub(Player pitcher) {
		Random ran = new Random();
		int p = 0;
		if(!pitcher.position.equals("P") && Teams.isNational(homeAcronym)) {
			if(inning > 9) {
				if(pitcher.equals(awayP)) {
					do {
						p = ran.nextInt(away.bullpen.size() - 5) + 5;
					}
					while(away.gamePitchers.contains(away.bullpen.get(p)));
					if(pitcher.position.equals("P"))
						printLog("Pitching Substitution: "+away.bullpen.get(p)+" for "+awayP+". "+awayP+" leaves the game with "+((int)awayP.gPitchCount)+" pitches\n");
					else
						printLog("Pitching Substitution: "+away.bullpen.get(p)+" for "+awayP+".\n");
					awayP = away.bullpen.get(p);
					away.gamePitchers.add(awayP);
					awayP.gPitchCount = 0;
					if(Teams.isNational(homeAcronym)) {
						awayOrder[awayOrder.length-1] = awayP;
						away.gameBatters.add(awayP);
					}
					addAwayField(awayP);
				}
				else if(pitcher.equals(homeP)) {
					do {
						p = ran.nextInt(home.bullpen.size() - 5) + 5;
					}
					while(home.gamePitchers.contains(home.bullpen.get(p)));
					if(pitcher.position.equals("P"))
						printLog("Pitching Substitution: "+home.bullpen.get(p)+" for "+homeP+". "+homeP+" leaves the game with "+((int)homeP.gPitchCount)+" pitches\n");
					else
						printLog("Pitching Substitution: "+home.bullpen.get(p)+" for "+homeP+".\n");
					homeP = home.bullpen.get(p);
					home.gamePitchers.add(homeP);
					homeP.gPitchCount = 0;
					if(Teams.isNational(homeAcronym)) {
						homeOrder[homeOrder.length-1] = homeP;
						home.gameBatters.add(homeP);
					}
					addHomeField(homeP);
				}
			}
			else if(inning == 9) {
				if(pitcher.equals(awayP)) {
					if(pitcher.position.equals("P"))
						printLog("Pitching Substitution: "+getCloser(away)+" for "+awayP+". "+awayP+" leaves the game with "+((int)awayP.gPitchCount)+" pitches\n");
					else
						printLog("Pitching Substitution: "+getCloser(away)+" for "+awayP+".\n");
					awayP = getCloser(away);
					away.gamePitchers.add(awayP);
					awayP.gPitchCount = 0;
					if(Teams.isNational(homeAcronym)) {
						awayOrder[awayOrder.length-1] =  awayP;
						away.gameBatters.add(awayP);
					}
					addAwayField(awayP);
				}
				else if(pitcher.equals(homeP)) {
					if(pitcher.position.equals("P"))
						printLog("Pitching Substitution: "+ getCloser(home)+" for "+homeP+". "+homeP+" leaves the game with "+((int)homeP.gPitchCount)+" pitches\n");
					else
						printLog("Pitching Substitution: "+getCloser(home)+" for "+homeP+".\n");				
					homeP = getCloser(home);
					home.gamePitchers.add(homeP);
					homeP.gPitchCount = 0;
					if(Teams.isNational(homeAcronym)) {
						homeOrder[homeOrder.length-1] = homeP;
						home.gameBatters.add(homeP);
					}
					addHomeField(homeP);
				}
			}
			else if(inning == 8) {
				if(pitcher.equals(awayP)) {
					if(pitcher.position.equals("P"))
						printLog("Pitching Substitution: "+getSetUpMan(away)+" for "+awayP+". "+awayP+" leaves the game with "+((int)awayP.gPitchCount)+" pitches\n");
					else
						printLog("Pitching Substitution: "+getSetUpMan(away)+" for "+awayP+".\n");
					awayP = getSetUpMan(away);
					away.gamePitchers.add(awayP);
					awayP.gPitchCount = 0;
					if(Teams.isNational(homeAcronym))
						awayOrder[awayOrder.length-1] =  awayP;
					addAwayField(awayP);
				}
				else if(pitcher.equals(homeP)) {
					if(pitcher.position.equals("P"))
						printLog("Pitching Substitution: "+getSetUpMan(home)+" for "+homeP+". "+homeP+" leaves the game with "+((int)homeP.gPitchCount)+" pitches\n");
					else
						printLog("Pitching Substitution: "+getSetUpMan(home)+" for "+homeP+".\n");				
					homeP = getSetUpMan(home);
					home.gamePitchers.add(homeP);
					homeP.gPitchCount = 0;
					if(Teams.isNational(homeAcronym))
						homeOrder[homeOrder.length-1] = homeP;
					addHomeField(homeP);
				}
			}
			else if(inning < 8) {
				if(pitcher.equals(awayP)) {
					do {
						p = ran.nextInt(away.bullpen.size() - 7) + 5;
					}
					while(away.gamePitchers.contains(away.bullpen.get(p)));
					if(pitcher.position.equals("P"))
						printLog("Pitching Substitution: "+away.bullpen.get(p)+" for "+awayP+". "+awayP+" leaves the game with "+((int)awayP.gPitchCount)+" pitches\n");
					else
						printLog("Pitching Substitution: "+away.bullpen.get(p)+" for "+awayP+".\n");
					awayP = away.bullpen.get(p);
					away.gamePitchers.add(awayP);
					awayP.gPitchCount = 0;
					if(Teams.isNational(homeAcronym)) {
						awayOrder[awayOrder.length-1] = awayP;
						away.gameBatters.add(awayP);
					}
					addAwayField(awayP);
				}
				else if(pitcher.equals(homeP)) {
					do {
						p = ran.nextInt(home.bullpen.size() - 7) + 5;
					}
					while(home.gamePitchers.contains(home.bullpen.get(p)));
					if(pitcher.position.equals("P"))
						printLog("Pitching Substitution: "+home.bullpen.get(p)+" for "+homeP+". "+homeP+" leaves the game with "+((int)homeP.gPitchCount)+" pitches\n");
					else
						printLog("Pitching Substitution: "+home.bullpen.get(p)+" for "+homeP+".\n");
					homeP = home.bullpen.get(p);
					home.gamePitchers.add(homeP);
					homeP.gPitchCount = 0;
					if(Teams.isNational(homeAcronym)) {
						homeOrder[homeOrder.length-1] = homeP;
						home.gameBatters.add(homeP);
					}
					addHomeField(homeP);
				}
			}
		}
		if((inning > 9)) {
			if (pitcher.position.equals("P") && (pitcher.equals(homeP) && (aRuns - hRuns <= 2) && (hRuns - aRuns >= -2) && (pitcher.gpBB + pitcher.gpH > 2))) {
				do {
					p = ran.nextInt(home.bullpen.size() - 5) + 5;
				}
				while(home.gamePitchers.contains(home.bullpen.get(p)));
				if(pitcher.position.equals("P"))
					printLog("Pitching Substitution: "+home.bullpen.get(p)+" for "+homeP+". "+homeP+" leaves the game with "+((int)homeP.gPitchCount)+" pitches\n");
				else
					printLog("Pitching Substitution: "+home.bullpen.get(p)+" for "+homeP+".\n");
				homeP = home.bullpen.get(p);
				home.gamePitchers.add(homeP);
				homeP.gPitchCount = 0;
				if(Teams.isNational(homeAcronym)) {
					homeOrder[homeOrder.length-1] = homeP;
					home.gameBatters.add(homeP);
				}
				addHomeField(homeP);
			}
			else if(pitcher.position.equals("P") && (pitcher.equals(awayP) && (hRuns - aRuns <= 2) && (aRuns - hRuns >= -2) && (pitcher.gpBB + pitcher.gpH > 2))) {
				do {
					p = ran.nextInt(away.bullpen.size() - 5) + 5;
				}
				while(away.gamePitchers.contains(away.bullpen.get(p)));
				if(pitcher.position.equals("P"))
					printLog("Pitching Substitution: "+away.bullpen.get(p)+" for "+awayP+". "+awayP+" leaves the game with "+((int)awayP.gPitchCount)+" pitches\n");
				else
					printLog("Pitching Substitution: "+away.bullpen.get(p)+" for "+awayP+".\n");
				awayP = away.bullpen.get(p);
				away.gamePitchers.add(awayP);
				awayP.gPitchCount = 0;
				if(Teams.isNational(homeAcronym)) {
					awayOrder[awayOrder.length-1] = awayP;
					away.gameBatters.add(awayP);
				}
				addAwayField(awayP);
			}
		}
		else if(inning == 9) {
			if (pitcher.position.equals("P") && pitcher.equals(homeP) && ((hRuns - aRuns < 4 && hRuns - aRuns > 0) || (aRuns - hRuns <= 0 && aRuns - hRuns >= -2)) && !home.gamePitchers.contains(getCloser(home)) && ((getCloser(home).spIP * 2) <= (home.tWins + home.tLosses + 2))) {
				if(pitcher.position.equals("P"))
					printLog("Pitching Substitution: "+ getCloser(home)+" for "+homeP+". "+homeP+" leaves the game with "+((int)homeP.gPitchCount)+" pitches\n");
				else
					printLog("Pitching Substitution: "+getCloser(home)+" for "+homeP+".\n");				
				homeP = getCloser(home);
				home.gamePitchers.add(homeP);
				homeP.gPitchCount = 0;
				if(Teams.isNational(homeAcronym)) {
					homeOrder[homeOrder.length-1] = homeP;
					home.gameBatters.add(homeP);
				}
				addHomeField(homeP);
			}
			else if(pitcher.position.equals("P") && pitcher.equals(awayP) && (aRuns - hRuns < 4 && aRuns - hRuns > 0 || (hRuns - aRuns <= 0 && hRuns - aRuns >= -2)) && !away.gamePitchers.contains(getCloser(away)) && ((getCloser(away).spIP * 2) <= (away.tWins + away.tLosses + 2))) {
				if(pitcher.position.equals("P"))
					printLog("Pitching Substitution: "+getCloser(away)+" for "+awayP+". "+awayP+" leaves the game with "+((int)awayP.gPitchCount)+" pitches\n");
				else
					printLog("Pitching Substitution: "+getCloser(away)+" for "+awayP+".\n");
				awayP = getCloser(away);
				away.gamePitchers.add(awayP);
				awayP.gPitchCount = 0;
				if(Teams.isNational(homeAcronym)) {
					awayOrder[awayOrder.length-1] =  awayP;
					away.gameBatters.add(awayP);
				}
				addAwayField(awayP);
			}
		}
		else if (inning == 8) {
			if (pitcher.position.equals("P") && pitcher.equals(homeP) && ((hRuns - aRuns < 4 && hRuns - aRuns > 0) || (aRuns - hRuns <= 0 && aRuns - hRuns >= 2)) && !home.gamePitchers.contains(getSetUpMan(home)) && ((getSetUpMan(home).spIP * 2) < (home.tWins + home.tLosses))) {
				if(pitcher.position.equals("P"))
					printLog("Pitching Substitution: "+getSetUpMan(home)+" for "+homeP+". "+homeP+" leaves the game with "+((int)homeP.gPitchCount)+" pitches\n");
				else
					printLog("Pitching Substitution: "+getSetUpMan(home)+" for "+homeP+".\n");				
				homeP = getSetUpMan(home);
				home.gamePitchers.add(homeP);
				homeP.gPitchCount = 0;
				if(Teams.isNational(homeAcronym)) {
					homeOrder[homeOrder.length-1] = homeP;
					home.gameBatters.add(homeP);
				}
				addHomeField(homeP);
			}
			else if(pitcher.position.equals("P") && pitcher.equals(awayP) && ((aRuns - hRuns < 4 && aRuns - hRuns > 0) || (hRuns - aRuns <= 0 && hRuns - aRuns >= 2)) && !away.gamePitchers.contains(getSetUpMan(away)) && ((getSetUpMan(away).spIP * 2) < (away.tWins + away.tLosses))) {
				if(pitcher.position.equals("P"))
					printLog("Pitching Substitution: "+getSetUpMan(away)+" for "+awayP+". "+awayP+" leaves the game with "+((int)awayP.gPitchCount)+" pitches\n");
				else
					printLog("Pitching Substitution: "+getSetUpMan(away)+" for "+awayP+".\n");
				awayP = getSetUpMan(away);
				away.gamePitchers.add(awayP);
				awayP.gPitchCount = 0;
				if(Teams.isNational(homeAcronym)) {
					awayOrder[awayOrder.length-1] =  awayP;
					away.gameBatters.add(awayP);
				}
				addAwayField(awayP);
			}
		}
		else if ((pitcher.gpER > 6) ||
				(pitcher.gPitchCount > 55 && pitcher.gpER > 5) ||
				(pitcher.gPitchCount > 75 && inning < 8 && pitcher.gpER > 4) ||
				(pitcher.gPitchCount > 85 && inning < 8 && pitcher.gpER > 3) ||
				(pitcher.gPitchCount > 95 && inning < 8 && pitcher.gpER > 2) ||
				(pitcher.gPitchCount > 105 && inning < 8 && pitcher.gpER > 1) ||
				(pitcher.gPitchCount > 115 && inning < 9 && pitcher.gpER > 0) ||
				(pitcher.gPitchCount > 125) ||
				(pitcher.role.equals("c") && inning > 10)) { 
			if(pitcher.position.equals("P") && pitcher.equals(homeP)) {
				do {
					p = ran.nextInt(home.bullpen.size() - 7) + 5;
				}
				while(home.gamePitchers.contains(home.bullpen.get(p)));
				if(pitcher.position.equals("P"))
					printLog("Pitching Substitution: "+home.bullpen.get(p)+" for "+homeP+". "+homeP+" leaves the game with "+((int)homeP.gPitchCount)+" pitches\n");
				else
					printLog("Pitching Substitution: "+home.bullpen.get(p)+" for "+homeP+".\n");
				homeP = home.bullpen.get(p);
				home.gamePitchers.add(homeP);
				homeP.gPitchCount = 0;
				if(Teams.isNational(homeAcronym)) {
					homeOrder[homeOrder.length-1] = homeP;
					home.gameBatters.add(homeP);
				}
				addHomeField(homeP);
			}
			else if(pitcher.position.equals("P") && pitcher.equals(awayP)) {
				do {
					p = ran.nextInt(away.bullpen.size() - 7) + 5;
				}
				while(away.gamePitchers.contains(away.bullpen.get(p)));
				if(pitcher.position.equals("P"))
					printLog("Pitching Substitution: "+away.bullpen.get(p)+" for "+awayP+". "+awayP+" leaves the game with "+((int)awayP.gPitchCount)+" pitches\n");
				else
					printLog("Pitching Substitution: "+away.bullpen.get(p)+" for "+awayP+".\n");
				awayP = away.bullpen.get(p);
				away.gamePitchers.add(awayP);
				awayP.gPitchCount = 0;
				if(Teams.isNational(homeAcronym)) {
					awayOrder[awayOrder.length-1] = awayP;
					away.gameBatters.add(awayP);
				}
				addAwayField(awayP);
			}
		}
	}
	
	private static void checkBatterForSub(Teams team, Player batter) {
		double max = 0;
		Player temp = null;
		int numLoops = 0;
		if(team.equals(away)) {
			if(awayOrder[aSpot].position.equals("P") && inning > 5 && inning < 8 && Math.abs(hRuns - aRuns) < 4) {
				do {
					temp = away.bench.get(rand.nextInt(away.bench.size()));
					numLoops++;
					if(numLoops == away.bench.size())
						break;
				}
				while(temp.LR.equals(homeP.LR) || away.gameBatters.contains(temp));
				if(!away.gameBatters.contains(temp) && temp != null) {
					printLog("Pinch Hitter: "+temp+" for "+awayP+". "+awayP+" leaves the game with "+((int)awayP.gPitchCount)+" pitches\n");
					awayP = temp;
					awayP.prePosition = awayP.position;
					awayP.position = "PH";
					awayOrder[aSpot] = awayP;
					away.gamePitchers.remove(awayP);
					away.gameBatters.add(awayP);
					addAwayField(awayP);
				}
			}
			else if(awayOrder[aSpot].position.equals("P") && inning > 7 && Math.abs(hRuns - aRuns) < 5) {
				for(Player p : away.bench) {
					if(p.sbOPS > max && !away.gameBatters.contains(p) && !p.LR.equals(homeP.LR)) {
						max = p.sbOPS;
						temp = p;
					}
				}
				if(!away.gameBatters.contains(temp) && temp != null) {
					printLog("Pinch Hitter: "+temp+" for "+awayP+". "+awayP+" leaves the game with "+((int)awayP.gPitchCount)+" pitches\n");
					awayP = temp;
					awayP.prePosition = awayP.position;
					awayP.position = "PH";
					awayOrder[aSpot] = awayP;
					away.gamePitchers.remove(awayP);
					away.gameBatters.add(awayP);
					addAwayField(awayP);
				}
			}
		}
		if(team.equals(home)) {
			if(homeOrder[hSpot].position.equals("P") && inning > 5 && inning < 8 && Math.abs(hRuns - aRuns) < 4) {
				do {
					temp = home.bench.get(rand.nextInt(home.bench.size()));
					numLoops++;
					if(numLoops == home.bench.size())
						break;
				}
				while(temp.LR.equals(awayP.LR) || home.gameBatters.contains(temp));
				if(!home.gameBatters.contains(temp) && temp != null) {
					printLog("Pinch Hitter: "+temp+" for "+homeP+". "+homeP+" leaves the game with "+((int)homeP.gPitchCount)+" pitches\n");
					homeP = temp;
					homeP.prePosition = homeP.position;
					homeP.position = "PH";
					homeOrder[hSpot] = homeP;
					home.gamePitchers.remove(homeP);
					home.gameBatters.add(homeP);
					addHomeField(homeP);
				}
			}
			else if(homeOrder[hSpot].position.equals("P") && inning > 7 && Math.abs(hRuns - aRuns) < 5) {
				for(Player p : home.bench) {
					if(p.sbOPS > max && !home.gameBatters.contains(p) && !p.LR.equals(awayP.LR)) {
						max = p.sbOPS;
						temp = p;
					}
				}
				if(!home.gameBatters.contains(temp) && temp != null) {
					printLog("Pinch Hitter: "+temp+" for "+homeP+". "+homeP+" leaves the game with "+((int)homeP.gPitchCount)+" pitches\n");
					homeP = temp;
					homeP.prePosition = homeP.position;
					homeP.position = "PH";
					homeOrder[hSpot] = homeP;
					home.gamePitchers.remove(homeP);
					home.gameBatters.add(homeP);
					addHomeField(homeP);
				}
			}
		}
	}
	
	/**
	 * Gets the closer of a team based on who has the 
	 * highest number of saves. Only pitchers who 
	 * haven't entered the game are eligible.
	 * @param team
	 * @return Player
	 */
	private static Player getCloser(Teams team) {
		double max = 0;
		Player closer = team.bullpen.get(0);
		for (Player p : team.bullpen) {
			if (p.pS > max) {
				if (!p.equals(homeP) && !home.gamePitchers.contains(p)) {
					max = p.pS;
					closer = p;
				}
				else if (!p.equals(awayP) && !away.gamePitchers.contains(p)) {
					max = p.pS;
					closer = p;
				}
			}
		}
		return closer;		
	}

	/**
	 * Gets the set-up man of a team based on who has the
	 * second most number of saves. Only pitchers who 
	 * haven't entered the game are eligible.
	 * @param team
	 * @return Player
	 */
	private static Player getSetUpMan(Teams team) {
		Player suman = team.bullpen.get(0), closer;
		double max = 0, secmax = 0;
		for(Player p : team.bullpen) {
			if(p.role.equals("c")) {
				closer = p;
				max = closer.pS;
			}
		}
		for(Player p : team.bullpen) {
			if(p.role.equals("su")) {
				if(!p.equals(homeP) && !home.gamePitchers.contains(p)) {
					suman = p;
				}
				else if(!p.equals(awayP) && !away.gamePitchers.contains(p)) {
					suman = p;
				}
			}
			else if(p.pS > secmax && p.pS < max) {
				if(!p.equals(homeP) && !home.gamePitchers.contains(p)) {
					secmax = p.pS;
					suman = p;
				}
				else if(!p.equals(awayP) && !away.gamePitchers.contains(p)) {
					secmax = p.pS;
					suman = p;
				}
			}
		}
		return suman;
	}
	
	/**
	 * Sets the position of each player based
	 * on the lineups for the away/home teams.
	 */
	private static void buildFields() {
		for(int i = 0; i < 9; i++) {
			addAwayField(awayOrder[i]);
			addHomeField(homeOrder[i]);
		}
		addAwayField(awayP);
		addHomeField(homeP);
	}

	/**
	 * If player's position is abnormal, find an empty slot
	 * in the field and add them there.
	 * @param field
	 * @param p
	 * @param startIndex
	 * @param endIndex
	 */
	private static void insertToEmptySlot(Player[] field, Player p, int startIndex, int endIndex) {
		for (int i = startIndex; i <= endIndex; i++) {
			if (field[i] == null) {
				field[i] = p;
				break;
			}
			//else
			//	p.position = "DH";
		}
	}
	
	/**
	 * Swaps position number for position name.
	 * @param position
	 * @return String
	 */
	public static String numberToPosition(int position) {
		if(position == 0) return "P";
		else if(position == 1) return "C";
		else if(position == 2) return "1B";
		else if(position == 3) return "2B";
		else if(position == 4) return "3B";
		else if(position == 5) return "SS";
		else if(position == 6) return "LF";
		else if(position == 7) return "CF";
		else return "RF";
	}
	
	/**
	 * Adds the given player to the field. Buildfields()
	 * calls this method to do its function.
	 * @param player
	 */
	private static void addAwayField(Player player) {
		if(!player.role.equals("b")) {
			if(player.position.equals("P")) {
				awayField[0] = player;
			}	
			else if(player.position.equals("C")) {
				if(awayField[1] != null)
					insertToEmptySlot(awayField, awayField[1], 1, 8);
				awayField[1] = player;
			}
			else if(player.position.equals("1B")) {
				if(awayField[2] != null)
					insertToEmptySlot(awayField, awayField[2], 1, 8);
				awayField[2] = player;
			}
			else if(player.position.equals("2B")) {
				if(awayField[3] != null)
					insertToEmptySlot(awayField, awayField[3], 1, 8);
				awayField[3] = player;
			}
			else if(player.position.equals("3B")) {
				if(awayField[4] != null)
					insertToEmptySlot(awayField, awayField[4], 1, 8);
				awayField[4] = player;
			}
			else if(player.position.equals("SS")) {
				if(awayField[5] != null)
					insertToEmptySlot(awayField, awayField[5], 1, 8);
				awayField[5] = player;
			}
			else if(player.position.equals("LF")) {
				if(awayField[6] != null)
					insertToEmptySlot(awayField, awayField[6], 1, 8);
				awayField[6] = player;
			}
			else if(player.position.equals("CF")) {
				if(awayField[7] != null)
					insertToEmptySlot(awayField, awayField[7], 1, 8);
				awayField[7] = player;
			}
			else if(player.position.equals("RF")) {
				if(awayField[8] != null)
					insertToEmptySlot(awayField, awayField[8], 1, 8);
				awayField[8] = player;
			}
			else if (player.position.equals("MI")) {
				if(awayField[3] == null)
					awayField[3] = player;
				else if(awayField[5] == null)
					awayField[5] = player;
				else {
					insertToEmptySlot(awayField, player, 2, 5);
				}
			}
			else if (player.position.equals("CI")) {
				if(awayField[2] == null)
					awayField[2] = player;
				else if(awayField[4] == null)
					awayField[4] = player;
				else {
					insertToEmptySlot(awayField, player, 2, 5);
				}
			}
			else if (player.position.equals("IF")) {
				insertToEmptySlot(awayField, player, 2, 5);
			}
			else if (player.position.equals("OF")) {
				insertToEmptySlot(awayField, player, 6, 8);
			}
			else if (player.position.equals("UT")) {
				insertToEmptySlot(awayField, player, 2, 8);
			}
		}
	}
	
	/**
	 * Adds the given player to the field. Buildfields()
	 * calls this method to do its function.
	 * @param player
	 */
	private static void addHomeField(Player player) {
		if(!player.role.equals("b")) {
			if(player.position.equals("P")) {
				homeField[0] = player;
			}	
			else if(player.position.equals("C")) {
				if(homeField[1] != null)
					insertToEmptySlot(homeField, homeField[1], 1, 8);
				homeField[1] = player;
			}
			else if(player.position.equals("1B")) {
				if(homeField[2] != null)
					insertToEmptySlot(homeField, homeField[2], 1, 8);
				homeField[2] = player;
			}
			else if(player.position.equals("2B")) {
				if(homeField[3] != null)
					insertToEmptySlot(homeField, homeField[3], 1, 8);
				homeField[3] = player;
			}
			else if(player.position.equals("3B")) {
				if(homeField[4] != null)
					insertToEmptySlot(homeField, homeField[4], 1, 8);
				homeField[4] = player;
			}
			else if(player.position.equals("SS")) {
				if(homeField[5] != null)
					insertToEmptySlot(homeField, homeField[5], 1, 8);
				homeField[5] = player;
			}
			else if(player.position.equals("LF")) {
				if(homeField[6] != null)
					insertToEmptySlot(homeField, homeField[6], 1, 8);
				homeField[6] = player;
			}
			else if(player.position.equals("CF")) {
				if(homeField[7] != null)
					insertToEmptySlot(homeField, homeField[7], 1, 8);
				homeField[7] = player;
			}
			else if(player.position.equals("RF")) {
				if(homeField[8] != null)
					insertToEmptySlot(homeField, homeField[8], 1, 8);
				homeField[8] = player;
			}
			else if (player.position.equals("MI")) {
				if(homeField[3] == null)
					homeField[3] = player;
				else if(homeField[5] == null)
					homeField[5] = player;
				else {
					insertToEmptySlot(homeField, player, 2, 5);
				}
			}
			else if (player.position.equals("CI")) {
				if(homeField[2] == null)
					homeField[2] = player;
				else if(homeField[4] == null)
					homeField[4] = player;
				else {
					insertToEmptySlot(homeField, player, 2, 5);
				}
			}
			else if (player.position.equals("IF")) {
				insertToEmptySlot(homeField, player, 2, 5);
			}
			else if (player.position.equals("OF")) {
				insertToEmptySlot(homeField, player, 6, 8);
			}
			else if (player.position.equals("UT")) {
				insertToEmptySlot(homeField, player, 2, 8);
			}
		}
	}

	/**
	 * Used to print the game log (play-by-play)
	 * to a file. The file is named gamelog.txt 
	 * in this project.
	 * @param log
	 */
	public static void printLog(String log) {
		w.print(log);
	}
	
	/**
	 * Once all the batters have taken their at-bat
	 * for the away team, then go back to the beginning.
	 */
	private static void increment_aSpot() {
		if(aSpot == 8) {
			aSpot = 0;
		}
		else {
			aSpot++;
		}
	}
	
	/**
	 * Once all the batters have taken their at-bat
	 * for the home team, then go back to the beginning.
	 */
	private static void increment_hSpot() {
		if(hSpot == 8) {
			hSpot = 0;
		}
		else {
			hSpot++;
		}
	}

	/**
	 * Sets all game stats for both teams back to 0.
	 * @param team
	 */
	public static void clearGameStats(Teams team) {
		for(Player p : team.roster) {
			p.gbAB = p.gb2B = p.gb3B = p.gbRBI = p.gbH = p.gbR = p.gbHR = p.gbBB = p.gbK = p.gbHBP = 0; 
			p.gpIP = p.gpER = p.gpTO = p.gpH = p.gpR = p.gpHR = p.gpBB = p.gpK = p.gpHBP = p.gPitchCount = 0; 
			p.gE = p.gPO = p.gA;
		}
	}
}
