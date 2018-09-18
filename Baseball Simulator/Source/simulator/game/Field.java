/*======================================

Project:       Baseball Simulator
File:          Field.java
Author:        Braden Franksen
Date:          Mar 15, 2017

======================================*/
package simulator.game;

import java.io.PrintWriter;
import java.util.Random;

import simulator.players.Player;

/**
 * Updates field after an at-bat.
 * @author Braden
 * @param Player
 * @param result
 */

public class Field {

	private static Player[] field;
	private static Player[] basepaths;
	private static Random rand = new Random();
	private int outs;
    private final static int SINGLE = 0;
    private final static int DOUBLE = 1;
    private final static int TRIPLE = 2;
    private final static int HOMERUN = 3;
    private final static int WALK = 4;
    private final static int HBP = 5;
    private final static int STRIKEOUT = 6;
	private static int hitDestination = 0, randPlay = 0;
	private static boolean sac = false, infieldIn = false;
    private PrintWriter w;
    
    public Field(PrintWriter init_w) {
    	w = init_w;
    	basepaths = new Player[4];
    	outs = 0;
    }
    
    public void resetField(Player[] init_field) {
    	field = init_field;
    	field[0] = init_field[0];
    	clearBasepaths();
    	outs = 0;
    }
    
    public void clearBasepaths() {
    	for(int i = 0; i < 4; i++) {
    		basepaths[i] = null;
    	}
    }
    
    public void printBasepaths() {
    	for(int i = 0; i < 4; i++) {
    		printLog("Player: " + basepaths[i]);
    	}
    }
    
    public int updateField(int result, Player player) {
    	hitDestination = rand.nextInt(9);
    	randPlay = rand.nextInt(16);
    	// TODO create boolean for run or earned run. non-errors set it to true. if false, Pitcher has R, not ER.
    	if(result == SINGLE) {
    		printLog(player + " hits ");
    		return updateSingle(player);
    	}
    	else if(result == DOUBLE) {
    		printLog(player + " hits a double.\n");
    		return updateDouble(player);
    	}
    	else if(result == TRIPLE) {
    		printLog(player + " hits a triple.\n");
    		return updateTriple(player);
    	}
    	else if(result == HOMERUN) {
    		printLog(player + " hits a home run.\n");
    		return updateHomerun(player);
    	}
    	else if(result == WALK) {
    		printLog(player + " draws a walk.\n");
    		return updateWalk(player);
    	}
    	else if(result == HBP) {
    		printLog(player + " is hit by a pitch.\n");
    		return updateHBP(player);
    	}
    	else if(result == STRIKEOUT) {
    		printLog(player + " strikes out.\n");
    		return updateStrikeout(player);
    	}
    	else {
    		return updateOut(player);
    	}
    }
    
    public int updateStolenBase(Player player) {
    	int runs = 0;
    	randPlay = rand.nextInt(16);
    	double chance = rand.nextDouble();
    	if(Game.inning > 7 && Math.abs(Game.aRuns - Game.hRuns) <= 1) {}
    	else {
	    	if(outs < 3 && manOnThird() && basepaths[3].isFast() && randPlay == 15 && Math.abs(Game.aRuns - Game.hRuns) > 1 && player.bSLG < .4) {
	    		if(player.bSpeed / 1.8 > chance) {
	        		printLog("\t" + basepaths[3] + " steals home!\n");
	        		field[0].gpER++;
	        		basepaths[3].gbSB++;
	        		basepaths[3].gbR++;
	        		clearBase(3);
	        		runs++;
	    		}
	    		else {
	    			printLog("\t" + basepaths[3] + " is out trying to steal home!\n");
	    			outs++;
	    			field[0].gpTO++;
	    			basepaths[3].gbCS++;
	    			clearBase(3);
	        		Game.getFieldOuts(this);
	    		}
	    	}
	    	if(outs < 3 && !manOnThird() && manOnSecond() && randPlay == 14 && outs < 3 && basepaths[2].isFast() && player.bSLG < .4) {
	    		if(player.bSpeed / 1.8 > chance) {
	    			printLog("\t" + basepaths[2] + " steals " + Game.numberToPosition(4) + "!\n");
	        		basepaths[2].bSB++;
	        		basepaths[3] = basepaths[2];
	        		clearBase(2);  		
	        	}
	    		else {
	    			printLog("\t" + basepaths[2] + " is out trying to steal " + Game.numberToPosition(4) + "!\n");
	    			outs++;
	    			field[0].gpTO++;
	    			basepaths[2].bCS++;
	    			clearBase(2);
	        		Game.getFieldOuts(this);
	    		}
	    	}
	    	if(outs != 3 && !manOnSecond() && manOnFirst() && randPlay > 13 && (outs < 3 || basepaths[1].isFast()) && player.bSLG < .45) {
	    		if(player.bSpeed / 1.8 > chance) {
	    			printLog("\t" + basepaths[1] + " steals " + Game.numberToPosition(3) + "!\n");
	        		basepaths[1].bSB++;
	        		basepaths[2] = basepaths[1];
	        		clearBase(1);
	    		}
	    		else {
	    			printLog("\t" + basepaths[1] + " is out trying to steal " + Game.numberToPosition(3) + "!\n");
	    			outs++;
	    			field[0].gpTO++;
	    			basepaths[1].bCS++;
	    			clearBase(1);
	        		Game.getFieldOuts(this);
	    		}
	    	}
    	}
    	return runs;
    }

