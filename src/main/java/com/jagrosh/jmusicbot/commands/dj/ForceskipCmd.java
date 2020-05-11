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
package com.jagrosh.jmusicbot.commands.dj;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jmusicbot.Bot;
import com.jagrosh.jmusicbot.audio.AudioHandler;
import com.jagrosh.jmusicbot.commands.DJCommand;
import net.dv8tion.jda.core.entities.User;
import com.github.LastorderDC.josaformatter.KoreanUtils;

/**
 *
 * @author John Grosh <john.a.grosh@gmail.com>
 */
public class ForceskipCmd extends DJCommand 
{
    public ForceskipCmd(Bot bot)
    {
        super(bot);
        this.name = "forceskip";
        this.help = "현재 재생중인 노래를 강제로 스킵합니다";
        this.aliases = bot.getConfig().getAliases(this.name);
        this.bePlaying = true;
    }

    @Override
    public void doCommand(CommandEvent event) 
    {
        AudioHandler handler = (AudioHandler)event.getGuild().getAudioManager().getSendingHandler();
        User u = event.getJDA().getUserById(handler.getRequester());
        String josa = KoreanUtils.format("%s를",handler.getPlayer().getPlayingTrack().getInfo().title);
        josa = Character.toString(josa.charAt(josa.length() - 1));
        event.reply(event.getClient().getSuccess()+" 노래 **"+handler.getPlayer().getPlayingTrack().getInfo().title
                +"** " + josa + " 스킵했습니다 (신청자 "+(u==null ? "누군가" : "**"+u.getName()+"**")+")");
        handler.getPlayer().stopTrack();
    }
}
