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
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
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

public class ScraperControl {


	public static void main(String[] args) throws Exception {

		// Scan list of champion and byte data.
		Scanner sc = new Scanner(new File("championlist.txt"));
		sc.useDelimiter("\t*,\t*");

		WebDriver driver = new ChromeDriver();
		System.setProperty("webdriver.chrome.driver", "/usr/local/bin/chromedriver");
		driver.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);

		Integer errorCount = 0;
		String nextChampion = sc.next();
		while(sc.hasNext()) {
			try {
				String[] championInfo = nextChampion.split("-");

				String champion = championInfo[0];
				champion = champion.replaceAll("[\\W]*", "");
				System.out.println(champion);

				Byte roleInfoBitmask = Byte.parseByte(championInfo[1]);

				String[] roles = new String[] { "Top", "Jungle", "Middle", "ADC", "Support" };

				// Each bit in the byte represents the role. A set bit means that there is substantial data for that role. 
				// EXAMPLE: 28 = 11100 = Top, Jungle, Mid
			
				for(int i = 4; i >= 0; i--) if ((roleInfoBitmask & (1 << i)) == 0) roles[4 - i] = "";
					
				

				for(String role : roles) {
					if(!role.equals("")) {
						// Change this for Windows.
						File championCountersFile = new File("/Users/Shared/LoL Matchups/" + role + "/" + champion.trim() + "_counters.txt");

						try {
							championCountersFile.delete();
						} catch(Exception e) {
							System.out.println("Error while deleting champion file.");
						}

						FileWriter myWriter = new FileWriter(championCountersFile.getPath());
						Map<String, Number[]> championCounters = new LinkedHashMap<>();

						championCountersFile.createNewFile();
						System.out.println(champion + " " + role + " file was created");

						// Get OP.GG and Champion.GG
						championCounters = OPGGScraper.getChampionCounters(champion, role.equals("Middle") ? "Mid" : role, driver);
						Map<String, Number[]> championGGCounters = ChampionGGScraper.getChampionCounters(champion, role, driver);

						// Add scraped data to LinkedHashMap using weighted averages.
						for(Map.Entry<String, Number[]> ccData : championGGCounters.entrySet()) {
							if(championCounters.containsKey(ccData.getKey())) {
								Number[] opData = championCounters.get(ccData.getKey());
								double sumOfGames = ccData.getValue()[1].intValue() + opData[1].intValue();
								double newWR = (opData[1].doubleValue() / sumOfGames * opData[0].doubleValue());
								newWR += ccData.getValue()[1].doubleValue() / sumOfGames *  ccData.getValue()[0].doubleValue();
								championCounters.put(ccData.getKey(), new Number[] { newWR, sumOfGames });
							} else {
								championCounters.put(ccData.getKey(), ccData.getValue());
							}
						}


						// Sort the champion data with merge sort with LinkedHashMap.
						mergeSort(championCounters);

						DecimalFormat df = new DecimalFormat("##.##");
						for (Map.Entry<String, Number[]> entrySet : championCounters.entrySet()) {
							myWriter.write(entrySet.getKey() + " - " + df.format(entrySet.getValue()[0].doubleValue()) + "% WR\n");// + entrySet.getValue()[1] + " games \n");
						}
						
						System.out.println(champion + " " + role + "'s file has been written.");
						myWriter.close();

					} 
				}
			} catch (Exception e) {
				// Try to get data again, break after 3 attempts.
				System.out.println(e);
				if(++errorCount >= 3) break;

			}
			nextChampion = sc.next();
		}
		driver.quit();
		System.out.println("The Driver has Quit.");
	}

	public static <T, A extends Number> void mergeSort(Map<T, A[]> map) {
		sort(map, 0, map.size() - 1);
	}

	public static <T, A extends Number> void merge(Map<T, A[]> map, int l, int m, int r) {

		int n1 = m - l + 1;
		int n2 = r - m;

		LinkedHashMap<T, A[]> L = new LinkedHashMap<>();
		LinkedHashMap<T, A[]> R = new LinkedHashMap<>();
		LinkedHashMap<T, A[]> newMap = new LinkedHashMap<>();

		int x = 0;
		for(Entry<T, A[]> pair : map.entrySet()) {
			if (x < n1) {
				L.put(pair.getKey(), pair.getValue());
			} else if(x++ < n2 + n1) {
				R.put(pair.getKey(), pair.getValue());
			} else {
				break;
			}
		}
		int i = 0, j = 0;

		ArrayList<Map.Entry<T, A[]>> lArray = new ArrayList<>();
		L.entrySet().stream().forEach(s -> lArray.add(s));
		ArrayList<Map.Entry<T, A[]>> rArray = new ArrayList<>();
		R.entrySet().stream().forEach(s -> rArray.add(s));

		while (i < lArray.size() && j < rArray.size()) { 
			if (lArray.get(i).getValue()[0].doubleValue() <= rArray.get(j).getValue()[0].doubleValue()) { 
				newMap.put(lArray.get(i).getKey(), lArray.get(i++).getValue()); 
			} 
			else { 
				newMap.put(rArray.get(j).getKey(), rArray.get(j++).getValue()); 
			}  
		} 
		while (i < lArray.size()) { 
			newMap.put(lArray.get(i).getKey(), lArray.get(i++).getValue());
		} 
		while (j < rArray.size()) { 
			newMap.put(rArray.get(j).getKey(), rArray.get(j++).getValue()); 
		} 

		map = newMap;
	}

	public static <T, A extends Number> void sort(Map<T, A[]> map, int l, int r) {
		if (l < r) {
			int m = (l + r) / 2;
			sort(map, l, m);
			sort(map, m + 1, r);

			merge(map, l, m, r);
		}
	}

}