    private int updateSingle(Player player) {
    	int runs = 0;
    	field[0].gpH++;
    	printLog(singleLocation());
    	printLog(" for a single.\n");
    	if(manOnThird()) {
    		runs += advanceFrom(3, 1);
    		player.gbRBI++;
    		field[0].gpER++;
    	}
    	if(manOnSecond()) {
    		if(basepaths[2].isFast() && hitDestination != 0) {
    			runs += advanceFrom(2, 2);
    			player.gbRBI++;
    			field[0].gpER++;
    		}
    		else {
    			runs += advanceFrom(2, 1);
    		}
    	}
    	if(manOnFirst()) {
    		if(!manOnThird() && basepaths[1].isFast() && (hitDestination == 1 || hitDestination == 2 || hitDestination == 8)) {		
    			runs += advanceFrom(1, 2);
    		}
    		else {
    			runs += advanceFrom(1, 1);
    		}
    	}
    	placeRunner(1, player);
    	player.gbAB++;
    	player.gbH++;
    	return runs;
	}

    private int updateDouble(Player player) {
		int runs = 0;
		field[0].gpH++;
		if (manOnThird()) {
			runs += advanceFrom(3, 1);
			player.gbRBI++;
			field[0].gpER++;
		}
		if (manOnSecond()) {
			runs += advanceFrom(2,2);
			player.gbRBI++;
			field[0].gpER++;
		}
		if (manOnFirst()) {
			if (basepaths[1].isFast()) {
				runs += advanceFrom(1,3);
				player.gbRBI++;
				field[0].gpER++;
			}
			else
				runs += advanceFrom(1,2);
		}
		placeRunner(2,player);
		player.gbAB++;
		player.gbH++;
		player.gb2B++;
		return runs;
    }

    private int updateTriple(Player player) {
		int runs = 0;
		field[0].gpH++;
		if (manOnThird()) {
			runs += advanceFrom(3,3);
			player.gbRBI++;
			field[0].gpER++;
		}
		if (manOnSecond()) {
			runs += advanceFrom(2,3);
			player.gbRBI++;
			field[0].gpER++;
		}
		if (manOnFirst()) {
			runs += advanceFrom(1,3);
			player.gbRBI++;
			field[0].gpER++;
		}
		placeRunner(3,player);
		player.gbAB++;
		player.gbH++;
		player.gb3B++;
		return runs;
    }
    
    private int updateHomerun(Player player) {
		int runs = 0;
		field[0].gpH++;
		field[0].gpER++;
		field[0].gpHR++;
		if (manOnThird()) {
			runs += advanceFrom(3,4);
			player.gbRBI++;
			field[0].gpER++;
		}
		if (manOnSecond()) {
			runs += advanceFrom(2,4);
			player.gbRBI++;
			field[0].gpER++;
		}
		if (manOnFirst()) {
			runs += advanceFrom(1,4);
			player.gbRBI++;
			field[0].gpER++;
		}
		player.gbRBI++;
		player.gbAB++;
		player.gbR++;
		player.gbH++;
		player.gbHR++;
		printLog("\t"+player+" scores.\n");
		if(field[0].equals(Game.awayP))
			Game.hInnRuns[Game.inning - 1]++;
		else if(field[0].equals(Game.homeP))
			Game.aInnRuns[Game.inning - 1]++;
		runs++;
		return runs;
    }
    
    private int updateWalk(Player player) {
		int runs = 0;
		field[0].gpBB++;
		if (manOnThird())
			if (manOnSecond() && manOnFirst()) {
				runs += advanceFrom(3, 1);
				player.gbRBI++;
				field[0].gpER++;
			}
		if (manOnSecond()) {
			if (manOnFirst()) {
				runs += advanceFrom(2, 1);
			}
		}
		if (manOnFirst()) {
			runs += advanceFrom(1, 1);
		}
		placeRunner(1,player);
		player.gbBB++;
		return runs;
    }
    
