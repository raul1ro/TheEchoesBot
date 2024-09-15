package com.bot.theechoesbot.core.service;

import com.bot.theechoesbot.core.service.template.GameCrawler;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Register service
 */
@SuppressWarnings("DataFlowIssue")
public class RegisterService{

	private final Logger logger = LoggerFactory.getLogger(RegisterService.class);

	private final GameCrawler gameCrawler = new GameCrawlerImpl();

	/**
	 * Registering the user as Intern. Request come from a button
	 */
	public void registerIntern(ButtonInteractionEvent event, Member member){

		try{

			//get the role - the id was taken manually.
			Role internRole = event.getJDA().getRoleById(1144309530684035223L);

			//add the role to the member
			event.getGuild()
				.addRoleToMember(
					member,
					internRole
				).queue();

			//reply
			event.reply("Register successfully.\nRole: " + internRole.getName() + "\nIf the registration is wrong contact <@328569043974094849> or <@658643411120685066>.").setEphemeral(true).queue();

		}catch(Exception e){

			logger.error("Error registering intern.", e);
			event.reply("Error registering as Intern.").setEphemeral(true).queue();
		}

	}

	/**
	 * Create a modal which required inputs before registering as member. Request come from a button.
	 */
	public void createModalRegisterMember(ButtonInteractionEvent event){

		try{

			//ask for character name
			TextInput characterNameInput = TextInput.create("input_character_name", "Character name", TextInputStyle.SHORT)
				.setMinLength(2)
				.setMaxLength(12)
				.setRequired(true)
				.build();

			//create the modal
			Modal modalRegisterMember = Modal.create("modal_register_member", "Register as member")
				.addActionRow(characterNameInput)
				.build();

			event.replyModal(modalRegisterMember).queue();

		}catch(Exception e){

			logger.error("Error initializing registration as member.", e);
			event.reply("Error initializing registration as member.").setEphemeral(true).queue();

		}

	}

	/**
	 * Register the user as Member. The request comes from a modal.
	 */
	public void registerMember(ModalInteractionEvent event, Member member){

		try{

			String characterName = event.getValue("input_character_name").getAsString();

			//get the user data and validate it.
			String[] userData = gameCrawler.getUserData(characterName);
			if(userData == null){
				event.reply("The character \"" + characterName + "\" not found in guild.\nIf you think this is an error contact <@328569043974094849> or <@658643411120685066>.").setEphemeral(true).queue();
				return;
			}

			//pre-load
			characterName = userData[0];
			String guildRank = userData[1];

			//if the character from guild is no longer Bober - that means he was already registered.
			if(!guildRank.equals("Bober")){
				event.reply("The character \"" + characterName + "\" is already registered.\nIf you think this is an error contact <@328569043974094849> or <@658643411120685066>.").setEphemeral(true).queue();
				return;
			}

			//pre-load
			Guild guild = event.getGuild();
			Role memberRole = event.getJDA().getRoleById(1110962256197468170L);

			//if the nickname is taken by another member
			boolean memberFound = !(guild.getMembersByNickname(characterName, true).isEmpty());
			if(memberFound){
				event.reply("The character \"" + characterName + "\" is already registered. If you think this is an error contact <@328569043974094849> or <@658643411120685066>.").setEphemeral(true).queue();
				return;
			}

			//set nickname and role
			guild.modifyNickname(member, characterName).queue();
			guild.addRoleToMember(member, memberRole).queue();

			//reply
			event.reply("Register successfully.\nNickname: " + characterName + "\nRole: " + memberRole.getName() + "\nIf the registration is wrong contact <@328569043974094849> or <@658643411120685066>.").setEphemeral(true).queue();

		}catch(Exception e){

			logger.error("Error registering member.", e);
			event.reply("Error registering as Member.").setEphemeral(true).queue();

		}

	}

}
