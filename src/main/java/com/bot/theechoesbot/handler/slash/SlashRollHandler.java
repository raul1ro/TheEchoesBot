package com.bot.theechoesbot.handler.slash;

import com.bot.theechoesbot.core.Globals;
import com.bot.theechoesbot.entity.ServerData;
import com.bot.theechoesbot.handler.slash.template.SlashHandler;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

/**
 * Implement /roll
 */
@SuppressWarnings("DataFlowIssue")
public class SlashRollHandler implements SlashHandler{

	@Override
	public void handle(SlashCommandInteractionEvent event, ServerData serverData){

		//extract he input value
		long upperLimit = 100L; //default value
		try{
			upperLimit = event.getOption("upper_limit").getAsLong();
		}catch(Exception ignored){}

		long rollNumber = Globals.RANDOM.nextLong(upperLimit + 1);

		String userid = event.getMember().getId();

		event.reply("<@" + userid + "> rolled: " + rollNumber + " *[0, " + upperLimit + "]*").queue();

	}

}
