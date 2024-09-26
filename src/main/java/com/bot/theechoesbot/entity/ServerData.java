package com.bot.theechoesbot.entity;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.NewsChannel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;

public class ServerData{

	private final long guildId;
	private final long botChannelId;
	private final long channelNewsAnnouncesId;
	private final long channelVoiceEventId;
	private final long channelTextScheduleId;
	private final long channelTextRegisterId;
	private final long roleInternId;
	private final long roleMemberId;

	private Guild guild;
	private TextChannel botChannel;
	private NewsChannel newsAnnouncesChannel;
	private VoiceChannel voiceEventChannel;
	private TextChannel textScheduleChannel;
	private TextChannel textRegisterChannel;
	private Role internRole;
	private Role memberRole;

	public ServerData(
		long guildId, long botChannelId,
		long channelNewsAnnouncesId, long channelVoiceEventId, long channelTextScheduleId, long channelTextRegisterId,
		long roleInternId, long roleMemberId
	){
		this.guildId = guildId;
		this.botChannelId = botChannelId;
		this.channelNewsAnnouncesId = channelNewsAnnouncesId;
		this.channelVoiceEventId = channelVoiceEventId;
		this.channelTextScheduleId = channelTextScheduleId;
		this.channelTextRegisterId = channelTextRegisterId;
		this.roleInternId = roleInternId;
		this.roleMemberId = roleMemberId;
	}

	@SuppressWarnings("DataFlowIssue")
	public void init(JDA jda){
		guild = jda.getGuildById(guildId);
		botChannel = guild.getTextChannelById(botChannelId);
		newsAnnouncesChannel = guild.getNewsChannelById(channelNewsAnnouncesId);
		voiceEventChannel = guild.getVoiceChannelById(channelVoiceEventId);
		textScheduleChannel = guild.getTextChannelById(channelTextScheduleId);
		textRegisterChannel = guild.getTextChannelById(channelTextRegisterId);
		internRole = guild.getRoleById(roleInternId);
		memberRole = guild.getRoleById(roleMemberId);
	}

	public TextChannel getBotChannel(){ return botChannel; }

	public long getBotChannelId(){ return botChannelId; }

	public long getChannelNewsAnnouncesId(){ return channelNewsAnnouncesId; }

	public long getChannelTextRegisterId(){ return channelTextRegisterId; }

	public long getChannelTextScheduleId(){ return channelTextScheduleId; }

	public long getChannelVoiceEventId(){ return channelVoiceEventId; }

	public Guild getGuild(){ return guild; }

	public long getGuildId(){ return guildId; }

	public Role getInternRole(){ return internRole; }

	public Role getMemberRole(){ return memberRole; }

	public NewsChannel getNewsAnnouncesChannel(){ return newsAnnouncesChannel; }

	public long getRoleInternId(){ return roleInternId; }

	public long getRoleMemberId(){ return roleMemberId; }

	public TextChannel getTextRegisterChannel(){ return textRegisterChannel; }

	public TextChannel getTextScheduleChannel(){ return textScheduleChannel; }

	public VoiceChannel getVoiceEventChannel(){ return voiceEventChannel; }

}