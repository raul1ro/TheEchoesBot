package com.bot.theechoesbot.core.listener;

import com.bot.theechoesbot.core.crawler.GameCrawlerImpl;
import com.bot.theechoesbot.core.crawler.template.GameCrawler;
import com.bot.theechoesbot.core.handler.EventCreateHandler;
import com.bot.theechoesbot.core.handler.slash.SlashEventNewHandler;
import com.bot.theechoesbot.core.handler.slash.SlashEventStartHandler;
import com.bot.theechoesbot.core.handler.slash.SlashRegisterHandler;
import com.bot.theechoesbot.core.handler.slash.SlashRollHandler;
import com.bot.theechoesbot.core.handler.slash.template.SlashHandler;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.NewsChannel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.guild.scheduledevent.ScheduledEventCreateEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@SuppressWarnings("DataFlowIssue")
@Service
public class BotListener extends ListenerAdapter{

	private final Logger logger = LoggerFactory.getLogger(BotListener.class);

	private final GameCrawler gameCrawler = new GameCrawlerImpl();

	private final SlashHandler slashRollHandler;
	private final SlashHandler slashRegisterHandler;
	private final SlashHandler slashEventNewHandler;
	private final SlashHandler slashEventStartHandler;

	private final EventCreateHandler eventCreateHandler;

	private final long guildId;
	private final long voiceEventId;
	private final long scheduleId;
	private final long announcesId;

	private Guild guild;
	private VoiceChannel voiceEventChannel;
	private TextChannel scheduleChannel;
	private NewsChannel announcesChannel;

	public BotListener(
		@Value("${discord.guildId}") long guildId,
		@Value("${discord.voiceEventId}") long voiceEventId,
		@Value("${discord.scheduleId}") long scheduleId,
		@Value("${discord.announcesId}") long announcesId
	){

		this.guildId = guildId;
		this.voiceEventId = voiceEventId;
		this.scheduleId = scheduleId;
		this.announcesId = announcesId;

		this.slashRollHandler = new SlashRollHandler();
		this.slashRegisterHandler = new SlashRegisterHandler(this.gameCrawler);
		this.slashEventNewHandler = new SlashEventNewHandler(this);
		this.slashEventStartHandler = new SlashEventStartHandler(this);

		this.eventCreateHandler = new EventCreateHandler(this);

	}

	@Override
	public void onReady(@NotNull ReadyEvent event){

		logger.info("Bot is ready. Reference: " + this);

		try{

			this.guild = event.getJDA().getGuildById(this.guildId);

			this.voiceEventChannel = this.guild.getVoiceChannelById(this.voiceEventId);
			this.scheduleChannel = this.guild.getTextChannelById(this.scheduleId);
			this.announcesChannel = this.guild.getNewsChannelById(this.announcesId);

		}catch(Exception e){

			logger.error("Error getting guild or channels", e);

		}

	}

	@Override
	public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event){

		switch(event.getName()){
			case "roll": slashRollHandler.handle(event); break;
			case "register": slashRegisterHandler.handle(event); break;
			case "event-new": slashEventNewHandler.handle(event); break;
			case "event-start": slashEventStartHandler.handle(event); break;
			default: event.reply("Unknown command").setEphemeral(true).queue();
		}

	}

	@Override
	public void onScheduledEventCreate(@NotNull ScheduledEventCreateEvent event){
		eventCreateHandler.handle(event);
	}

	public Long getGuildId(){ return guildId; }
	public Long getVoiceEventId(){ return voiceEventId; }
	public Long getScheduleId(){ return scheduleId; }
	public Long getAnnouncesId(){ return announcesId; }

	public Guild getGuild(){ return guild; }
	public VoiceChannel getVoiceEventChannel(){ return voiceEventChannel; }
	public TextChannel getScheduleChannel(){ return scheduleChannel; }
	public NewsChannel getAnnouncesChannel(){ return announcesChannel; }

}
