package com.bot.theechoesbot.listener;

import com.bot.theechoesbot.service.RegisterService;
import com.bot.theechoesbot.handler.ButtonInteractionHandler;
import com.bot.theechoesbot.handler.EventCreateHandler;
import com.bot.theechoesbot.handler.ModalInteractionHandler;
import com.bot.theechoesbot.handler.slash.SlashEventNewHandler;
import com.bot.theechoesbot.handler.slash.SlashEventStartHandler;
import com.bot.theechoesbot.handler.slash.SlashRollHandler;
import com.bot.theechoesbot.handler.slash.template.SlashHandler;
import com.bot.theechoesbot.service.DiscordUtil;
import com.bot.theechoesbot.entity.ServerData;
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

	private final static Logger logger = LoggerFactory.getLogger(BotListener.class);

	private final ServerData serverData;

	@SuppressWarnings("FieldCanBeLocal")
	private final RegisterService registerService = new RegisterService();

	private final SlashHandler slashRollHandler;
	private final SlashHandler slashEventNewHandler;
	private final SlashHandler slashEventStartHandler;

	private final EventCreateHandler eventCreateHandler;
	private final ButtonInteractionHandler buttonInteractionHandler;
	private final ModalInteractionHandler modalInteractionHandler;

	@Autowired
	public BotListener(Environment env){

		//noinspection DataFlowIssue
		this.serverData = new ServerData(
			Long.parseLong(env.getProperty("discord.guildId")),
			Long.parseLong(env.getProperty("discord.channel.news.announcesId")),
			Long.parseLong(env.getProperty("discord.channel.voice.eventId")),
			Long.parseLong(env.getProperty("discord.channel.text.scheduleId")),
			Long.parseLong(env.getProperty("discord.channel.text.registerId")),
			Long.parseLong(env.getProperty("discord.role.internId")),
			Long.parseLong(env.getProperty("discord.role.memberId"))
		);

		this.slashRollHandler = new SlashRollHandler();
		this.slashEventNewHandler = new SlashEventNewHandler();
		this.slashEventStartHandler = new SlashEventStartHandler();

		this.eventCreateHandler = new EventCreateHandler();
		this.buttonInteractionHandler = new ButtonInteractionHandler(this.registerService);
		this.modalInteractionHandler = new ModalInteractionHandler(this.registerService);

	}

	@Override
	public void onReady(@NotNull ReadyEvent event){

		logger.info("Bot is ready. Reference: " + this);

		try{

			//initialize the channels instances
			serverData.init(event.getJDA());

			//initialize discord stuffs
			DiscordUtil discordUtil = new DiscordUtil();
			discordUtil.initCommands(event.getJDA());
			discordUtil.initRegister(serverData.getTextRegisterChannel());

		}catch(Exception e){

			logger.error("Error onReady", e);
			System.exit(1);

		}

	}

	@Override
	public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event){

		switch(event.getName()){
			case "roll": slashRollHandler.handle(event, serverData); break;
			case "event-new": slashEventNewHandler.handle(event, serverData); break;
			case "event-start": slashEventStartHandler.handle(event, serverData); break;
			default: {
				event.reply("Unknown command").setEphemeral(true).queue();
				logger.warn("Unknown command: " + event.getName());
				break;
			}
		}

	}

	@Override
	public void onScheduledEventCreate(@NotNull ScheduledEventCreateEvent event){
		eventCreateHandler.handle(event, serverData);
	}

	@Override
	public void onButtonInteraction(@NotNull ButtonInteractionEvent event){
		buttonInteractionHandler.handle(event, serverData);
	}

	@Override
	public void onModalInteraction(@NotNull ModalInteractionEvent event){
		modalInteractionHandler.handle(event, serverData);
	}

}
