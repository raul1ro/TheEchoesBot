package com.bot.theechoesbot.listener;

import com.bot.theechoesbot.core.Core;
import com.bot.theechoesbot.handler.*;
import com.bot.theechoesbot.handler.slash.SlashEventCancelHandler;
import com.bot.theechoesbot.service.RegisterService;
import com.bot.theechoesbot.handler.slash.SlashEventNewHandler;
import com.bot.theechoesbot.handler.slash.SlashEventStartHandler;
import com.bot.theechoesbot.handler.slash.SlashRollHandler;
import com.bot.theechoesbot.handler.slash.template.SlashHandler;
import com.bot.theechoesbot.service.DiscordUtil;
import com.bot.theechoesbot.entity.ServerData;
import net.dv8tion.jda.api.entities.ScheduledEvent;
import net.dv8tion.jda.api.events.guild.scheduledevent.ScheduledEventCreateEvent;
import net.dv8tion.jda.api.events.guild.scheduledevent.update.ScheduledEventUpdateStatusEvent;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.events.session.ShutdownEvent;
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
	private final SlashHandler slashEventCancelHandler;

	private final EventCreateHandler eventCreateHandler;
	private final EventCancelHandler eventCancelHandler;
	private final EventCompletHandler eventCompletHandler;
	private final EventActiveHandler eventActiveHandler;

	private final ButtonInteractionHandler buttonInteractionHandler;
	private final ModalInteractionHandler modalInteractionHandler;

	@Autowired
	public BotListener(Environment env){

		//noinspection DataFlowIssue
		this.serverData = new ServerData(
			Long.parseLong(env.getProperty("discord.guildId")),
			Long.parseLong(env.getProperty("discord.bot.channelId")),
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
		this.slashEventCancelHandler = new SlashEventCancelHandler();

		this.eventCreateHandler = new EventCreateHandler();
		this.eventCancelHandler = new EventCancelHandler();
		this.eventCompletHandler = new EventCompletHandler();
		this.eventActiveHandler = new EventActiveHandler();

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
			DiscordUtil.initCommands(event.getJDA());
			DiscordUtil.initRegister(serverData.getTextRegisterChannel());

		}catch(Exception e){

			logger.error("Error onReady", e);
			System.exit(1);

		}

	}

	@Override
	public void onShutdown(@NotNull ShutdownEvent event){
		logger.warn("Bot is shutting down. Reference: " + this);
		DiscordUtil.sendMessage("Bot has stopped.", serverData.getBotChannelId(), Core.getBotToken());
	}

	@Override
	public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event){

		switch(event.getName()){
			case "roll": this.slashRollHandler.handle(event, serverData); break;
			case "event-new": this.slashEventNewHandler.handle(event, serverData); break;
			case "event-start": this.slashEventStartHandler.handle(event, serverData); break;
			case "event-cancel": this.slashEventCancelHandler.handle(event, serverData); break;
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
	public void onScheduledEventUpdateStatus(@NotNull ScheduledEventUpdateStatusEvent event){

		ScheduledEvent.Status status = event.getNewStatus();
		switch(status){

			case ScheduledEvent.Status.ACTIVE -> this.eventActiveHandler.handle(event.getScheduledEvent(), serverData);
			case ScheduledEvent.Status.COMPLETED -> this.eventCompletHandler.handle(event.getScheduledEvent(), serverData);
			case ScheduledEvent.Status.CANCELED -> this.eventCancelHandler.handle(event.getScheduledEvent(), serverData);

		}

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
