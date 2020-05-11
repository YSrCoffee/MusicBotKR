/*
 * Copyright 2018 John Grosh <john.a.grosh@gmail.com>.
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
import com.github.LastorderDC.josaformatter.KoreanUtils;

/**
 *
 * @author John Grosh <john.a.grosh@gmail.com>
 */
public class PauseCmd extends DJCommand 
{
    public PauseCmd(Bot bot)
    {
        super(bot);
        this.name = "pause";
        this.help = "현재 재생중인 노래를 일시정지합니다";
        this.aliases = bot.getConfig().getAliases(this.name);
        this.bePlaying = true;
    }

    @Override
    public void doCommand(CommandEvent event) 
    {
        AudioHandler handler = (AudioHandler)event.getGuild().getAudioManager().getSendingHandler();
        if(handler.getPlayer().isPaused())
        {
            event.replyWarning("플레이어가 이미 일시정지되어 있습니다! `"+event.getClient().getPrefix()+"play` 명령으로 재생하세요!");
            return;
        }
        handler.getPlayer().setPaused(true);
        String josa = KoreanUtils.format("%s를",handler.getPlayer().getPlayingTrack().getInfo().title);
        josa = Character.toString(josa.charAt(josa.length() - 1));
        event.replySuccess("노래 **"+handler.getPlayer().getPlayingTrack().getInfo().title+"** " + josa + " 일시정지했습니다. `"+event.getClient().getPrefix()+"play` 를 입력해 다시 재생하세요!");
    }
}
