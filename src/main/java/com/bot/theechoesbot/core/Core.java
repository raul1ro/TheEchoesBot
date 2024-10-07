package com.bot.theechoesbot.core;

import com.bot.theechoesbot.entity.ServerData;
import com.bot.theechoesbot.listener.BotListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.concurrent.*;

@Component
public class Core{

	private final static Logger logger = LoggerFactory.getLogger(Core.class);

	private static String BotToken;
	private static BotListener Bot;
	private static JDA JDAClient;
	private static ServerData ServerData;

	private Core(
		BotListener bot,
		Environment env
	){

		try{

			BotToken = env.getProperty("discord.bot.token");
			Bot = bot;
			ServerData = new ServerData(
				Long.parseLong(env.getProperty("discord.guildId")),
				Long.parseLong(env.getProperty("discord.bot.channelId")),
				Long.parseLong(env.getProperty("discord.channel.news.announcesId")),
				Long.parseLong(env.getProperty("discord.channel.voice.eventId")),
				Long.parseLong(env.getProperty("discord.channel.text.scheduleId")),
				Long.parseLong(env.getProperty("discord.channel.text.registerId")),
				Long.parseLong(env.getProperty("discord.role.internId")),
				Long.parseLong(env.getProperty("discord.role.memberId"))
			);

			ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(3, Thread.ofVirtual().factory());
			ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
				3, 5,
				1, TimeUnit.MINUTES,
				new LinkedBlockingQueue<>(),
				Thread.ofVirtual().factory()
			);

			//create the client
			JDAClient = JDABuilder.createDefault(BotToken)
				.setGatewayPool(scheduledThreadPoolExecutor, true)
				.setRateLimitScheduler(scheduledThreadPoolExecutor, true)
				.setRateLimitElastic(threadPoolExecutor, true)
				.setCallbackPool(threadPoolExecutor, true)
				.setEventPool(threadPoolExecutor, true)
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

	public static com.bot.theechoesbot.entity.ServerData getServerData(){ return ServerData; }

}
