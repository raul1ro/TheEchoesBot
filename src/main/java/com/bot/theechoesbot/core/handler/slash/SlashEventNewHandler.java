package com.bot.theechoesbot.core.handler.slash;

import com.bot.theechoesbot.core.Globals;
import com.bot.theechoesbot.core.handler.slash.template.SlashHandler;
import com.bot.theechoesbot.object.ServerData;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
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

	private final Logger logger = LoggerFactory.getLogger(SlashEventNewHandler.class);

	private final ServerData serverData;

	private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd - HH:mm");

	public SlashEventNewHandler(ServerData serverData){
		this.serverData = serverData;
	}

	@Override
	public void handle(SlashCommandInteractionEvent event){

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

			logger.error("Invalid format: " + date + " - " + time, e);
			event.getHook().sendMessage("Date (yyyy-mm-dd) or time (hh:mm server-time) in wrong format: " + date + " - " + time).queue();
			return;

		}

		try{

			//create the event
			ScheduledEventAction scheduledEvent = this.serverData.getGuild().createScheduledEvent(
				title,
				this.serverData.getVoiceEventChannel(),
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

					//get the hook
					InteractionHook hook = event.getHook();

					//send the messages
					hook.sendMessage(
						"Event created: " + eventId
					).and(
						hook.sendMessage(
							MarkdownUtil.maskedLink(eventId, "https://discord.com/events/" + this.serverData.getGuildId() + "/" + eventId)
						)
					).queue();


				},
				(error) -> {
					logger.error("Error creating scheduled event", error);
					event.getHook().sendMessage("Error: " + error.getMessage()).queue();
				}
			);

		}catch(Exception e){

			logger.error("Error creating scheduled event", e);
			event.getHook().sendMessage("Error: " + e.getMessage()).queue();

		}

	}

}
