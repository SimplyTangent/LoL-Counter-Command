import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;
import java.util.Scanner;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class OPGGScraper {

	public static void main(String[] args) throws Exception {


	}
	
	
	
	public static LinkedHashMap<String, Number[]> getChampionCounters(String championName, String role, WebDriver driver) {
		
		LinkedHashMap<String, Number[]> championCounters = new LinkedHashMap<>();
		


		String baseUrl = "http://na.op.gg/champion/" + championName + "/statistics/" + role;
		WebElement counterMatchups;
		
		driver.get(baseUrl);
		
		// enter counter page.
		WebElement counterLink = driver.findElement(By.linkText("Counters"));
		counterLink.click();
		
		// sort WR in decending order (click twice)
		List<WebElement> sortByWinRateButtonCheck = driver.findElements(By.className("champion-matchup-sort__button"));
		if(sortByWinRateButtonCheck.size() <= 0) {
			return championCounters;
		}
		WebElement sortByWinRateButton = sortByWinRateButtonCheck.get(0);
		sortByWinRateButton.click();
		sortByWinRateButton.click();
		
		ArrayList<WebElement> matchups = (ArrayList<WebElement>) driver.findElements(By.className("champion-matchup-champion-list__item"));

		for(WebElement webE: matchups) {
			Scanner matchupRefiner = new Scanner(webE.getText());
			String rank = matchupRefiner.nextLine();
			String championCounter = matchupRefiner.nextLine();
			String wr = matchupRefiner.nextLine();
			String playrateAndGames = matchupRefiner.nextLine();
			
			championCounters.put(championCounter, new Number[] 
					{ Double.parseDouble(wr.substring(0, wr.length() - 2)), 
					  Integer.parseInt(playrateAndGames.split(" ")[1].replaceAll("[\\W]", "")) });
			
		}
		
		System.out.println(championName + " " + role + " op.gg's data has been scraped.");
		return championCounters;
		
		
	}

}
