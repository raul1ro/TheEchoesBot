package com.bot.theechoesbot.command;

import com.bot.theechoesbot.command.template.SlashCommand;
import com.bot.theechoesbot.core.Globals;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.List;

public class RollSlashCommand extends SlashCommand{

	public RollSlashCommand(){
		super(
			"roll",
			null,
			"Roll a random number.",
			List.of(
				new OptionData(
					OptionType.INTEGER,
					"upper_limit",
					"The highest number which can be rolled.",
					false
				).setMinValue(1).setMaxValue(1000)
			)
		);
	}

	@Override
	protected void executeImpl(SlashCommandInteraction interaction){

		//extract he input value
		long upperLimit = 100L; //default value
		try{
			upperLimit = interaction.getOption("upper_limit").getAsLong();
		}catch(Exception ignored){}

		long rollNumber = Globals.RANDOM.nextLong(upperLimit + 1);

		String userid = interaction.getMember().getId();

		interaction.reply("<@" + userid + "> rolled: " + rollNumber + " *[0, " + upperLimit + "]*").queue();

	}

}
