/*======================================

Project:       Baseball Simulator
File:          BaseballSimulator.java
Author:        Braden Franksen
Date:          Mar 20, 2017

======================================*/
package simulator;

import java.io.IOException;

import simulator.simulate.FullSeason;

/**
 * @author Braden
 *
 */
public class BaseballSimulator {
	
	//private static ArrayList<String> winners;
	
	public static void main(String[] args) throws IOException {
		chooseGamemode();
	}
	
	private static void chooseGamemode() throws IOException {
		//winners = new ArrayList<String>();
		FullSeason.runGames();
		FullSeason.getStatsAfterSeason();
	}
}
