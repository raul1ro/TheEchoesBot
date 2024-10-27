package com.bot.theechoesbot.command;

import com.bot.theechoesbot.command.template.SlashCommand;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageHistory;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.middleman.StandardGuildMessageChannel;
import net.dv8tion.jda.api.entities.channel.unions.GuildChannelUnion;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class ClearChannelSlashCommand extends SlashCommand{

	private static final Logger logger = LoggerFactory.getLogger(ClearChannelSlashCommand.class);

	public ClearChannelSlashCommand(){
		super(
			"clear-channel",
			List.of(),
			"Delete all the messages from a channel",
			List.of(
				new OptionData(
					OptionType.CHANNEL,
					"channel",
					"The channel to clear.",
					true
				),
				new OptionData(
					OptionType.BOOLEAN,
					"only_app",
					"Delete only APP messages",
					true
				)
			)
		);
	}

	@Override
	protected void executeImpl(SlashCommandInteraction interaction){

		interaction.deferReply().queue();

		//get the inputs
		GuildChannelUnion channel = interaction.getOption("channel").getAsChannel();
		boolean onlyApp = interaction.getOption("only_app").getAsBoolean();

		//check the type of channel
		ChannelType channelType = channel.getType();
		if(channelType != ChannelType.TEXT && channelType != ChannelType.NEWS){
			interaction.getHook().sendMessage("Invalid channel: " + channel.getName() + ". Allowed only text or news channels.").queue();
			return;
		}

		try{

			StandardGuildMessageChannel messageChannel = channel.asStandardGuildMessageChannel();
			MessageHistory messageHistory = messageChannel.getHistory();

			List<Message> deleteMessages = new ArrayList<>();
			List<Message> messages;
			while(!(messages = messageHistory.retrievePast(10).complete()).isEmpty()){

				for(Message message : messages){
					if(onlyApp){
						if(message.getAuthor().isBot()){
							deleteMessages.add(message);
						}
					}else{
						deleteMessages.add(message);
					}
				}

			}

			//delete messages
			//bunch if more than 1
			//single if only one
			if(deleteMessages.size() > 1){
				messageChannel.deleteMessages(deleteMessages).queue();
			}else if(deleteMessages.size() == 1){
				deleteMessages.getFirst().delete().queue();
			}

			interaction.getHook().sendMessage("Deleted " + deleteMessages.size() + " messages").queue();

		}catch(Exception e){
			logger.error("Error while clearing channel", e);
			interaction.getHook().sendMessage("Error clearing channel: " + e.getMessage()).queue();
		}

	}

}
