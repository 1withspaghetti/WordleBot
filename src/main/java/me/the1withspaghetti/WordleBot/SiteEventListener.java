package me.the1withspaghetti.WordleBot;

public interface SiteEventListener {
	
	/*
	 * Sends and checks the word and returns the result as a int[5] array
	 * 
	 * Within the returned array, the following is used:
	 *   0 = gray (no significance)
	 *   1 = yellow (in word, wrong spot)
	 *   2 = green (correct spot)
	 * 
	 */
	public int[] sendWord(char[] word, int row);
	
}
