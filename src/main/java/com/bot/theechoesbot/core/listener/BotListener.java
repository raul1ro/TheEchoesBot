package com.bot.theechoesbot.core.listener;

import com.bot.theechoesbot.core.service.RegisterService;
import com.bot.theechoesbot.core.handler.ButtonInteractionHandler;
import com.bot.theechoesbot.core.handler.EventCreateHandler;
import com.bot.theechoesbot.core.handler.ModalInteractionHandler;
import com.bot.theechoesbot.core.handler.slash.SlashEventNewHandler;
import com.bot.theechoesbot.core.handler.slash.SlashEventStartHandler;
import com.bot.theechoesbot.core.handler.slash.SlashRollHandler;
import com.bot.theechoesbot.core.handler.slash.template.SlashHandler;
import com.bot.theechoesbot.core.service.DiscordUtil;
import com.bot.theechoesbot.object.ServerData;
import net.dv8tion.jda.api.events.guild.scheduledevent.ScheduledEventCreateEvent;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;


/**
 * Hold needed instances and listen for events
 */
@Service
public class BotListener extends ListenerAdapter{

	@Autowired
	private Environment env;

	private final Logger logger = LoggerFactory.getLogger(BotListener.class);

	private final ServerData serverData;

	@SuppressWarnings("FieldCanBeLocal")
	private final RegisterService registerService = new RegisterService();

	private final SlashHandler slashRollHandler;
	private final SlashHandler slashEventNewHandler;
	private final SlashHandler slashEventStartHandler;

	private final EventCreateHandler eventCreateHandler;
	private final ButtonInteractionHandler buttonInteractionHandler;
	private final ModalInteractionHandler modalInteractionHandler;

	public BotListener(){

		//noinspection DataFlowIssue
		this.serverData = new ServerData(
			Long.parseLong(env.getProperty("discord.guildId")),
			Long.parseLong(env.getProperty("discord.voiceEventId")),
			Long.parseLong(env.getProperty("discord.scheduleId")),
			Long.parseLong(env.getProperty("discord.announcesId")),
			Long.parseLong(env.getProperty("discord.registerId"))
		);

		this.slashRollHandler = new SlashRollHandler();
		this.slashEventNewHandler = new SlashEventNewHandler(serverData);
		this.slashEventStartHandler = new SlashEventStartHandler(serverData);

		this.eventCreateHandler = new EventCreateHandler(serverData);
		this.buttonInteractionHandler = new ButtonInteractionHandler(this.registerService);
		this.modalInteractionHandler = new ModalInteractionHandler(this.registerService);

	}

	@Override
	public void onReady(@NotNull ReadyEvent event){

		logger.info("Bot is ready. Reference: " + this);

		try{

			//initialize the channels instances
			serverData.initChannels(event.getJDA());

			//initialize discord stuffs
			DiscordUtil discordUtil = new DiscordUtil();
			discordUtil.initCommands(event.getJDA());
			discordUtil.initRegister(serverData.getRegisterChannel());

		}catch(Exception e){

			logger.error("Error onReady", e);
			System.exit(1);

		}

	}

	@Override
	public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event){

		switch(event.getName()){
			case "roll": slashRollHandler.handle(event); break;
			case "event-new": slashEventNewHandler.handle(event); break;
			case "event-start": slashEventStartHandler.handle(event); break;
			default: event.reply("Unknown command").setEphemeral(true).queue();
		}

	}

	@Override
	public void onScheduledEventCreate(@NotNull ScheduledEventCreateEvent event){
		eventCreateHandler.handle(event);
	}

	@Override
	public void onButtonInteraction(@NotNull ButtonInteractionEvent event){
		buttonInteractionHandler.handle(event);
	}

	@Override
	public void onModalInteraction(@NotNull ModalInteractionEvent event){
		modalInteractionHandler.handle(event);
	}

}
