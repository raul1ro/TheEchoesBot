package com.bot.theechoesbot.core;

import com.bot.theechoesbot.listener.BotListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Core{

	private final static Logger logger = LoggerFactory.getLogger(Core.class);

	private static String BotToken;
	private static BotListener Bot;
	private static JDA JDAClient;

	private Core(
		@Value("${discord.bot.token}") String botToken,
		BotListener bot
	){

		try{

			BotToken = botToken;
			Bot = bot;

			//create the client
			JDAClient = JDABuilder.createDefault(botToken)
				.addEventListeners(Bot)
				.build();

		}catch(Exception e){

			logger.error("Failed in Core", e);
			throw new RuntimeException();

		}

	}

	public static BotListener getBot(){ return Bot; }

	public static String getBotToken(){ return BotToken; }

	public static JDA getJDAClient(){ return JDAClient; }

}
