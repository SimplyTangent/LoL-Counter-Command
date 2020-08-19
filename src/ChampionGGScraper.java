import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;
import java.util.Scanner;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ChampionGGScraper {




	public static Map<String, Number[]> getChampionCounters(String championName, String role, WebDriver driver) {

		Map<String, Number[]> rows = new LinkedHashMap<String, Number[]>();
		// champion.gg url for champion data.
		String baseUrl = "https://champion.gg/champion/" + championName + "/" + role;
		WebElement counterMatchups;

		driver.get(baseUrl);
		
		if(!driver.getCurrentUrl().equals(baseUrl)) {
			return rows;
		}

		try {

			WebElement showMoreElement = driver.findElement(By.className("show-more"));
			String fractionOfChampionsLeft = showMoreElement.findElement(By.tagName("small")).getText();
			Integer denominator = Integer.parseInt(fractionOfChampionsLeft.substring(fractionOfChampionsLeft.indexOf('/') + 2, fractionOfChampionsLeft.lastIndexOf(' ')));

			for(int i = 0; i < denominator / 5; i++) {
				Actions action = new Actions(driver);
				action.moveToElement(showMoreElement).perform();
				action.moveToElement(showMoreElement).click().perform();
			}

		} catch (Exception e) {
			System.out.println("Small amount of matchups.");
		}



		counterMatchups = driver.findElement(By.tagName("matchups"));
		String rawData = counterMatchups.getText();

		Scanner refinerScanner = new Scanner(rawData);

		while(refinerScanner.hasNextLine()) {
			String championCounter = refinerScanner.nextLine();
			if(!refinerScanner.hasNextLine()) break;
			String gamesPlayed = refinerScanner.nextLine().split(" ")[0];
			String winrate = refinerScanner.nextLine();

			rows.put(championCounter, new Number[] { Double.parseDouble(winrate.substring(0, winrate.length() - 2)), 
					Integer.parseInt(gamesPlayed) });
		}

		refinerScanner.close();
		System.out.println(championName + " " + role + " champion.gg's data has been scraped.");
		return rows;


	}




}