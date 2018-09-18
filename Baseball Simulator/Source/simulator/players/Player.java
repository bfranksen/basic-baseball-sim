/*======================================

Project:       Baseball Simulator
File:          Player.java
Author:        Braden Franksen
Date:          Mar 14, 2017

======================================*/
package simulator.players;

/**
 * @author Braden
 *
 */
public class Player {

	private int cachedHashCode;
	// player info
	public String role, position, name, LR, prePosition;
	public int age;
	
	// batting stats from base file
	public double bG, bPA, bAB, bR, bH, b2B, b3B, bHR, bRBI, bSB, bCS, bBB, bK, bAVG, bOBP, bSLG, bOPS, bOPSplus, bTB, bGDP, bHBP, bSH, bSF, bIBB;
	// batting stats used for simulation
	public double bSpeed, b1BAvg, b2BAvg, b3BAvg, bHRAvg, bBBAvg, bKAvg, bHBPAvg;
	
	// pitching stats from base file
	public double pW, pL, pWLP, pERA, pG, pGS, pGF, pCG, pSHO, pS, pIP, pH, pR, pER, pHR, pBB, pIBB, pK, pHBP, pBK, pWP, pBF, pERAplus, pFIP, pWHIP, pH9, pHR9, pBB9, pK9, pKperP;
	// pitching stats used for simulation
	public double p1BAvg, p2BAvg, p3BAvg, pHRAvg, pBBAvg, pKAvg, pHBPAvg;
	
	// fielding stats from base file
	public double fPO, fA, fE;
	// fielding stats used for simulation
	public double fFieldPct;
	
	// game hitting stats
	public double gbPA, gbAB, gbR, gbH, gb2B, gb3B, gbHR, gbRBI, gbSB, gbCS, gbBB, gbK, gbTB, gbGDP, gbHBP, gbSH, gbSF, gbIBB;
	// game pitching stats
	public double gpTO, gPitchCount, gpIP, gpER, gpH, gpR, gpHR, gpBB, gpK, gpHBP; 
    // game fielding stats
	public double gPO, gA, gE;
 
	// season hitting stats
	public double sbG, sbPA, sbAB, sbR, sbH, sb2B, sb3B, sbHR, sbRBI, sbSB, sbCS, sbBB, sbK, sbAVG, sbOBP, sbSLG, sbOPS, sbOPSplus, sbTB, sbGDP, sbHBP, sbSH, sbSF, sbIBB;	
	// season pitching stats
	public double spIP, spER, spTO, spH, spR, spHR, spBB, spK, spHBP;
    // season fielding stats
	public double sfPO, sfA, sfE, sfFieldPct;

	public Player(String position, String name, String LR, String role) {
		this.position = position;
		this.name = name;
		this.LR = LR;
		this.role = role;
		this.cachedHashCode = computeHashCode();
	}

	private int computeHashCode() {
		int result = 1;
		result = 31 * result + ((position == null) ? 0 : position.hashCode());
		result = 31 * result + ((name == null) ? 0 : name.hashCode());
		result = 31 * result + ((LR == null) ? 0 : LR.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object other) {
		if(this == other) {
			return true;
		}
		if(!(other instanceof Player) || other == null)
			return false;
		Player p = (Player) other;
		return name == p.name && position == p.position && LR == p.LR;
	}

	@Override
	public int hashCode() {
		return this.cachedHashCode;
	}

	public void setBaseAvg() {
		if(bPA > 0) {
			b1BAvg = (bH - (b2B + b3B + bHR)) / bPA;
			b2BAvg = b2B / bPA;
			b3BAvg = b3B / bPA;
			bHRAvg = bHR / bPA;
			bBBAvg = bBB / bPA;
			bKAvg = bK / bPA;
			bHBPAvg = bHBP / bPA;
			bAVG = bH / bAB;
			bOBP = (bH + bBB + bHBP) / (bAB + bBB + bHBP + bSH + bSF);
			bSLG = ((bH - b2B - b3B - bHR) + (2 * b2B) + (3 * b3B) + (4 * bHR)) / bAB;
			bOPS = bOBP + bSLG;
		}
		if(position.equals("P")) {
			p2BAvg = (pH * 0.174) / pBF;
			p3BAvg = (pH * 0.024) / pBF;
			pHRAvg = pHR / pBF;
			p1BAvg = (pH / pBF) - p2BAvg - p3BAvg - pHRAvg;
			pBBAvg = pBB / pBF;
			pKAvg = pK / pBF;
			pHBPAvg = pHBP / pBF;
		}
		if(fPO > 0) {
			fFieldPct = (fPO + fA) / (fPO + fA + fE);
		}
	}
	
	// TODO need to finish this(update stats during season)
/*	public void setSeasonAvg() {
		if(sbPA > 0) {
			b1BAvg = (sbH - (sb2B + sb3B + sbHR)) / sbPA;
			b2BAvg = sb2B / sbPA;
			b3BAvg = sb3B / sbPA;
			bHRAvg = sbHR / sbPA;
			bBBAvg = sbBB / sbPA;
			bKAvg = sbK / sbPA;
			bHBPAvg = sbHBP / sbPA;
			sbAVG = sbH / sbAB;
			sbOBP = (sbH + sbBB + sbHBP) / (sbAB + sbBB + sbHBP + sbSH + sbSF);
			sbSLG = ((sbH - sb2B - sb3B - sbHR) + (2 * sb2B) + (3 * sb3B) + (4 * sbHR)) / sbAB;
			sbOPS = sbOBP + sbSLG;
		}
		if(position.endsWith("P")) {
			p2BAvg = (spH * 0.174) / spBF;
		}
	}*/
	
	public double speedScore() {
		bSpeed = 0;
		bSpeed += (((bSB + 3) / (bSB + bCS + 5)) + (bSB * .01 - bCS * .01));
		if(position.equals("2B") || position.equals("LF") || position.equals("RF") ||
		   position.equals("OF") || position.equals("MI") || position.equals("UT") ||
		   (position.equals("DH") && (role.equals("IF") || role.equals("OF") || role.equals("b")))) {
			bSpeed += bSpeed * .15;
		}
		else if(position.equals("SS") || position.equals("CF")) {
			bSpeed += bSpeed * .25;
		}
		else {
			bSpeed -= bSpeed * .35;
		}
		bSpeed += .4 * b2B / bH;
		bSpeed += 2.5 * b3B / bH;
		return bSpeed;
	}
	
	public boolean isFast() {
		return speedScore() > .75;
	}
	
	@Override
	public String toString() {
		return this.name.replaceAll("-", " ");
	}
}
