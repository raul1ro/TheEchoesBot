package com.bot.theechoesbot.core.crawler.template;

public interface GameCrawler{

	/**
	 * [0] - character name; [1] - guild rank
	 */
	String[] getUserData(String characterName);

}
