package com.bot.theechoesbot.service;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Some function for discord
 */
public class DiscordUtil{

	private final static Logger logger = LoggerFactory.getLogger(DiscordUtil.class);

	/**
	 * Update the commands
	 */
	public void initCommands(JDA jda){

		jda.updateCommands()
			.addCommands(

				//roll
				Commands.slash("roll", "Roll a random number.")
					.addOptions(
						new OptionData(
							OptionType.INTEGER,
							"upper_limit",
							"The highest number which can be rolled.",
							false
						).setMinValue(1).setMaxValue(1000)
					),

				//event-new
				Commands.slash("event-new", "Create a new event.")
					.addOptions(
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
							OptionType.STRING,
							"description",
							"(optional) The description of the event.",
							false
						),
						new OptionData(
							OptionType.USER,
							"leader",
							"(optional) The leader of the event.",
							false
						)
					),

				//event-start
				Commands.slash("event-start", "Start the event.")
					.addOptions(
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

			).queue(
				s -> logger.info("Updated the commands."),
				e -> logger.error("Error updating the commands.", e)
			);

	}

	/**
	 * Create a message with buttons for register
	 */
	//tip: it would be nice to clear the channel before init
	public void initRegister(TextChannel registerChannel){

		try{

			//clear the channel
			registerChannel.getHistory().retrievePast(100).queue( //get the messages
				(successHistory) -> {
					successHistory.forEach(e -> { //iterate every message
						e.delete().queue( //delete every message
							(successDelete) -> {},
							(errorDelete) -> logger.error("Error deleting message")
						);

					});
					logger.info("Successfully cleared the " + registerChannel.getName() + " channel");
				},
				(errorHistory) -> logger.error("Error getting messages from " + registerChannel.getName(), errorHistory)
			);

			//create the buttons
			Button internButton = Button.secondary("register_intern", "Intern");
			Button memberButton = Button.success("register_member", "Member");

			MessageCreateData message = new MessageCreateBuilder()
				.setAllowedMentions(List.of()) //no visible mention
				.setSuppressedNotifications(true) //no notification
				.setContent(
					"""
					To have access to the server you need to register yourself.
					You can register yourself as **Intern** or **Member**.
					- Intern - you are not in guild. Limited access.
					- Member - you are in the guild. Full access.
	
					**For any problem please contact <@328569043974094849> or <@658643411120685066>.**
					"""
				).setActionRow(internButton, memberButton)
				.build();

			registerChannel.sendMessage(message).queue(
				s -> logger.info("Register message was created."),
				e -> logger.error("Failed to create the register message.", e)
			);

		}catch(Exception e){

			logger.error("Failed to initRegister.", e);
			throw e;

		}

	}

}