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
	
	public char[] solveWordle(int maxGuesses) {
		LinkedList<char[]> guesses = new LinkedList<>();
		HashSet<Character> grays = new HashSet<>();
		char[] greens = {' ',' ',' ',' ',' '};
		char[] yellows = {' ',' ',' ',' ',' '};
		while (true) {
			char[] guess = null;
			int finds = 0;
			if (guesses.isEmpty()) {
				//guess = new String("soare").toCharArray();
				guess = words.peek();
			} else {
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
			System.out.println("Guessing "+new String(guess)+" out of "+finds);
			int[] res = listener.sendWord(guess, guesses.size());
			guesses.add(guess);
			greens = new char[] {' ',' ',' ',' ',' '};
			yellows = new char[] {' ',' ',' ',' ',' '};
			System.out.println(new String(guess)+" retured "+printInts(res));
			if (checkAll(res)) {
				System.out.println("----- Found Result: \""+new String(guess)+"\" on turn "+guesses.size()+" -----");
				return guess;
			} else if (guesses.size() >= maxGuesses) {
				System.err.println("----- Could not find result! ----- (narrowed down to "+finds+" words)");
				return guess;
			}
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
	
	private static boolean checkAll(int[] res) {
		for (int i: res)
			if (i != 2) return false;
		return true;
	}
	
	private static String printInts(int[] ints) {
		StringBuilder str = new StringBuilder();
		for (int i: ints)
			str.append(i);
		return str.toString();
	}
}
