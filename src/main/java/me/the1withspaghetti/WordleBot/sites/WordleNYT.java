package me.the1withspaghetti.WordleBot.sites;

import org.openqa.selenium.chrome.ChromeDriver;
import io.github.bonigarcia.wdm.WebDriverManager;
import me.the1withspaghetti.WordleBot.FileLoader;
import me.the1withspaghetti.WordleBot.SiteEventListener;
import me.the1withspaghetti.WordleBot.Solver; 

public class WordleNYT {
	
	static ChromeDriver web;
	
	
	/*
	 * If an alternate site of wordle should be used to not expose today's wordle (and to loop games)
	 */
	public static final boolean ALT = true;
	private siteListener = new SiteEventListener();

	public static void main(String[] args) throws Exception {
		
		WebDriverManager.chromedriver().setup();
		web = new ChromeDriver();
		
		web.get((ALT ? "https://wordle.berknation.com/" : "https://www.powerlanguage.co.uk/wordle/"));
		
		
		Solver solver = new Solver(FileLoader.toLinkedList(FileLoader.loadJsonFromResource("/WordleNYT/ordered_words.json")), this.siteListener {
			@Override
			public int[] sendWord(char[] word, int row) {
				try {
					for (char c: word) {
						web.executeScript("document.querySelector(\"game-app\").$keyboard.dispatchKeyPressEvent(\""+c+"\")");
						Thread.sleep(25);
					}
					web.executeScript("document.querySelector(\"game-app\").$keyboard.dispatchKeyPressEvent(\"↵\")");
					Thread.sleep(1800);
					int[] res = new int[5];
					for (int i = 0; i < 5; i++) {
						String str = (String) web.executeScript("return document.querySelector(\"game-app\").$board.children["+row+"].$tiles["+i+"].getAttribute(\"evaluation\");");
						switch (str) {
							case "absent":
								res[i] = 0;
								break;
							case "present":
								res[i] = 1;
								break;
							case "correct":
								res[i] = 2;
								break;
						}
					}
					return res;
				} catch (Exception e) {
					e.printStackTrace();
					return null;
				}
			}
		});
		
		
		
		if (!ALT) {
			web.executeScript("document.querySelector(\"game-app\").$game.querySelector(\"game-modal\").style.display = \"none\"");
			solver.solveWordle(6);
		} else {
			while (true) {
				web.executeScript("window.randomWord()");
				web.executeScript("document.querySelector(\"game-app\").$game.querySelector(\"game-modal\").style.display = \"none\"");
				Thread.sleep(100);
				solver.solveWordle(6);
				Thread.sleep(500);
			}
		}
		
	}
}