    private int updateStrikeout(Player player) {
		int runs = 0;
		field[0].gpK++;
		double error = rand.nextDouble();
		if (error > field[1].fFieldPct) {
			// catcher makes an error: passed ball
			field[1].gE++;
			printLog(field[1]+" drops the third strike!\n");
			if (manOnThird())
				runs += advanceFrom(3, 1);
			if (manOnSecond())
				runs += advanceFrom(2, 1);
			if (manOnFirst())
				runs += advanceFrom(1, 1);
			placeRunner(1,player);	
 		}
		else { 
			outs++;
			field[0].gpTO++;
    	}
		player.gbAB++;
		player.gbK++;
		return runs;
    }
    
    private int updateHBP(Player player) {
		int runs = 0;
		field[0].gpHBP++;
		if (manOnThird())
			if (manOnSecond() && manOnFirst()) {
				runs += advanceFrom(3, 1);
				player.gbRBI++;
				field[0].gpER++;
			}
		if (manOnSecond())
			if (manOnFirst()) {
				runs += advanceFrom(2, 1);
			}
		if (manOnFirst()) {
			runs += advanceFrom(1, 1);
		}
		placeRunner(1,player);
		player.gbHBP++;
		return runs;
    }
    
    public int updateOut(Player player) {
		field[0].gpTO++;
		outs++;
		sac = false;
		int runs = 0;
		double error = rand.nextDouble();
		if(hitDestination != 1) 
			printLog(player + " hits a fieldable ball to " + Game.numberToPosition(hitDestination) + ".\n");
		if (error > field[hitDestination].fFieldPct && field[hitDestination].fFieldPct > 0)
			updateOutError(player);
		else if (outs <= 2 && hitDestination == 1)
			runs += updateOutBunt(player);
		else if (outs <= 2 && hitDestination > 5 && (manOnThird() || manOnSecond()))
			runs += updateOutSacFly(player);
		else if (outs <= 2 && (hitDestination == 2 || hitDestination == 3) && !manOnFirst() && (manOnSecond() || manOnThird()))
			runs += updateOutRightSideGB(player);
		else if (outs <= 2 && (hitDestination == 2 || hitDestination == 4) && manOnFirst() && !manOnSecond() && manOnThird() && randPlay < 8)
			runs += updateOutCornerGB(player);
		else if (outs <= 2 && hitDestination > 0 && hitDestination < 6 && manOnFirst() && randPlay < 9)
			runs += updateOutDoublePlay(player);
		else if (outs <= 2 && hitDestination > 0 && hitDestination < 6 && manOnFirst() && randPlay > 8)
			runs += updateFielderChoice(player);
		else if(hitDestination == 1) 
			printLog(player + " hits a fieldable ball to " + Game.numberToPosition(hitDestination - 1) + ".\n");
		if (sac == false)
			player.gbAB++;
		return runs;
    }

	private int updateOutError(Player player) {
		int runs = 0;
		printLog(field[hitDestination] + " commits a fielding error!\n");
		outs--;
		field[0].gpTO--;
		field[hitDestination].gE++;
		if (manOnThird())
			runs += advanceFrom(3, 1);
		if (manOnSecond())
			runs += advanceFrom(2, 1);
		if (manOnFirst())
			runs += advanceFrom(1, 1);
		placeRunner(1,player);	
		return runs;
	}

