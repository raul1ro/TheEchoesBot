package com.bot.theechoesbot.handler.slash;

import com.bot.theechoesbot.core.Globals;
import com.bot.theechoesbot.handler.slash.template.SlashHandler;
import com.bot.theechoesbot.entity.ServerData;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.requests.restaction.ScheduledEventAction;
import net.dv8tion.jda.api.utils.MarkdownUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * Implement /event-new
 */
@SuppressWarnings("DataFlowIssue")
public class SlashEventNewHandler implements SlashHandler{

	private final static Logger logger = LoggerFactory.getLogger(SlashEventNewHandler.class);

	private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd - HH:mm");

	@Override
	public void handle(SlashCommandInteractionEvent event, ServerData serverData){

		//process may take more than 3 seconds, so set bot in thinking mode (15mins to reply)
		event.deferReply().queue();

		//extract the inputs
		String title = event.getOption("title").getAsString();
		String date = event.getOption("date").getAsString();
		String time = event.getOption("time").getAsString();

		//description var
		String description;

		//leader
		OptionMapping leaderOption = event.getOption("leader");
		if(leaderOption != null){
			description = "**Leader: " + leaderOption.getAsMember().getNickname() + "**";
		}else{
			description = "**Leader: " + event.getMember().getNickname() + "**";
		}

		//description
		OptionMapping descriptionOption = event.getOption("description");
		if(descriptionOption != null){
			description = description + "\n" + descriptionOption.getAsString();
		}

		//prepare date-time
		OffsetDateTime dateTime;
		try{

			//parse date-time -> set zone -> change time to utc -> to offsetdatetime
			dateTime = LocalDateTime.parse(date + " - " + time, this.dateTimeFormatter)
				.atZone(Globals.ZONE_ID_SERVER)
				.withZoneSameInstant(ZoneId.of("UTC"))
				.toOffsetDateTime();

		}catch(Exception e){
			event.getHook().sendMessage("Date (yyyy-mm-dd) or time (hh:mm server-time) in wrong format: " + date + " - " + time).queue();
			logger.error("Invalid format: " + date + " - " + time, e);
			return;
		}

		try{

			//create the event
			ScheduledEventAction scheduledEvent = serverData.getGuild().createScheduledEvent(
				title,
				serverData.getVoiceEventChannel(),
				dateTime
			);
			scheduledEvent = scheduledEvent.setDescription(description);

			//queue the action
			//success -> reply with success
			//error -> reply with error
			scheduledEvent.queue(
				(success) -> {

					//get the id
					String eventId = success.getId();

					logger.info("Event created: " + eventId);

					//get the hook
					InteractionHook hook = event.getHook();

					//send the messages
					hook.sendMessage("Event created: " + eventId).and(
						hook.sendMessage(
							MarkdownUtil.maskedLink(eventId, "https://discord.com/events/" + serverData.getGuildId() + "/" + eventId)
						)
					).queue(
						(success1) -> {},
						(error1) -> logger.error("Error callback-eventNew", error1)
					);

				},
				(error) -> {
					event.getHook().sendMessage("Error: " + error.getMessage()).queue();
					logger.error("Error creating scheduled event", error);
				}
			);

		}catch(Exception e){
			event.getHook().sendMessage("Error: " + e.getMessage()).queue();
			logger.error("Error creating scheduled event", e);
		}

	}

}
