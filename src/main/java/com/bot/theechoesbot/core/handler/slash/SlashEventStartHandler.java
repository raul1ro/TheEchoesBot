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
public class SlashEventStartHandler implements SlashHandler{

	private final Logger logger = LoggerFactory.getLogger(SlashEventStartHandler.class);

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
				return;
			}
			if(scheduledEvent.getStatus() != ScheduledEvent.Status.SCHEDULED){
				event.getHook().sendMessage("The event has already started.").queue();
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
					(success) -> event.getHook().sendMessage("Event started: " + eventId).queue(),
					(error) -> {
						logger.error("Error starting event", error);
						event.getHook().sendMessage("Error: " + error.getMessage()).queue();
					}

				);

		}catch(Exception e){

			logger.error("Error starting event", e);
			event.getHook().sendMessage("Error starting event: " + e.getMessage()).queue();

		}

	}

}