	private int updateOutBunt(Player player) {
		int runs = 0;
		if(player.position.equals("P")) {
			if(manOnThird() && outs < 3 && Math.abs(Game.aRuns - Game.hRuns) < 2 && Game.inning > 7) {
				if(randPlay < 4) {
					buntLocation(player);
					runs += advanceFrom(3, 1);
					field[0].gpER++;
					player.gbRBI++;
					sac = true;
				}
			}
			if(!manOnFirst() && manOnSecond() && !manOnThird() && outs < 3) {
				buntLocation(player);
				if(randPlay < 12) {
					runs += advanceFrom(2, 1);
					sac = true;
				}
				else {
					placeRunner(1, player);
					clearBase(2);
				}
			}
			else if(manOnFirst() && !manOnSecond() && outs < 3) {
				buntLocation(player);
				if(randPlay < 12) {
					runs += advanceFrom(1, 1);
					sac = true;
				}
				else {
					placeRunner(1, player);
				}
			}
			else if(manOnFirst() && manOnSecond() && !manOnThird() && outs < 3) {
				buntLocation(player);
				if(randPlay < 13) {
					runs += advanceFrom(2, 1);
					runs += advanceFrom(1, 1);
					sac = true;
				}
				else {
					if(randPlay > 6) {
						runs += advanceFrom(1, 1);
						placeRunner(1, player);
					}
					else {
						runs += advanceFrom(2, 1);
						placeRunner(1, player);
					}
				}
			}
			else {
				if(!manOnFirst() && !manOnSecond() && !manOnThird())
					randomGrounder(player);
				else if(manOnFirst()) {
					randomGrounder(player);
					updateFielderChoice(player);
				}
				else {
					if(!manOnFirst() && !manOnSecond() && manOnThird()) {
						randomGrounder(player);
					}
					else if(!manOnFirst() && manOnSecond() && !manOnThird()) {
						buntLocation(player);
						runs += advanceFrom(2, 1);
					}
					else {
						buntLocation(player);
					}
				}
			}
		}
		else if(!player.position.equals("P")) {
			if(!manOnFirst() && !manOnSecond() && !manOnThird() && player.isFast() && player.bSLG < .425 && randPlay > 12) {
				buntLocation(player);
			}
			else if(manOnFirst() && !manOnSecond() && !manOnThird() &&
				   ((player.bSLG < .45 && randPlay > 4) || 
				   (player.bSLG < .475 && Math.abs(Game.aRuns - Game.hRuns) < 2 && Game.inning > 6))) {
				buntLocation(player);
				if(randPlay < 12) {
					runs += advanceFrom(1, 1);
					sac = true;
				}
				else if(randPlay < 16) {
					placeRunner(1, player);
				}
			}
			else if(manOnFirst() && manOnSecond() && !manOnThird() &&
					((player.bSLG < .45 && randPlay > 4) || 
				    (player.bSLG < .475 && Math.abs(Game.aRuns - Game.hRuns) < 2 && Game.inning > 6))) {
				buntLocation(player);
				if(randPlay < 10) {
					runs += advanceFrom(2, 1);
					runs += advanceFrom(1, 1);
					sac = true;
				}
				else if(randPlay < 13) {
					runs += advanceFrom(2, 1);
					placeRunner(1, player);
				}
				else if(randPlay < 16) {
					runs += advanceFrom(1, 1);
					placeRunner(1, player);
				}
			}
			else if(!manOnFirst() && manOnSecond() && !manOnThird() &&
					((player.bSLG < .45 && randPlay > 4) || 
					(player.bSLG < .475 && Math.abs(Game.aRuns - Game.hRuns) < 2 && Game.inning > 6))) {
				buntLocation(player);
				if(randPlay < 12) {
					runs += advanceFrom(2, 1);
					sac = true;
				}
				else {
					placeRunner(1, player);
				}
			}		
			else if(manOnFirst() && !manOnSecond() && manOnThird() &&
					((player.bSLG < .45 && randPlay > 4) || 
				    (player.bSLG < .475 && Math.abs(Game.aRuns - Game.hRuns) < 2 && Game.inning > 6))) {
				buntLocation(player);
				if(randPlay < 11) {
					runs += advanceFrom(1, 1);
					sac = true;
					if(randPlay < 1 && basepaths[3].isFast()) {
						runs += advanceFrom(3, 1);
						field[0].gpER++;
						player.gbRBI++;
					}
				}
				else if(randPlay < 13) {
					placeRunner(1, player);
					if(randPlay < 1 && basepaths[3].isFast()) {
						runs += advanceFrom(3, 1);
						field[0].gpER++;
						player.gbRBI++;
					}
				}
				else {
					runs += advanceFrom(1, 1);
					placeRunner(1, player);
					clearBase(3);
				}
			}
			else if(manOnFirst() && manOnSecond() && manOnThird() &&
				   ((player.bSLG < .45 && randPlay > 8) || 
				   (player.bSLG < .475 && Math.abs(Game.aRuns - Game.hRuns) < 2 && Game.inning > 6))) {
				buntLocation(player);
				if(randPlay < 12) {
					runs += advanceFrom(3, 1);
					runs += advanceFrom(2, 1);
					runs += advanceFrom(1, 1);
					sac = true;
					field[0].gpER++;
					player.gbRBI++;
				}
				else if(randPlay < 14) {
					runs += advanceFrom(3, 1);
					runs += advanceFrom(1, 1);
					placeRunner(1, player);
					field[0].gpER++;
					player.gbRBI++;
				}
				else {
					runs += advanceFrom(2, 1);
					runs += advanceFrom(1, 1);
					placeRunner(1, player);
				}
			}
			else if(!manOnFirst() && manOnSecond() && manOnThird() &&
					((player.bSLG < .45 && randPlay > 8) || 
					(player.bSLG < .475 && Math.abs(Game.aRuns - Game.hRuns) < 2 && Game.inning > 6))) {
				buntLocation(player);
				if(randPlay < 12) {
					runs += advanceFrom(3, 1);
					runs += advanceFrom(2, 1);
					sac = true;
					field[0].gpER++;
					player.gbRBI++;
				}
				else if(randPlay < 14) {
					runs += advanceFrom(3, 1);
					placeRunner(1, player);
					clearBase(2);
					field[0].gpER++;
					player.gbRBI++;
				}
				else {
					runs += advanceFrom(2, 1);
					placeRunner(1, player);
				}
			}
			else if(!manOnFirst() && !manOnSecond() && manOnThird() &&
					((player.bSLG < .45 && randPlay > 6) || 
					(player.bSLG < .475 && Math.abs(Game.aRuns - Game.hRuns) < 2 && Game.inning > 6))) {
				buntLocation(player);
				if(randPlay < 13) {
					runs += advanceFrom(3, 1);
					field[0].gpER++;
					player.gbRBI++;
					sac = true;
				}
				else {
					placeRunner(1, player);
					clearBase(3);
				}
			}
			else {
				if(!manOnFirst() && !manOnSecond() && !manOnThird())
					randomGrounder(player);
				else if(manOnFirst()) {
					randomGrounder(player);
					updateFielderChoice(player);
				}
				else {
					if(!manOnFirst() && !manOnSecond() && manOnThird()) {
						randomGrounder(player);
					}
					else if(!manOnFirst() && manOnSecond() && !manOnThird()) {
						buntLocation(player);
						runs += advanceFrom(2, 1);
					}
					else {
						buntLocation(player);
					}
				}
			}
		}
		else {
			if(!manOnFirst() && !manOnSecond() && !manOnThird())
				randomGrounder(player);
			else if(manOnFirst()) {
				randomGrounder(player);
				updateFielderChoice(player);
			}
			else {
				if(!manOnFirst() && !manOnSecond() && manOnThird()) {
					randomGrounder(player);
				}
				else if(!manOnFirst() && manOnSecond() && !manOnThird()) {
					buntLocation(player);
					runs += advanceFrom(2, 1);
				}
				else {
					buntLocation(player);
				}
			}
		}
		return runs;
	}

