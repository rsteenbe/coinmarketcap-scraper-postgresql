# Coinmarketcap Scraper (PostgreSQL Database Storage)

*Author* : [Renco Steenbergen](https://www.linkedin.com/in/renco-steenbergen-87b52a119/)

*APIs* : [Coinmarketcap API](https://coinmarketcap.com/api/)

*Summary* : Scraper tool to store cryptocurrency data on a Raspberry Pi 3

*Technologies & Libraries* : IntelliJ IDEA, PostgreSQL 9.5, Notepad++, Raspbian GNU/Linux 8, Java SE 1.8.0, Slf4j 1.7.25, JSONSimple 1.1.1, Maven 3.5.0, Crontab

## Purpose
The main purpose of this tool is to obtain cryptocurrency data that can be used for research purposes or technical analysis. 
This Java scraper tool can be used to store 5 minute interval data of the cryptocurrencies, coins or tokens using the Coinmarketcap API. 
The data will be stored in a Postgres database.
This tool is designed to use on a Raspberry Pi and could be connected to a framework like Grafana, but it can also be used on other devices. 

## Requirements
* Raspberry Pi
* Java 8
* PostgreSQL 9.5
* Crontab

## Tables
Copy the script from the createtables.txt and create the tables in PostgreSQL.

## Variables
The variables that will be stored for the coins are: 

* Price in BTC
* Price in USD
* Daily Volume in USD
* Market Cap in USD
* Price in EUR
* Daily Volume in EUR
* Market Cap in EUR
* Total Supply
* Available Supply
* Percentual Change per Hour
* Percentual Change per 24 Hour
* Percentual Change per 7 Days
* Date as a Java Timestamp (with or without Time Zone)

All data will be stored as a double or timestamp.

## Properties
The following properties are added in `coinmarketcap-scraper.properties`:

* `coinmarketcap-scraper.coinMarketCapApi` refers to the URL of the Coinmarketcap API.
* `coinmarketcap-scraper.storageFolder` refers to the folder on your system where the data will be stored (Default: /tmp/coinmarketcap-data).

* `coins` refers to the coin ID from the Coinmarketcap API and decides which coins will be scraped. The coins property can be modified by adding other coins from the API.

## Installation
## Maven on Windows 7
Be sure that Maven is installed correctly on your machine. Use Maven to compile and package the tool by using the following commands in the Windows command line: `mvn clean install`.

## Crontab on Raspberry Pi 3
Copy the JAR-file `/target/coinmarketcap-scraper-1.1.jar` from the Windows machine to the Raspberry Pi and place it in the `/tmp` folder.

In the command line of the Raspberry Pi, type in the following command:
`crontab -e`

When the JAR-file is placed in the `/tmp` folder, add the following line at the bottom of this file:
`*/5 * * * * sudo java -jar /cronjobs/coinmarketcap-scraper-1.1.jar >/dev/null 2>/dev/null`

Feel free to run the JAR-file from another path.