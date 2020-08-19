# LoL-Counter-Command
### Description
This is a java project made that uses Selenium to scrape OP.GG and Champion.GG for League of Legend's champion matchups and its winrates. It creates files in /Users/shared/ 
with the data. Additionally, you can add 3 lines of code to create a terminal command for counters.

### Prerequisites
This was tested in macOS. Linux should be fine. You may have to change the filepath in /src/ScraperControl.java for Windows. Chromedriver is used in this, but 
it can be easily changed to Firefoxdriver or any other driver.

### Terminal Command Setup
*(This is for MacOS and Linux)* Open Terminal. Type "cd ~/" and open ".bash_profile". If there is no bash profile, type "touch .bash_profile". In your bash profile,
add the following:
```bash
function counter() {
  sed -n 1,$3p /Users/Shared/'LoL Matchups'/$2/$1_counters.txt
}
```
Be careful of accidentally erasing anything in the bash profile.

## Usage
Run ScraperControl.java to scrape data and generate counters. To add a champion to championlist.txt, add the champion with "-31" to the end of its name.  
After setting up the terminal, you can use the counter command in the terminal like the following:
```
counter [CHAMPION_NAME] [ROLE] [AMOUNT OF COUNTERS]
counter garen top 10
```
With data from patch 10.16, you will see the following:
```
Quinn - 44.7% WR
Kayle - 46.1% WR
Teemo - 47.52% WR
Gnar - 47.89% WR
Vayne - 48% WR
Urgot - 48.23% WR
Camille - 48.66% WR
Darius - 49.03% WR
Tryndamere - 49.28% WR
Yorick - 49.5% WR
```

### Next Steps
I am looking to add support to excel to create a a workbook for the data. Additionally, I will add more websites to get more data.
