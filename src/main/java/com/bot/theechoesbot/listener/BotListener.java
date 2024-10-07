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


	@SuppressWarnings("FieldCanBeLocal")
	private final RegisterService registerService = new RegisterService();

	private final SlashHandler slashRollHandler;
	private final SlashHandler slashEventNewHandler;
	private final SlashHandler slashEventStartHandler;
	private final SlashHandler slashEventCancelHandler;

	private final EventCreateHandler eventCreateHandler;
	private final EventCancelHandler eventCancelHandler;
	private final EventCompleteHandler eventCompleteHandler;
	private final EventActiveHandler eventActiveHandler;

	private final ButtonInteractionHandler buttonInteractionHandler;
	private final ModalInteractionHandler modalInteractionHandler;

	public BotListener(){

		this.slashRollHandler = new SlashRollHandler();
		this.slashEventNewHandler = new SlashEventNewHandler();
		this.slashEventStartHandler = new SlashEventStartHandler();
		this.slashEventCancelHandler = new SlashEventCancelHandler();

		this.eventCreateHandler = new EventCreateHandler();
		this.eventCancelHandler = new EventCancelHandler();
		this.eventCompleteHandler = new EventCompleteHandler();
		this.eventActiveHandler = new EventActiveHandler();

		this.buttonInteractionHandler = new ButtonInteractionHandler(this.registerService);
		this.modalInteractionHandler = new ModalInteractionHandler(this.registerService);

	}

	@Override
	public void onReady(@NotNull ReadyEvent event){

		logger.info("Bot is ready. Reference: " + this);

		try{

			//initialize the channels instances
			Core.getServerData().init(event.getJDA());

			//initialize discord stuffs
			DiscordUtil.initCommands(event.getJDA());
			DiscordUtil.initRegister(Core.getServerData().getTextRegisterChannel());

		}catch(Exception e){

			logger.error("Error onReady", e);
			System.exit(1);

		}

	}

	@Override
	public void onShutdown(@NotNull ShutdownEvent event){
		logger.warn("Bot is shutting down. Reference: " + this);
		DiscordUtil.sendLog("Bot has stopped.");
	}

	@Override
	public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event){

		switch(event.getName()){
			case "roll": this.slashRollHandler.handle(event); break;
			case "event-new": this.slashEventNewHandler.handle(event); break;
			case "event-start": this.slashEventStartHandler.handle(event); break;
			case "event-cancel": this.slashEventCancelHandler.handle(event); break;
			default: {
				event.reply("Unknown command").setEphemeral(true).queue();
				logger.warn("Unknown command: " + event.getName());
				break;
			}
		}

	}

	@Override
	public void onScheduledEventCreate(@NotNull ScheduledEventCreateEvent event){
		eventCreateHandler.handle(event);
	}

	@Override
	public void onScheduledEventUpdateStatus(@NotNull ScheduledEventUpdateStatusEvent event){

		ScheduledEvent.Status status = event.getNewStatus();
		switch(status){

			case ScheduledEvent.Status.ACTIVE -> this.eventActiveHandler.handle(event.getScheduledEvent());
			case ScheduledEvent.Status.COMPLETED -> this.eventCompleteHandler.handle(event.getScheduledEvent());
			case ScheduledEvent.Status.CANCELED -> this.eventCancelHandler.handle(event.getScheduledEvent());

		}

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
