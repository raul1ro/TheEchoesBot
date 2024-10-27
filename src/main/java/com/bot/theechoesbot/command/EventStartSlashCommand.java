package com.bot.theechoesbot.command;

import com.bot.theechoesbot.command.template.SlashCommand;
import com.bot.theechoesbot.core.Cache;
import com.bot.theechoesbot.core.Core;
import net.dv8tion.jda.api.entities.ScheduledEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class EventStartSlashCommand extends SlashCommand{

	private static final Logger logger = LoggerFactory.getLogger(EventStartSlashCommand.class);

	public EventStartSlashCommand(){
		super(
			"event-start",
			List.of(Core.getServerData().getRoleMasterId()),
			"Start the event.",
			List.of(
				new OptionData(
					OptionType.STRING,
					"event_id",
					"The id of the event.",
					true
				),
				new OptionData(
					OptionType.STRING,
					"message",
					"(optional) Additional message in announcement.",
					false
				)
			)
		);
	}

	@Override
	protected void executeImpl(SlashCommandInteraction interaction){

		try{

			//defer reply
			interaction.deferReply().queue();

			//get the event id input
			String eventId = interaction.getOption("event_id").getAsString();

			//get the schedule and validate it
			ScheduledEvent scheduledEvent = Core.getServerData().getGuild().retrieveScheduledEventById(eventId).complete();
			if(scheduledEvent == null){
				interaction.getHook().sendMessage("Could not find event with id " + eventId).queue();
				logger.warn("Event not found: " + eventId);
				return;
			}
			if(scheduledEvent.getStatus() != ScheduledEvent.Status.SCHEDULED){
				interaction.getHook().sendMessage("The event can't be started.").queue();
				logger.warn("The event can't be started: " + eventId);
				return;
			}

			//modify status to active
			scheduledEvent.getManager()
				.setStatus(ScheduledEvent.Status.ACTIVE)
				.queue(
					(success) -> {

						logger.info("Event started: " + eventId);
						interaction.getHook().sendMessage("Event started: " + eventId).queue(
							s -> {},
							e -> logger.info("Error callback-eventStart: " + eventId, e)
						);

						//get the message input and save it in cache
						OptionMapping messageOption = interaction.getOption("message");
						if(messageOption != null){
							Cache.Event.put("start_" + eventId, messageOption.getAsString());
						}

					},
					(error) -> {
						interaction.getHook().sendMessage("Error: " + error.getMessage()).queue();
						logger.error("Error starting the event: " + eventId, error);
					}
				);

		}catch(Exception e){
			interaction.getHook().sendMessage("Error starting the event: " + e.getMessage()).queue();
			logger.error("Error starting the event", e);
		}

	}

}
