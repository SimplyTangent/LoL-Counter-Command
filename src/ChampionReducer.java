import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import java.util.Scanner;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


public class ChampionReducer {
	
	final static long MIN_FILE_SIZE = 100; // change to reduce or increase the amount of champion data scraped.

	// Checks each file and reduces the roles for each champion to limit the amount of scraping the driver needs to do.
	public static void main(String[] args) throws Exception {
		
		
		String[] roles = new String[] { "Top", "Jungle", "Middle", "ADC", "Support" };
		Map<String, Integer> map = new TreeMap<>();
		
		for (int i = 0; i < roles.length; i++) {
			File[] files = new File("/Users/Shared/LoL Matchups/" + roles[i]).listFiles();
			for(File file : files) {
				System.out.println(file.getName());
				if(file.length() > MIN_FILE_SIZE) { 
					String champion = file.getName().split("_")[0];
					map.put(champion, ((1 << 4 - i) | map.getOrDefault(champion, 0)));
				}
			}
		}
		
		// Removes hidden file in macOS Folders
		map.remove(".DS");
		
		FileWriter myWriter = new FileWriter("championlist.txt");
		for(Map.Entry<String, Integer> entry : map.entrySet()) {
			myWriter.write(entry.getKey() + "-" + entry.getValue() + ",");
		}
		myWriter.close();
			
	}

}
