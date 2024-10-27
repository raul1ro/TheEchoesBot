package com.bot.theechoesbot.command;

import com.bot.theechoesbot.command.template.MessageCommand;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.middleman.StandardGuildMessageChannel;
import net.dv8tion.jda.api.interactions.commands.context.MessageContextInteraction;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import net.dv8tion.jda.api.interactions.modals.ModalInteraction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class EditMessageCommand extends MessageCommand{

	private final static Logger logger = LoggerFactory.getLogger(EditMessageCommand.class);

	public EditMessageCommand(){
		super(
			"Edit Message",
			List.of()
		);
	}

	@Override
	protected void executeImpl(MessageContextInteraction interaction){

		try{

			Message targetMessage = interaction.getTarget();

			//data
			TextInput channelId = TextInput.create("channel_id", "Channel Id", TextInputStyle.SHORT)
				.setRequired(true)
				.setValue(targetMessage.getChannelId())
				.build();
			TextInput messageId = TextInput.create("message_id", "Message Id", TextInputStyle.SHORT)
				.setRequired(true)
				.setValue(targetMessage.getId())
				.build();

			//ask for character name
			TextInput newMessage = TextInput.create("new_message", "New message", TextInputStyle.PARAGRAPH)
				.setMinLength(1)
				.setMaxLength(4000)
				.setRequired(true)
				.setValue(targetMessage.getContentRaw())
				.build();

			//create the modal
			Modal modalRegisterMember = Modal.create(
				"modal_edit_message",
					"Edit message"
				)
				.addActionRow(channelId)
				.addActionRow(messageId)
				.addActionRow(newMessage)
				.build();

			interaction.replyModal(modalRegisterMember).queue();

		}catch(Exception e){

			logger.error("Error initializing edit message.", e);
			interaction.reply("Error initializing edit message. Contact <@328569043974094849>.").setEphemeral(true).queue();

		}

	}

	public static void editMessage(ModalInteraction modal, Member member){

		modal.deferReply(true).queue();
		try{

			String channelId = modal.getValue("channel_id").getAsString();
			String messageId = modal.getValue("message_id").getAsString();
			String newMessage = modal.getValue("new_message").getAsString();

			modal.getJDA()
				.getChannelById(StandardGuildMessageChannel.class, channelId)
				.editMessageById(
					messageId,
					newMessage
				).queue(
					s -> modal.getHook().sendMessage("Message edited").queue(),
					e -> {
						logger.error("Error editing message.", e);
						modal.getHook().sendMessage("Error editing message: " + e.getMessage()).queue();
					}
				);

		}catch(Exception e){
			logger.error("Error editing message.", e);
			modal.getHook().sendMessage("Error editing message: " + e.getMessage()).queue();
		}

	}

}