	private int updateOutSacFly(Player player) {
		sac = false;
		int runs = 0;
		if (!manOnSecond() && manOnThird()) {
			printLog("The hit is a sac fly.\n");
			runs += advanceFrom(3, 1);
			player.gbRBI++;
			field[0].gpER++;
			sac = true;
		}
		else if (manOnSecond() && !manOnThird()) {
			if(hitDestination == 8 || (hitDestination == 7 && basepaths[2].isFast())) {
				printLog("The hit is a sac fly.\n");
				runs += advanceFrom(2, 1);
				sac = true;
			}
		}
		else if (manOnSecond() && manOnThird()) {
			printLog("The hit is a sac fly.\n");
			runs += advanceFrom(3, 1);
			player.gbRBI++;
			field[0].gpER++;
			sac = true;
			if(hitDestination == 8) {
				runs += advanceFrom(2, 1);
				sac = true;
			}
		}	
		return runs;
	}

	private int updateOutRightSideGB(Player player) {
		int runs = 0;
		if (manOnThird() && basepaths[3].isFast()) {
			runs += advanceFrom(3, 1);
			player.gbRBI++;
			field[0].gpER++;
		}
		if ((manOnSecond() && !manOnThird() && basepaths[2].isFast()) || 
			(manOnSecond() && manOnThird() && basepaths[3].isFast())) {
			runs += advanceFrom(2, 1);
		}
		return runs;
	}

	private int updateOutCornerGB(Player player) {
		int runs = 0;
		runs += advanceFrom(1, 1);
		clearBase(3);
		return runs;
	}

