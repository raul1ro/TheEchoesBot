package com.bot.theechoesbot.core.handler.slash;

import com.bot.theechoesbot.core.crawler.template.GameCrawler;
import com.bot.theechoesbot.core.handler.slash.template.SlashHandler;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Implement /register
 */
@SuppressWarnings("DataFlowIssue")
public class SlashRegisterHandler implements SlashHandler{

	private final Logger logger = LoggerFactory.getLogger(SlashRegisterHandler.class);

	private final GameCrawler gameCrawler;

	public SlashRegisterHandler(GameCrawler gameCrawler){
		this.gameCrawler = gameCrawler;
	}

	@Override
	public void handle(SlashCommandInteractionEvent event){

		try{

			//get the roles of user and validate it
			List<Role> roles = event.getMember().getRoles();
			if(!roles.isEmpty()){
				event.reply("You are already registered.\nIf you think this is an error contact <@328569043974094849>.").setEphemeral(true).queue();
				return;
			}

			//get the inputs
			String role = event.getOption("role").getAsString();
			String nickname = event.getOption("character_name").getAsString();

			//execute based on role
			switch(role){

				case "intern":
					setIntern(event);
					break;
				case "member":
					setMember(event, nickname);
					break;

			}

		}catch(Exception e){

			logger.error("Registration error", e);
			event.reply("Error during execution.\nCheck the inputs and try again.\nIf the problem persists contact <@328569043974094849>.").setEphemeral(true).queue();

		}

	}

	/**
	 * Set the user as Intern.
	 */
	private void setIntern(SlashCommandInteractionEvent event){

		//get the role - the id was taken manually.
		Role internRole = event.getJDA().getRoleById(1144309530684035223L);

		//add the role to the member
		event.getGuild()
			.addRoleToMember(
				event.getMember(),
				internRole
			).queue();

		//reply
		event.reply("Register successfully.\nRole: " + internRole.getName() + "\nIf the registration is wrong contact <@328569043974094849>.").setEphemeral(true).queue();

	}

	/**
	 * Set the user as Member.
	 */
	private void setMember(SlashCommandInteractionEvent event, String characterName){

		//get the user data and validate it.
		String[] userData = gameCrawler.getUserData(characterName);
		if(userData == null){
			event.reply("The character \"" + characterName + "\" not found in guild.\nIf you think this is an error contact <@328569043974094849>.").setEphemeral(true).queue();
			return;
		}

		//pre-load
		characterName = userData[0];
		String guildRank = userData[1];

		//if the character from guild is no longer Bober - that means he was already registered.
		if(!guildRank.equals("Bober")){
			event.reply("The character \"" + characterName + "\" is already registered.\nIf you think this is an error contact <@328569043974094849>.").setEphemeral(true).queue();
			return;
		}


		//pre-load
		Guild guild = event.getGuild();
		Member member = event.getMember();
		Role memberRole = event.getJDA().getRoleById(1110962256197468170L);

		//if the nickname is taken by another member
		boolean memberFound = !(guild.getMembersByNickname(characterName, true).isEmpty());
		if(memberFound){
			event.reply("The character \"" + characterName + "\" is already registered. If you think this is an error contact <@328569043974094849>.").setEphemeral(true).queue();
			return;
		}

		//set nickname and role
		guild.modifyNickname(member, characterName).and(
			guild.addRoleToMember(member, memberRole)
		).queue();

		//reply
		event.reply("Register successfully.\nNickname: " + characterName + "\nRole: " + memberRole.getName() + "\nIf the registration is wrong contact <@328569043974094849>.").setEphemeral(true).queue();

	}

}
