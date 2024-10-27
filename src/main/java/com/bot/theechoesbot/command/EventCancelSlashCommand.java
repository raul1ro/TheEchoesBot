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

public class EventCancelSlashCommand extends SlashCommand{

	private static final Logger logger = LoggerFactory.getLogger(EventCancelSlashCommand.class);

	public EventCancelSlashCommand(){
		super(
			"event-cancel",
			List.of(Core.getServerData().getRoleMasterId()),
			"Cancel the event.",
			List.of(
				new OptionData(
					OptionType.STRING,
					"event_id",
					"The id of the event.",
					true
				),
				new OptionData(
					OptionType.STRING,
					"reason",
					"(optional) The Reason of cancellation.",
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
			ScheduledEvent scheduledEvent = interaction.getJDA().getScheduledEventById(eventId);
			if(scheduledEvent == null){
				interaction.getHook().sendMessage("Could not find event with id " + eventId).queue();
				logger.warn("Event not found: " + eventId);
				return;
			}

			ScheduledEvent.Status currentStatus = scheduledEvent.getStatus();
			boolean isScheduled = (currentStatus == ScheduledEvent.Status.SCHEDULED);
			boolean isActive = (currentStatus == ScheduledEvent.Status.ACTIVE);
			if(!isScheduled && !isActive){
				interaction.getHook().sendMessage("The event can't be canceled.").queue();
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
						interaction.getHook().sendMessage("Event canceled: " + eventId).queue(
							s -> {},
							e -> logger.info("Error callback-eventCancel: " + eventId, e)
						);

						//get the reason from input and save it in cache
						OptionMapping reasonOption = interaction.getOption("reason");
						if(reasonOption != null){
							Cache.Event.put("cancel_" + eventId, reasonOption.getAsString());
						}

					},
					(error) -> {
						interaction.getHook().sendMessage("Error: " + error.getMessage()).queue();
						logger.error("Error canceling the event: " + eventId, error);
					}
				);

		}catch(Exception e){
			interaction.getHook().sendMessage("Error canceling the event: " + e.getMessage()).queue();
			logger.error("Error canceling the event", e);
		}

	}

}
