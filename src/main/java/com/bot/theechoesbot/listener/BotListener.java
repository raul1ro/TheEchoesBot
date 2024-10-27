package com.bot.theechoesbot.listener;

import com.bot.theechoesbot.command.*;
import com.bot.theechoesbot.command.template.Command;
import com.bot.theechoesbot.command.template.SlashCommand;
import com.bot.theechoesbot.core.Core;
import com.bot.theechoesbot.eventhandler.*;
import com.bot.theechoesbot.eventhandler.template.EventHandler;
import com.bot.theechoesbot.service.RegisterService;
import com.bot.theechoesbot.service.DiscordUtil;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.guild.scheduledevent.ScheduledEventCreateEvent;
import net.dv8tion.jda.api.events.guild.scheduledevent.update.ScheduledEventUpdateStatusEvent;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.events.session.ShutdownEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.CommandInteraction;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Hold needed instances and listen for events
 */
@Service
public class BotListener extends ListenerAdapter{

	private final static Logger logger = LoggerFactory.getLogger(BotListener.class);

	@SuppressWarnings("FieldCanBeLocal")
	private final RegisterService registerService = new RegisterService();

	private final Map<String, Command<? extends CommandInteraction>> commands = new HashMap<>();
	private final Map<String, EventHandler<? extends Event>> handlers = new HashMap<>();

	@Override
	public void onReady(@NotNull ReadyEvent event){

		logger.info("Bot is ready. Reference: " + this);

		try{

			//create the commands
			commands.putAll(
				Stream.of(
					new RollSlashCommand(),
					new EventNewSlashCommand(), new EventStartSlashCommand(), new EventCancelSlashCommand(),
					new ClearChannelSlashCommand(),
					new EditMessageCommand()
				).collect(
					Collectors.toMap(
						Command::getName,
						v -> v
					)
				)
			);

			//create the handlers
			handlers.put("event-create", new EventCreateEventHandler());
			handlers.put("event-update-status", new EventUpdateStatusEventHandler());

			//initialize the objects
			Core.getServerData().init(event.getJDA());

			//update the commands
			DiscordUtil.updateCommands(
				event.getJDA(),
				commands.values().stream().map(Command::buildCommand).toList()
			);

			//update the register message
			DiscordUtil.updateRegister(Core.getServerData().getChannelTextRegister());

		}catch(Exception e){

			logger.error("Error onReady", e);
			System.exit(1);

		}

	}

	@Override
	public void onShutdown(@NotNull ShutdownEvent event){
		DiscordUtil.sendLog("Bot has stopped.");
		logger.warn("Bot is shutting down. Reference: " + this + " - " + event.getCode() + " - " + event.getCloseCode().getMeaning());
	}

	@Override
	public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event){
		((SlashCommand)(commands.get(event.getName()))).execute(event);
	}

	@Override
	public void onScheduledEventCreate(@NotNull ScheduledEventCreateEvent event){
		((EventCreateEventHandler)(handlers.get("event-create"))).handle(event);
	}

	@Override
	public void onScheduledEventUpdateStatus(@NotNull ScheduledEventUpdateStatusEvent event){
		((EventUpdateStatusEventHandler)(handlers.get("event-update-status"))).handle(event);
	}

	@Override
	public void onButtonInteraction(@NotNull ButtonInteractionEvent event){

		String buttonId = event.getButton().getId();
		switch(buttonId){

			case "register_intern": registerService.registerAsIntern(event, event.getMember(), Core.getServerData().getGuild(), Core.getServerData().getRoleIntern()); break;
			case "register_member": registerService.createModalRegisterAsMember(event); break;
			default: {
				event.reply("Unknown button id: " + buttonId).setEphemeral(true).queue();
				logger.warn("Unknown button id: " + buttonId);
				break;
			}

		}

	}

	@Override
	public void onModalInteraction(@NotNull ModalInteractionEvent event){

		String modalId = event.getModalId();
		switch(event.getModalId()){

			case "modal_register_member": this.registerService.registerAsMember(event, event.getMember(), Core.getServerData().getGuild(), Core.getServerData().getRoleMember()); break;
			case "modal_edit_message": EditMessageCommand.editMessage(event, event.getMember()); break;
			default: {
				event.reply("Unknown modal id: " + modalId).setEphemeral(true).queue();
				logger.warn("Unknown modal id: " + modalId);
				break;
			}

		}

	}

	@Override
	public void onMessageContextInteraction(@NotNull MessageContextInteractionEvent event){
		((EditMessageCommand)(commands.get("Edit Message"))).execute(event.getInteraction());
	}

}