	private int updateOutDoublePlay(Player player) {
		infieldIn = false;
		int runs = 0;
		printLog("The infield turns a double play!\n");
		if (outs + 1 == 3) {
			outs++;
			field[0].gpTO++;
		}
		else if (outs + 1 < 3) {
			if (!manOnSecond() && !manOnThird()) {
				clearBase(2);
				clearBase(1);
			}
			else if (manOnSecond() && !manOnThird()) {
				if(hitDestination == 4 || hitDestination == 5) {
					if(basepaths[1].speedScore() > player.speedScore()) {
						runs += advanceFrom(1, 1);
						clearBase(3);
						clearBase(1);
					}
					else {
						placeRunner(1, player);
						clearBase(3);
						clearBase(2);
					}
				}
				else {
					runs += advanceFrom(2, 1);
					clearBase(2);
					clearBase(1);
				}
			}
			else if (!manOnSecond() && manOnThird()) {
				if(Game.inning >= 7 && (Game.aRuns == Game.hRuns || Math.abs(Game.aRuns - Game.hRuns) == 1)) {
					clearBase(3);
					runs += advanceFrom(1, 1);
				}
				else {
					runs += advanceFrom(3, 1);
					clearBase(1);
					field[0].gpER++;
				}
			}
			else if (manOnSecond() && manOnThird()) {
				if(basepaths[3].isFast() || (Game.inning >= 7 && (Game.aRuns == Game.hRuns || Math.abs(Game.aRuns - Game.hRuns) == 1))) {
					infieldIn = true;
				}
				if(infieldIn) {
					runs += advanceFrom(2, 1);
					runs += advanceFrom(1, 1);
					clearBase(1);
				}
				else {
					if(hitDestination == 4) {
						randPlay = rand.nextInt(3);
						if(randPlay == 0) {
							runs += advanceFrom(2, 1);
							runs += advanceFrom(1, 1);
						}
						else if(randPlay == 1) {
							runs += advanceFrom(3, 1);
							runs += advanceFrom(2, 1);
							clearBase(1);
							field[0].gpER++;
						}
						else {
							runs += advanceFrom(1, 1);
							placeRunner(1, player);
							clearBase(3);
						}
					}
					else if(hitDestination == 5) {
						randPlay = rand.nextInt(2);
						if(randPlay == 0) {
							runs += advanceFrom(3, 1);
							runs += advanceFrom(1, 1);
							field[0].gpER++;
						}
						else {
							runs += advanceFrom(3, 1);
							runs += advanceFrom(2, 1);
							clearBase(1);
							field[0].gpER++;
						}
					}
					else {
						runs += advanceFrom(3, 1);
						runs += advanceFrom(2, 1);
						clearBase(1);
						field[0].gpER++;
					}
				}
			}
			field[0].gpTO++;
			outs++;
		}
		return runs;
	}

