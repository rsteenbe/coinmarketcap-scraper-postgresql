//////////////////////////////////////////////////////////////////////////
//                                                                      //
//      2017-01-24 - Created by Renco Steenbergen                       //
//                                                                      //
//////////////////////////////////////////////////////////////////////////

package com.rencomusic.coinmarketcap.scraper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.Properties;

public class Main {
    public static void main(String[] args) {
        Logger logger = LoggerFactory.getLogger(Main.class);
        logger.debug("Coinmarketcap scraper running.");

        Properties properties = new Properties();
        InputStream input = null;
        String coinMarketCapUrl = null;
        String storageFolder = null;
        try {
            input = Main.class.getClassLoader().getResourceAsStream("coinmarketcap-scraper.properties");
            properties.load(input);
            coinMarketCapUrl = properties.getProperty("coinmarketcap-scraper.coinMarketCapApi");
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        logger.debug("Properties loaded successfully.");

        String json = null;
        try {
            json = new Scraper(coinMarketCapUrl).scrape();
            logger.debug("Scraping done.");
        } catch (IOException e) {
            e.printStackTrace();
        }
        logger.debug("Reading JSON from coinmarketcap.com completed.");

        Map<String, Ticker> data = TickerService.createTickerList(json);
        logger.debug("All JSON data has been put in a map with key value pair: id, ticker object.");

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM");
        String now = dateFormat.format(new Date());
        logger.debug("Recent date in format yyyy-MM: " + now);

        ArrayList<String> coins = new Coins().getCoins();

        try {
            Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres","postgres","postgres");
            logger.debug("PSQL DB connection successful!");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < coins.size(); i++) {
            try {
                insertRecordIntoTable(data, coins, i);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        logger.debug("Data has been written to CSV.");
    }

    private static void insertRecordIntoTable(Map<String,Ticker> data, ArrayList<String> coins, Integer i) throws SQLException {
        Connection dbConnection = null;
        PreparedStatement preparedStatement = null;

        if (data.get(coins.get(i)) != null) {
            String insertTableSQL = "INSERT INTO " + coins.get(i).replace("-","_")
                    + "(price_btc, price_usd, daily_volume_usd,  market_cap_usd, price_eur, market_cap_eur, total_supply, available_supply, hourly_change, daily_change, weekly_change, time_stamp) VALUES"
                    + "(?,?,?,?,?,?,?,?,?,?,?,?)";
            try {
                dbConnection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "postgres", "postgres");
                preparedStatement = dbConnection.prepareStatement(insertTableSQL);
                preparedStatement.setDouble(1, Double.parseDouble(data.get(coins.get(i)).getPriceBtc()));
                preparedStatement.setDouble(2, Double.parseDouble(data.get(coins.get(i)).getPriceUsd()));
                preparedStatement.setDouble(3, Double.parseDouble(data.get(coins.get(i)).getDailyVolumeUsd()));
                preparedStatement.setDouble(4, Double.parseDouble(data.get(coins.get(i)).getMarketCapUsd()));
                preparedStatement.setDouble(5, Double.parseDouble(data.get(coins.get(i)).getPriceEur()));
                preparedStatement.setDouble(6, Double.parseDouble(data.get(coins.get(i)).getMarketCapEur()));
                preparedStatement.setDouble(7, Double.parseDouble(data.get(coins.get(i)).getTotalSupply()));
                preparedStatement.setDouble(8, Double.parseDouble(data.get(coins.get(i)).getAvailableSupply()));
                preparedStatement.setDouble(9, Double.parseDouble(data.get(coins.get(i)).getPercentChange1h()));
                preparedStatement.setDouble(10, Double.parseDouble(data.get(coins.get(i)).getPercentChange24h()));
                preparedStatement.setDouble(11, Double.parseDouble(data.get(coins.get(i)).getPercentChange7d()));
                preparedStatement.setTimestamp(12, getCurrentTimeStamp());
                preparedStatement.executeUpdate();
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            } finally {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
                if (dbConnection != null) {
                    dbConnection.close();
                }
            }
        } else {
            System.out.println("NULL CO:IN NOT EXIST");
        }

    }
    private static java.sql.Timestamp getCurrentTimeStamp() {
        java.util.Date today = new java.util.Date();
        return new java.sql.Timestamp(today.getTime());
    }
}
