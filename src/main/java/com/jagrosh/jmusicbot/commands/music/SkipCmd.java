/*
 * Copyright 2016 John Grosh <john.a.grosh@gmail.com>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jagrosh.jmusicbot.commands.music;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jmusicbot.Bot;
import com.jagrosh.jmusicbot.audio.AudioHandler;
import com.jagrosh.jmusicbot.commands.MusicCommand;
import net.dv8tion.jda.api.entities.User;

/**
 *
 * @author John Grosh <john.a.grosh@gmail.com>
 */
public class SkipCmd extends MusicCommand 
{
    public SkipCmd(Bot bot)
    {
        super(bot);
        this.name = "skip";
        this.help = "현재 노래를 스킵하도록 투표합니다";
        this.aliases = bot.getConfig().getAliases(this.name);
        this.beListening = true;
        this.bePlaying = true;
    }

    @Override
    public void doCommand(CommandEvent event) 
    {
        AudioHandler handler = (AudioHandler)event.getGuild().getAudioManager().getSendingHandler();
        if(event.getAuthor().getIdLong()==handler.getRequester())
        {
            event.reply(event.getClient().getSuccess()+" 노래 **"+handler.getPlayer().getPlayingTrack().getInfo().title+"** 를 스킵했습니다");
            handler.getPlayer().stopTrack();
        }
        else
        {
            int listeners = (int)event.getSelfMember().getVoiceState().getChannel().getMembers().stream()
                    .filter(m -> !m.getUser().isBot() && !m.getVoiceState().isDeafened()).count();
            String msg;
            if(handler.getVotes().contains(event.getAuthor().getId()))
                msg = event.getClient().getWarning()+" 이미 노래 스킵에 투표했습니다 `[";
            else
            {
                msg = event.getClient().getSuccess()+" 노래 스킵에 투표했습니다. `[";
                handler.getVotes().add(event.getAuthor().getId());
            }
            int skippers = (int)event.getSelfMember().getVoiceState().getChannel().getMembers().stream()
                    .filter(m -> handler.getVotes().contains(m.getUser().getId())).count();
            int required = (int)Math.ceil(listeners * .55);
            msg+= skippers+" 표, "+required+"/"+listeners+" 표 필요]`";
            if(skippers>=required)
            {
                User u = event.getJDA().getUserById(handler.getRequester());
                msg+="\n"+event.getClient().getSuccess()+" 노래 **"+handler.getPlayer().getPlayingTrack().getInfo().title
                    +"**"+(handler.getRequester()==0 ? "" : " (신청자 "+(u==null ? "누군가" : "**"+u.getName()+"**")+")를 스킵했습니다");
                handler.getPlayer().stopTrack();
            }
            event.reply(msg);
        }
    }
    
}
