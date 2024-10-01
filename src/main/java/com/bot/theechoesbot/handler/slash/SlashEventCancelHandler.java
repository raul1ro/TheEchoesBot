package com.bot.theechoesbot.handler.slash;

import com.bot.theechoesbot.core.Cache;
import com.bot.theechoesbot.entity.ServerData;
import com.bot.theechoesbot.handler.slash.template.SlashHandler;
import net.dv8tion.jda.api.entities.ScheduledEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implement /event-cancel
 */
@SuppressWarnings("DataFlowIssue")
public class SlashEventCancelHandler implements SlashHandler{

	private final static Logger logger = LoggerFactory.getLogger(SlashEventCancelHandler.class);

	@Override
	public void handle(SlashCommandInteractionEvent event, ServerData serverData){

		try{

			//defer reply
			event.deferReply().queue();

			//get the event id input
			String eventId = event.getOption("event_id").getAsString();

			//get the schedule and validate it
			ScheduledEvent scheduledEvent = event.getJDA().getScheduledEventById(eventId);
			if(scheduledEvent == null){
				event.getHook().sendMessage("Could not find event with id " + eventId).queue();
				logger.warn("Event not found: " + eventId);
				return;
			}

			ScheduledEvent.Status currentStatus = scheduledEvent.getStatus();
			boolean isScheduled = (currentStatus == ScheduledEvent.Status.SCHEDULED);
			boolean isActive = (currentStatus == ScheduledEvent.Status.ACTIVE);
			if(!isScheduled && !isActive){
				event.getHook().sendMessage("The event can't be canceled.").queue();
				logger.warn("The event can't be canceled: " + eventId + " / " + currentStatus);
				return;
			}

			//determine the new status
			//Scheduled -> Canceled
			//Active -> Completed
			ScheduledEvent.Status newStatus;
			if(isScheduled){
				newStatus = ScheduledEvent.Status.CANCELED;
			}else{
				newStatus = ScheduledEvent.Status.COMPLETED;
			}

			//set status to cancel
			scheduledEvent.getManager()
				.setStatus(newStatus)
				.queue(
					(success) -> {

						logger.info("Event canceled: " + eventId);
						event.getHook().sendMessage("Event canceled: " + eventId).queue(
							s -> {},
							e -> logger.info("Error callback-eventCancel: " + eventId, e)
						);

						//get the reason from input and save it in cache
						OptionMapping reasonOption = event.getOption("reason");
						if(reasonOption != null){
							Cache.Event.put("cancel_" + eventId, reasonOption.getAsString());
						}

					},
					(error) -> {
						event.getHook().sendMessage("Error: " + error.getMessage()).queue();
						logger.error("Error canceling the event: " + eventId, error);
					}
				);

		}catch(Exception e){
			event.getHook().sendMessage("Error canceling the event: " + e.getMessage()).queue();
			logger.error("Error canceling the event", e);
		}

	}

}
