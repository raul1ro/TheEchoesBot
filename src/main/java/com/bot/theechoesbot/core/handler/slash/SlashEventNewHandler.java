package com.bot.theechoesbot.core.handler.slash;

import com.bot.theechoesbot.core.Globals;
import com.bot.theechoesbot.core.handler.slash.template.SlashHandler;
import com.bot.theechoesbot.core.listener.BotListener;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.requests.restaction.ScheduledEventAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Implement /event-new
 */
@SuppressWarnings("DataFlowIssue")
public class SlashEventNewHandler implements SlashHandler{

	private final Logger logger = LoggerFactory.getLogger(SlashEventNewHandler.class);

	private final BotListener bot;

	private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd - HH:mm");

	public SlashEventNewHandler(BotListener bot){
		this.bot = bot;
	}

	@Override
	public void handle(SlashCommandInteractionEvent event){

		//extract the inputs
		String title = event.getOption("title").getAsString();
		String date = event.getOption("date").getAsString();
		String time = event.getOption("time").getAsString();
		String description = null;
		try{
			description = event.getOption("description").getAsString();
		}catch(Exception ignored){}

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
			event.reply("Date (yyyy-mm-dd) or time (hh:mm server-time) in wrong format: " + date + " - " + time).queue();
			return;

		}

		try{

			//create the event
			ScheduledEventAction scheduledEvent = this.bot.getGuild().createScheduledEvent(title, this.bot.getVoiceEventChannel(), dateTime);
			scheduledEvent = scheduledEvent.setDescription(description);

			//queue the action
			//success -> reply with success
			//error -> reply with error
			scheduledEvent.queue(
				(success) -> {

					String eventId = success.getId();
					event.reply("Event created. Id: [" + eventId + "](https://discord.com/events/" + this.bot.getGuildId() + "/" + eventId + ")").queue();

				},
				(error) -> event.reply("Error: " + error.getMessage()).queue()
			);

		}catch(Exception e){

			logger.error("Error creating scheduled event", e);
			event.reply("Error: " + e.getMessage()).queue();

		}

	}

}
