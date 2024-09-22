package com.bot.theechoesbot.core.handler.slash;

import com.bot.theechoesbot.core.handler.slash.template.SlashHandler;
import com.bot.theechoesbot.object.ServerData;
import net.dv8tion.jda.api.entities.ScheduledEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.utils.MarkdownUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implement /event-start
 */
@SuppressWarnings("DataFlowIssue")
public class SlashEventStartHandler implements SlashHandler{

	private final static Logger logger = LoggerFactory.getLogger(SlashEventStartHandler.class);

	private final ServerData serverData;

	public SlashEventStartHandler(ServerData serverData){
		this.serverData = serverData;
	}

	@Override
	public void handle(SlashCommandInteractionEvent event){

		try{

			//defer reply
			event.deferReply().queue();

			String eventId = event.getOption("event_id").getAsString();
			String message = null;
			OptionMapping messageOption = event.getOption("message");
			if(messageOption != null){
				message = messageOption.getAsString();
			}

			//get the schedule and validate it
			ScheduledEvent scheduledEvent = event.getJDA().getScheduledEventById(eventId);
			if(scheduledEvent == null){
				event.getHook().sendMessage("Could not find event with id " + eventId).queue();
				logger.warn("Event not found: " + eventId);
				return;
			}
			if(scheduledEvent.getStatus() != ScheduledEvent.Status.SCHEDULED){
				event.getHook().sendMessage("The event has already started.").queue();
				logger.warn("Event has already started: " + eventId);
				return;
			}

			//modify status to active
			//and announce it
			scheduledEvent.getManager()
				.setStatus(ScheduledEvent.Status.ACTIVE)
				.and(
					this.serverData.getAnnouncesChannel().sendMessage(
						"@everyone\n" +
							MarkdownUtil.maskedLink(
								scheduledEvent.getName(),
								"https://discord.com/events/" + this.serverData.getGuildId() + "/" + eventId
							) +
							" is starting. Get Ready." +
							(message != null ? "\n" + message : "")
					)
				).queue(

					//reply
					(success) -> {

						logger.info("Event started: " + eventId);

						event.getHook().sendMessage("Event started: " + eventId).queue(
							s -> {},
							e -> logger.info("Error callback-eventStart: " + eventId, e)
						);

					},
					(error) -> {
						event.getHook().sendMessage("Error: " + error.getMessage()).queue();
						logger.error("Error starting event: " + eventId, error);
					}

				);

		}catch(Exception e){
			event.getHook().sendMessage("Error starting event: " + e.getMessage()).queue();
			logger.error("Error starting event", e);
		}

	}

}
