package com.bot.theechoesbot.core;

import com.bot.theechoesbot.core.listener.BotListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Core{

	private final static Logger logger = LoggerFactory.getLogger(Core.class);

	private final BotListener bot;
	private final JDA jda;

	private Core(
		@Value("${discord.bot.token}") String botToken,
		BotListener bot
	){

		try{

			//create the bot
			this.bot = bot;

			//create the client
			this.jda = JDABuilder.createDefault(botToken)
				.addEventListeners(this.bot)
				.build();

		}catch(Exception e){

			logger.error("Failed in Core", e);
			throw new RuntimeException();

		}

	}

	public BotListener getBot(){ return bot; }
	public JDA getJda(){ return jda; }

}