    private int updateFielderChoice(Player player) {
    	infieldIn = false;
    	int runs = 0;
		printLog("The play results in a fielder's choice. ");
		if (!manOnSecond() && !manOnThird()) {
			if(randPlay < 11) {
				printLog(basepaths[1] + " is out at " + Game.numberToPosition(3) + ".\n");
				placeRunner(1, player);
			}
			else {
				printLog(player + " is out at " + Game.numberToPosition(2) + ".\n");
				runs += advanceFrom(1, 1);
			}
		}
		else if (manOnSecond() && !manOnThird()) {
			if(hitDestination == 0 || hitDestination == 1 || hitDestination == 4 || hitDestination == 5) {
				if(randPlay < 9) {
					printLog(basepaths[2] + " is out at " + Game.numberToPosition(4) + ".\n");
					runs += advanceFrom(1, 1);
					placeRunner(1, player);
				}
				else if(randPlay < 12) {
					printLog(basepaths[1] + " is out at " + Game.numberToPosition(3) + ".\n");
					runs += advanceFrom(2, 1);
					placeRunner(1, player);
				}
				else {
					printLog(player + " is out at " + Game.numberToPosition(2) + ".\n");
					runs += advanceFrom(2, 1);
					runs += advanceFrom(1, 1);
				}
			}
			else {
				if(randPlay < 9) {
					printLog(basepaths[1] + " is out at " + Game.numberToPosition(3) + ".\n");
					runs += advanceFrom(2, 1);
					placeRunner(1, player);
				}
				else {
					printLog(player + " is out at " + Game.numberToPosition(2) + ".\n");
					runs += advanceFrom(2, 1);
					runs += advanceFrom(1, 1);
				}
			}
		}
		else if (!manOnSecond() && manOnThird()) {
			if(Game.inning >= 7 && (Game.aRuns == Game.hRuns || Math.abs(Game.aRuns - Game.hRuns) == 1)) {
				printLog(basepaths[3] + " is out at home.\n");
				clearBase(3);
				runs += advanceFrom(1, 1);
				placeRunner(1, player);
			}
			else {
				if(randPlay > 13) {
					printLog(basepaths[3] + " is out at home.\n");
					clearBase(3);
					runs += advanceFrom(1, 1);
					placeRunner(1, player);
				}
				else if(randPlay > 11) {
					printLog(basepaths[1] + " is out at " + Game.numberToPosition(3) + ".\n");
					runs += advanceFrom(3, 1);
					placeRunner(1, player);
					field[0].gpER++;
				}
				else {
					printLog(player + " is out at " + Game.numberToPosition(2) + ".\n");
					runs += advanceFrom(3, 1);
					runs += advanceFrom(1, 1);
					field[0].gpER++;
				}
			}
		}
		else if (manOnSecond() && manOnThird()) {
			if(basepaths[3].isFast() || (Game.inning >= 7 && (Game.aRuns == Game.hRuns || Math.abs(Game.aRuns - Game.hRuns) == 1))) {
				infieldIn = true;
			}
			if(infieldIn) {
				printLog(basepaths[3] + " is out at home.\n");
				runs += advanceFrom(2, 1);
				runs += advanceFrom(1, 1);
				placeRunner(1, player);
			}
			else {
				infieldIn = false;
				if(hitDestination == 2) {
					randPlay = rand.nextInt(5);
					if(randPlay < 2) {
						printLog(basepaths[3] + " is out at home.\n");
						runs += advanceFrom(2, 1);
						runs += advanceFrom(1, 1);
						placeRunner(1, player);
					}
					else if(randPlay == 2) {
						printLog(basepaths[1] + " is out at " + Game.numberToPosition(3) + ".\n");
						runs += advanceFrom(3, 1);
						runs += advanceFrom(2, 1);
						placeRunner(1, player);
						field[0].gpER++;
					}
					else {
						printLog(player + " is out at " + Game.numberToPosition(2) + ".\n");
						runs += advanceFrom(3, 1);
						runs += advanceFrom(2, 1);
						runs += advanceFrom(1, 1);
						field[0].gpER++;
					}
				}
				else if(hitDestination == 4) {
					randPlay = rand.nextInt(6);
					if(randPlay < 2) {
						printLog(basepaths[3] + " is out at home.\n");
						runs += advanceFrom(2, 1);
						runs += advanceFrom(1, 1);
						placeRunner(1, player);
					}
					else if(randPlay == 2) {
						printLog(basepaths[2] + " is out at " + Game.numberToPosition(4) + ".\n");
						runs += advanceFrom(3, 1);
						runs += advanceFrom(1, 1);
						placeRunner(1, player);
						field[0].gpER++;
					}
					else if(randPlay == 3) {
						printLog(basepaths[1] + " is out at " + Game.numberToPosition(3) + ".\n");
						runs += advanceFrom(3, 1);
						runs += advanceFrom(2, 1);
						placeRunner(1, player);
						field[0].gpER++;
					}
					else {
						printLog(player + " is out at " + Game.numberToPosition(2) + ".\n");
						runs += advanceFrom(3, 1);
						runs += advanceFrom(2, 1);
						runs += advanceFrom(1, 1);
						field[0].gpER++;
					}
				}
				else if(hitDestination == 5) {
					randPlay = rand.nextInt(4);
					if(randPlay == 0) {
						printLog(basepaths[2] + " is out at " + Game.numberToPosition(4) + ".\n");
						runs += advanceFrom(3, 1);
						runs += advanceFrom(1, 1);
						placeRunner(1, player);
						field[0].gpER++;
					}
					else if(randPlay == 1) {
						printLog(basepaths[1] + " is out at " + Game.numberToPosition(3) + ".\n");
						runs += advanceFrom(3, 1);
						runs += advanceFrom(2, 1);
						placeRunner(1, player);
						field[0].gpER++;
					}
					else {
						printLog(player + " is out at " + Game.numberToPosition(2) + ".\n");
						runs += advanceFrom(3, 1);
						runs += advanceFrom(2, 1);
						runs += advanceFrom(1, 1);
						field[0].gpER++;
					}
				}
				else {
					randPlay = rand.nextInt(5);
					if(randPlay < 2) {
						printLog(basepaths[3] + " is out at home.\n");
						runs += advanceFrom(2, 1);
						runs += advanceFrom(1, 1);
						placeRunner(1, player);
					}
					else if(randPlay == 2) {
						printLog(basepaths[1] + " is out at " + Game.numberToPosition(3) + ".\n");
						runs += advanceFrom(3, 1);
						runs += advanceFrom(2, 1);
						placeRunner(1, player);
						field[0].gpER++;
					}
					else {
						printLog(player + " is out at " + Game.numberToPosition(2) + ".\n");
						runs += advanceFrom(3, 1);
						runs += advanceFrom(2, 1);
						runs += advanceFrom(1, 1);
						field[0].gpER++;
					}
				}
			}
		}
		return runs;
	}

