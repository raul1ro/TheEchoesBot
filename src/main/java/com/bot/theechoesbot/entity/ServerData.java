package com.bot.theechoesbot.entity;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.NewsChannel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;

public class ServerData{

	private final long guildId;
	private final long channelNewsAnnouncesId;
	private final long channelVoiceEventId;
	private final long channelTextScheduleId;
	private final long channelTextRegisterId;
	private final long roleInternId;
	private final long roleMemberId;

	private Guild guild;
	private NewsChannel newsAnnouncesChannel;
	private VoiceChannel voiceEventChannel;
	private TextChannel textScheduleChannel;
	private TextChannel textRegisterChannel;
	private Role internRole;
	private Role memberRole;

	public ServerData(
		long guildId,
		long channelNewsAnnouncesId, long channelVoiceEventId, long channelTextScheduleId, long channelTextRegisterId,
		long roleInternId, long roleMemberId
	){
		this.guildId = guildId;
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
		newsAnnouncesChannel = guild.getNewsChannelById(channelNewsAnnouncesId);
		voiceEventChannel = guild.getVoiceChannelById(channelVoiceEventId);
		textScheduleChannel = guild.getTextChannelById(channelTextScheduleId);
		textRegisterChannel = guild.getTextChannelById(channelTextRegisterId);
		internRole = guild.getRoleById(roleInternId);
		memberRole = guild.getRoleById(roleMemberId);
	}

	public long getGuildId(){ return guildId; }

	public long getChannelNewsAnnouncesId(){ return channelNewsAnnouncesId; }

	public long getChannelVoiceEventId(){ return channelVoiceEventId; }

	public long getChannelTextScheduleId(){ return channelTextScheduleId; }

	public long getChannelTextRegisterId(){ return channelTextRegisterId; }

	public long getRoleInternId(){ return roleInternId; }

	public long getRoleMemberId(){ return roleMemberId; }

	public Guild getGuild(){ return guild; }

	public void setGuild(Guild guild){ this.guild = guild; }

	public NewsChannel getNewsAnnouncesChannel(){ return newsAnnouncesChannel; }

	public void setNewsAnnouncesChannel(NewsChannel newsAnnouncesChannel){ this.newsAnnouncesChannel = newsAnnouncesChannel; }

	public VoiceChannel getVoiceEventChannel(){ return voiceEventChannel; }

	public void setVoiceEventChannel(VoiceChannel voiceEventChannel){ this.voiceEventChannel = voiceEventChannel; }

	public TextChannel getTextScheduleChannel(){ return textScheduleChannel; }

	public void setTextScheduleChannel(TextChannel textScheduleChannel){ this.textScheduleChannel = textScheduleChannel; }

	public TextChannel getTextRegisterChannel(){ return textRegisterChannel; }

	public void setTextRegisterChannel(TextChannel textRegisterChannel){ this.textRegisterChannel = textRegisterChannel; }

	public Role getInternRole(){ return internRole; }

	public void setInternRole(Role internRole){ this.internRole = internRole; }

	public Role getMemberRole(){ return memberRole; }

	public void setMemberRole(Role memberRole){ this.memberRole = memberRole; }

}