package com.bot.theechoesbot.command;

import com.bot.theechoesbot.command.template.SlashCommand;
import com.bot.theechoesbot.core.Core;
import com.bot.theechoesbot.core.Globals;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.requests.restaction.ScheduledEventAction;
import net.dv8tion.jda.api.utils.MarkdownUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

public class EventNewSlashCommand extends SlashCommand{

	public final static Logger logger = LoggerFactory.getLogger(EventNewSlashCommand.class);

	public EventNewSlashCommand(){
		super(
			"event-new",
			List.of(Core.getServerData().getRoleMasterId()),
			"Create a new event.",
			List.of(
				new OptionData(
					OptionType.STRING,
					"title",
					"The title of the event.",
					true
				),
				new OptionData(
					OptionType.STRING,
					"date",
					"Date of the event. Format: yyyy-mm-dd",
					true

				),
				new OptionData(
					OptionType.STRING,
					"time",
					"Time of the event. Format: hh:mm (server-time)",
					true
				),
				new OptionData(
					OptionType.INTEGER,
					"required_ilvl",
					"The required item level.",
					false
				),
				new OptionData(
					OptionType.STRING,
					"description",
					"The description of the event.",
					false
				),
				new OptionData(
					OptionType.USER,
					"leader",
					"The leader of the event.",
					false
				)
			)
		);
	}

	@Override
	protected void executeImpl(SlashCommandInteraction interaction){

		//process may take more than 3 seconds, so set bot in thinking mode (15mins to reply)
		interaction.deferReply().queue();

		//extract the inputs
		String title = interaction.getOption("title").getAsString();
		String date = interaction.getOption("date").getAsString();
		String time = interaction.getOption("time").getAsString();

		//nessages for description
		List<String> messages = new ArrayList<>();

		//leader
		OptionMapping leaderOption = interaction.getOption("leader");
		if(leaderOption != null){
			messages.add("Leader: " + leaderOption.getAsMember().getNickname());
		}else{
			messages.add("Leader: " + interaction.getMember().getNickname());
		}

		//required ilvl
		OptionMapping required_ilvl = interaction.getOption("required_ilvl");
		if(required_ilvl != null){
			messages.add("Required ilvl: " + required_ilvl.getAsLong());
		}else{
			messages.add("Required ilvl: None");
		}

		//description
		OptionMapping descriptionOption = interaction.getOption("description");
		if(descriptionOption != null){
			messages.add(descriptionOption.getAsString());
		}

		String description = String.join("\n", messages);

		//prepare date-time
		OffsetDateTime dateTime;
		try{

			//parse date-time -> set zone -> change time to utc -> to offsetdatetime
			dateTime = LocalDateTime.parse(date + " - " + time, Globals.DATE_TIME_FORMATTER)
				.atZone(Globals.ZONE_ID_SERVER)
				.withZoneSameInstant(ZoneId.of("UTC"))
				.toOffsetDateTime();

		}catch(Exception e){
			interaction.getHook().sendMessage("Date (yyyy-mm-dd) or time (hh:mm server-time) in wrong format: " + date + " - " + time).queue();
			logger.error("Invalid format: " + date + " - " + time, e);
			return;
		}

		try{

			//create the event
			ScheduledEventAction scheduledEvent = Core.getServerData().getGuild().createScheduledEvent(
				title,
				Core.getServerData().getChannelVoiceEvent(),
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
					InteractionHook hook = interaction.getHook();

					//send the messages
					hook.sendMessage("Event created: " + eventId).and(
						hook.sendMessage(
							MarkdownUtil.maskedLink(eventId, "https://discord.com/events/" + Core.getServerData().getGuildId() + "/" + eventId)
						)
					).queue(
						(success1) -> {},
						(error1) -> logger.error("Error callback-eventNew", error1)
					);

				},
				(error) -> {
					interaction.getHook().sendMessage("Error: " + error.getMessage()).queue();
					logger.error("Error creating scheduled event", error);
				}
			);

		}catch(Exception e){
			interaction.getHook().sendMessage("Error creating a new event: " + e.getMessage()).queue();
			logger.error("Error creating scheduled event", e);
		}

	}

}