	private int advanceFrom(int base, int bases) {
    	int runs = 0;
    	if((base + bases) > 3) {
    		basepaths[0] = basepaths[base];
    		basepaths[base] = null;
    		runs++;
    		basepaths[0].gbR++;
    		printLog("\t" + basepaths[0] + " scores.\n");
    		if(field[0].equals(Game.awayP))
    			Game.hInnRuns[Game.inning - 1]++;
    		else if(field[0].equals(Game.homeP))
    			Game.aInnRuns[Game.inning - 1]++;
    	}
    	else {
    		basepaths[base + bases] = basepaths[base];
    		basepaths[base] = null;
    		printLog("\t" + basepaths[base + bases] + " advances to " + (base + bases) + "B.\n");
    	}
    	return runs;
    }

    private String singleLocation() {
    	if(hitDestination == 0) {
    		return "a weak ground ball";
    	}
    	else if(hitDestination == 1) {
    		return "a ground ball down the " + Game.numberToPosition(2) + " line";
    	}
    	else if(hitDestination == 2) {
    		return "a ground ball between " + Game.numberToPosition(2) + " and " + Game.numberToPosition(3);
    	}
    	else if(hitDestination == 3) {
    		return "a ground ball up the middle";
    	}
    	else if(hitDestination == 4) {
    		return "a ground ball between " + Game.numberToPosition(4) + " and " + Game.numberToPosition(5);
    	}
    	else if(hitDestination == 5) {
    		return "a ground ball down the " + Game.numberToPosition(4) + " line";
    	}
    	else if(hitDestination == 6) {
    		return "a line drive to " + Game.numberToPosition(6);
    	}
    	else if(hitDestination == 7) {
    		return "a line drive to " + Game.numberToPosition(7);
    	}
    	else {
    		return "a line drive to " + Game.numberToPosition(8);
    	}
    }
    
    private void buntLocation(Player player) {
		int bunt = rand.nextInt(12);
		if(bunt > 8) {
			printLog(player + " bunts to " + Game.numberToPosition(hitDestination + 3) + ".\n");
		}
		else if(bunt > 5) {
			printLog(player + " bunts to " + Game.numberToPosition(hitDestination + 1) + ".\n");
		}
		else if(bunt > 2) {
			printLog(player + " bunts to the " + Game.numberToPosition(hitDestination - 1) + ".\n");
		}
		else {
			printLog(player + " bunts to the " + Game.numberToPosition(hitDestination) + ".\n");
		}
    }

    private void randomGrounder(Player player) {
    	int gb = rand.nextInt(12);
    	if(gb > 9) {
    		printLog(player + " hits a fieldable ball to " + Game.numberToPosition(hitDestination + 4) + ".\n");
    	}
    	else if(gb > 7) {
			printLog(player + " hits a fieldable ball to " + Game.numberToPosition(hitDestination + 3) + ".\n");
		}
		else if(gb > 5) {
			printLog(player + " hits a fieldable ball to " + Game.numberToPosition(hitDestination + 2) + ".\n");
		}
		else if(gb > 3) {
			printLog(player + " hits a fieldable ball to " + Game.numberToPosition(hitDestination + 1) + ".\n");
		}
		else if(gb > 1) {
			printLog(player + " hits a fieldable ball to " + Game.numberToPosition(hitDestination) + ".\n");
		}
		else {
			printLog(player + " hits a fieldable ball to " + Game.numberToPosition(hitDestination - 1) + ".\n");
		}
    }
    
	private void printLog(String log) {
    	w.print(log);
    }
	
	private void clearBase(int base) {
		basepaths[base] = null;
	}
	
	private void placeRunner(int base, Player player) {
		basepaths[base] = player;
		printLog("\t" + player + " reaches " + base + "B.\n");
	}
	
	public static boolean manOnFirst() {
		if(basepaths[1] != null)
			return true;
		else
			return false;
	}
	public static boolean manOnSecond() {
		if(basepaths[2] != null)
			return true;
		else
			return false;
	}
	public static boolean manOnThird() {
		if(basepaths[3] != null)
			return true;
		else
			return false;
	}
	
	public void calcPitchCount() {
		int baserunning = 0;
		if(field[0].equals(Game.awayP)) {
			for(Player p : Game.home.gameBatters) {
				baserunning += (p.gbSB + p.gbCS);
			}
		}
		else if(field[0].equals(Game.homeP)) {
			for(Player p : Game.away.gameBatters) {
				baserunning += (p.gbSB + p.gbCS);
			}
		}
		field[0].gPitchCount = ((baserunning * 1.25) + (4.95*field[0].gpK) + (5.35*field[0].gpBB) + (4.25*(field[0].gpH+field[0].gpHBP)) + (3.55*(field[0].gpTO - field[0].gpK)));
	}
	
	public int getOuts() {
		return outs;
	}
}
