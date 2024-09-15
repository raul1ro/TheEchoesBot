package com.bot.theechoesbot.object;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.NewsChannel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ServerData{

	private final long guildId;
	private final long voiceEventId;
	private final long scheduleId;
	private final long announcesId;
	private final long registerId;

	private Guild guild;
	private VoiceChannel voiceEventChannel;
	private TextChannel scheduleChannel;
	private NewsChannel announcesChannel;
	private TextChannel registerChannel;

	public ServerData(
		@Value("${discord.guildId}") long guildId,
		@Value("${discord.voiceEventId}") long voiceEventId,
		@Value("${discord.scheduleId}") long scheduleId,
		@Value("${discord.announcesId}") long announcesId,
		@Value("${discord.registerId}") long registerId){

		this.guildId = guildId;
		this.voiceEventId = voiceEventId;
		this.scheduleId = scheduleId;
		this.announcesId = announcesId;
		this.registerId = registerId;

	}

	@SuppressWarnings("DataFlowIssue")
	public void initChannels(JDA jda){

		guild = jda.getGuildById(guildId);
		voiceEventChannel = guild.getVoiceChannelById(voiceEventId);
		scheduleChannel = guild.getTextChannelById(scheduleId);
		announcesChannel = guild.getNewsChannelById(announcesId);
		registerChannel = guild.getTextChannelById(registerId);

	}

	public long getGuildId(){ return guildId; }

	public Guild getGuild(){ return guild; }

	public VoiceChannel getVoiceEventChannel(){ return voiceEventChannel; }

	public TextChannel getScheduleChannel(){ return scheduleChannel; }

	public NewsChannel getAnnouncesChannel(){ return announcesChannel; }

	public TextChannel getRegisterChannel(){ return registerChannel; }

}
