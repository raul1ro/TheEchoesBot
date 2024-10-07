package com.bot.theechoesbot.handler.slash;

import com.bot.theechoesbot.core.Cache;
import com.bot.theechoesbot.core.Core;
import com.bot.theechoesbot.handler.slash.template.SlashHandler;
import net.dv8tion.jda.api.entities.ScheduledEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implement /event-start
 */
@SuppressWarnings("DataFlowIssue")
public class SlashEventStartHandler implements SlashHandler{

	private final static Logger logger = LoggerFactory.getLogger(SlashEventStartHandler.class);

	@Override
	public void handle(SlashCommandInteractionEvent event){

		try{

			//defer reply
			event.deferReply().queue();

			//get the event id input
			String eventId = event.getOption("event_id").getAsString();

			//get the schedule and validate it
			ScheduledEvent scheduledEvent = Core.getServerData().getGuild().retrieveScheduledEventById(eventId).complete();
			if(scheduledEvent == null){
				event.getHook().sendMessage("Could not find event with id " + eventId).queue();
				logger.warn("Event not found: " + eventId);
				return;
			}
			if(scheduledEvent.getStatus() != ScheduledEvent.Status.SCHEDULED){
				event.getHook().sendMessage("The event can't be started.").queue();
				logger.warn("The event can't be started: " + eventId);
				return;
			}

			//modify status to active
			scheduledEvent.getManager()
				.setStatus(ScheduledEvent.Status.ACTIVE)
				.queue(
					(success) -> {

						logger.info("Event started: " + eventId);
						event.getHook().sendMessage("Event started: " + eventId).queue(
							s -> {},
							e -> logger.info("Error callback-eventStart: " + eventId, e)
						);

						//get the message input and save it in cache
						OptionMapping messageOption = event.getOption("message");
						if(messageOption != null){
							Cache.Event.put("start_" + eventId, messageOption.getAsString());
						}

					},
					(error) -> {
						event.getHook().sendMessage("Error: " + error.getMessage()).queue();
						logger.error("Error starting the event: " + eventId, error);
					}
				);

		}catch(Exception e){
			event.getHook().sendMessage("Error starting the event: " + e.getMessage()).queue();
			logger.error("Error starting the event", e);
		}

	}

}
