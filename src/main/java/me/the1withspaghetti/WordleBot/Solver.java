package me.the1withspaghetti.WordleBot;

import java.util.HashSet;
import java.util.LinkedList;

public class Solver {
	
	private SiteEventListener listener;
	private LinkedList<char[]> words;
	
	public Solver(LinkedList<char[]> wordList, SiteEventListener listener) {
		this.words = wordList;
		this.listener = listener;
	}
	
	/**
	 *  Begins a new game of Wordle, using the set SiteEventListener and wordList for the dictionary.
	 *  
	 *  @param maxGuesses
	 *  	The maximum amount of guesses to submit before giving up. Is usually 6 on the website
	 *  @returns The last guess made before stopping the solving process, will be the answer if the game was successfully won.
	 */
	public char[] solveWordle(int maxGuesses) {
		
		// Saved game data used for making guesses
		LinkedList<char[]> guesses = new LinkedList<>();
		HashSet<Character> grays = new HashSet<>();
		char[] greens = {' ',' ',' ',' ',' '};
		char[] yellows = {' ',' ',' ',' ',' '};
		
		while (true) {
			char[] guess = null;
			int finds = 0;
			if (guesses.isEmpty()) {
				// On the first guess, take the first word from the word list
				guess = words.peek();
			} else {
				// On all other guesses, find the first word that matches the criteria
				LinkedList<char[]> matches = findMatches(greens, new char[][] {yellows}, grays.toString().toCharArray(), maxGuesses);
				
				for (char[] match: matches) {
					finds++;
					if (guess == null && !guesses.contains(match))
						guess = match;
				}
				if (guess == null) {
					System.err.println("Could not find a result!");
					return guess;
				}
			}
			
			// Sends the guess to the website and returns the result in the form of an int list.
			System.out.println("Guessing "+new String(guess)+" out of "+finds);
			int[] res = listener.sendWord(guess, guesses.size());
			guesses.add(guess);
			System.out.println(new String(guess)+" retured "+printInts(res));
			
			// Check for a win or if the solver is out of guesses
			if (checkAll(res)) {
				System.out.println("----- Found Result: \""+new String(guess)+"\" on turn "+guesses.size()+" -----");
				return guess;
			} else if (guesses.size() >= maxGuesses) {
				System.err.println("----- Could not find result! ----- (narrowed down to "+finds+" words)");
				return guess;
			}
			
			// Resets the green and yellow game data and inputs the new data from the received result
			greens = new char[] {' ',' ',' ',' ',' '};
			yellows = new char[] {' ',' ',' ',' ',' '};
			for (int i = 0; i < 5; i++) {
				if (res[i] == 0)
					grays.add(guess[i]);
			}
			for (int i = 0; i < 5; i++) {
				if (res[i] == 1) {
					yellows[i] = guess[i];
					grays.remove(guess[i]);
				}
				else if (res[i] == 2) {
					greens[i] = guess[i];
					grays.remove(guess[i]);
				}
					
			}
		}
	}
	
	/**
	 * 
	 * @param greens 
	 * 		A 5-long list of slots will letters for known greens and spaces for unknown.
	 * @param yellows
	 * 		A 5-long list of lists that contain all yellows that haven been in that slot.
	 * @param grays
	 * 		A list of all gray letters
	 * @param max
	 * 		The max amount of results to return
	 * @return A list of all words that match the given criteria
	 */
	public LinkedList<char[]> findMatches(char[] greens, char[][] yellows, char[] grays, int max) {
		LinkedList<char[]> results = new LinkedList<char[]>();
		for (char[] test: words) {
			if (checkMatch(test,greens,yellows,grays)) {
				results.add(test);
				if (results.size() >= max) return results;
			}
				
		}
		return results;
	}
	
	/**
	 * Takes in a test word and returns true if the word matches the criteria.
	 * 
	 * @param test
	 * 		A 5-long list of chars for the word to be tested
	 * @param greens 
	 * 		A 5-long list of slots will letters for known greens and spaces for unknown.
	 * @param yellows
	 * 		A 5-long list of lists that contain all yellows that haven been in that slot.
	 * @param grays
	 * 		A list of all gray letters
	 * @return a boolean, true if the test word matches the criteria, otherwise false
	 */
	private static boolean checkMatch(char[] test, char[] greens, char[][] yellows, char[] grays) {
		// Checking for greens in the start for efficiency
		for (int i = 0; i < 5; i++) {
			if (greens[i] != ' ' && greens[i] != test[i])
				return false;
		}
		
		for (int i = 0; i < 5; i++) {
			char x = test[i];
			for (char c: grays) {
				if (c == x)
					return false;
			}
		}
		boolean[] gused = new boolean[5];
		// Sets greens that have been used
		for (int i = 0; i < 5; i++) {
			if (greens[i] == test[i]) {
				gused[i] = true;
			}
		}
		for (int line = 0; line < yellows.length; line++) {
			boolean[] yused = new boolean[5];
			for (int i = 0; i < 5; i++) {
				char x = yellows[line][i];
				if (x != ' ') {
					boolean found = false;
					for (int l = 0; l < 5; l++) {
						char y = test[l];
						if (x == y && i != l && yused[l] != true) {
							found = true;
							yused[l] = true;
						}
					}
					if (found == false) {
						return false;
					}
				}
			}
		}
		return true;
	}
	
	/**
	 * Takes in the result of a guess and returns true if all guesses were green
	 * 
	 * @param res
	 * 		The result of a guess, a 5-long array of ints from 0 to 2
	 * @return true if all the ints are equal to 2
	 */
	private static boolean checkAll(int[] res) {
		for (int i: res)
			if (i != 2) return false;
		return true;
	}
	
	/**
	 * Takes in an array of ints and combines them as a string.
	 * 
	 * Example: {1,2,3,4,5} returns "12345"
	 * 
	 * @param ints
	 * 		The array of integers to be printed
	 * @return A String containing all the ints combined
	 */
	private static String printInts(int[] ints) {
		StringBuilder str = new StringBuilder();
		for (int i: ints)
			str.append(i);
		return str.toString();
	}
}
