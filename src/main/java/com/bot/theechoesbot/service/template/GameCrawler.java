package com.bot.theechoesbot.service.template;

/**
 * Crawl data from the game
 */
public interface GameCrawler{

	/**
	 * [0] - character name; [1] - guild rank
	 */
	String[] getUserData(String characterName);

}
