package com.bot.theechoesbot.core;

import com.bot.theechoesbot.core.listener.BotListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Core{

	private final Logger logger = LoggerFactory.getLogger(Core.class);

	private final BotListener bot;
	private final JDA jda;

	private Core(@Value("${discord.bot.token}") String botToken, BotListener bot){

		try{

			//create the bot
			this.bot = bot;

			//create the client
			this.jda = JDABuilder.createDefault(botToken)
				.addEventListeners(this.bot)
				.build();

			//initialized the commands
			initCommands();

		}catch(Exception e){

			logger.error("Failed in Core", e);
			throw new RuntimeException("Failed to initialize Core");

		}

	}

	private void initCommands(){

		this.jda.updateCommands()
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

				//register
				Commands.slash("register", "Register yourself for access to server.")
					.addOptions(
						new OptionData(
							OptionType.STRING,
							"role",
							"The role you are registering for.",
							true
						).addChoices(
							new Command.Choice("Intern", "intern"),
							new Command.Choice("Member", "member")
						),
						new OptionData(
							OptionType.STRING,
							"character_name",
							"For Member role this should match the main character from the guild.",
							true
						)
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
						)
					)

			).queue();

	}

	public BotListener getBot(){ return bot; }
	public JDA getJda(){ return jda; }

}
