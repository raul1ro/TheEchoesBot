package com.bot.theechoesbot.service;

import com.bot.theechoesbot.core.Core;
import com.bot.theechoesbot.core.Globals;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
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
	public static void initCommands(JDA jda){

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
							OptionType.INTEGER,
							"required_ilvl",
							"(optional) The required item level.",
							false
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
					),

				//event-cancel
				Commands.slash("event-cancel", "Cancel the event.")
					.addOptions(
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

			).queue(
				s -> logger.info("Updated the commands."),
				e -> logger.error("Error updating the commands.", e)
			);

	}

	/**
	 * Create a message with buttons for register
	 */
	public static void initRegister(TextChannel registerChannel){

		try{

			//clear the channel - but with thread lock
			//only bot messages
			//to be sure the clear will not execute too late and delete register message
			List<Message> messages = registerChannel.getHistory().retrievePast(5).complete();
			messages.stream().filter(e -> e.getAuthor().isBot()).forEach(e -> e.delete().complete());
			logger.info("Successfully cleared the " + registerChannel.getName() + " channel");

			//create the buttons
			Button internButton = Button.secondary("register_intern", "Intern");
			Button memberButton = Button.success("register_member", "Member");

			//create the message
			MessageCreateData message = new MessageCreateBuilder()
				.setAllowedMentions(List.of()) //no visible mention
				.setSuppressedNotifications(true) //no notification
				.setContent(
					"""
					To have access to the server you need to register yourself.
					You can register yourself as **Intern** or **Member**.
					- Intern - __you are not in guild. Limited access.__
					- Member - __you are in the guild. Full access.__
					_If you register as Intern, and later you become member of the guild, ask <@328569043974094849> or <@658643411120685066> for upgrade._
					
					For any problem please contact <@328569043974094849>.
					"""
				).setActionRow(internButton, memberButton)
				.build();

			//send the message
			registerChannel.sendMessage(message).queue(
				s -> logger.info("Register message was created."),
				e -> logger.error("Failed to create the register message.", e)
			);

		}catch(Exception e){

			logger.error("Failed to initRegister.", e);
			throw e;

		}

	}

	public static void sendLog(String text){
		sendMessage(text, Core.getServerData().getBotChannelId(), Core.getBotToken());
	}

	/**
	 * Send a message directly, without any library.
	 */
	public static boolean sendMessage(String text, long channelId, String botToken){

		try{

			RequestBody body = RequestBody.create(
				"{\"content\":\"" + text + "\"}",
				MediaType.parse("application/json")
			);

			Request request = new Request.Builder()
				.url("https://discord.com/api/v10/channels/" + channelId + "/messages")
				.method("POST", body)
				.addHeader("Content-Type", "application/json")
				.addHeader("Authorization", "Bot " + botToken)
				.build();

			try(Response response = Globals.HTTP_CLIENT.newCall(request).execute()){

				int responseCode = response.code();

				//check response
				if(responseCode == 200){ return true; }

				//noinspection ConstantConditions
				throw new Exception("code: " + responseCode + " - status: " + response.message() + " - body: " + response.body().string());

			}

		}catch(Exception e){
			logger.error("Failed to send message.", e);
			return false;
		}

	}

}
