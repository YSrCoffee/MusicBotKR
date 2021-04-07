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
import com.github.LastorderDC.josaformatter.KoreanUtils;

/**
 *
 * @author John Grosh <john.a.grosh@gmail.com>
 */
public class SkiptoCmd extends DJCommand 
{
    public SkiptoCmd(Bot bot)
    {
        super(bot);
        this.name = "skipto";
        this.help = "특정 노래로 스킵합니다";
        this.arguments = "<position>";
        this.aliases = bot.getConfig().getAliases(this.name);
        this.bePlaying = true;
    }

    @Override
    public void doCommand(CommandEvent event) 
    {
        int index = 0;
        try
        {
            index = Integer.parseInt(event.getArgs());
        }
        catch(NumberFormatException e)
        {
            event.reply(KoreanUtils.format(event.getClient().getError()+" `%s`는 올바른 숫자가 아닙니다!",event.getArgs()));
            return;
        }
        AudioHandler handler = (AudioHandler)event.getGuild().getAudioManager().getSendingHandler();
        if(index<1 || index>handler.getQueue().size())
        {
            event.reply(event.getClient().getError()+" 위치는 1과 "+handler.getQueue().size()+" 사이 정수여야 합니다!");
            return;
        }
        handler.getQueue().skip(index-1);
        String josa = KoreanUtils.format("%s를",handler.getQueue().get(0).getTrack().getInfo().title);
        josa = Character.toString(josa.charAt(josa.length() - 1));
        event.reply(event.getClient().getSuccess()+" 노래 **"+handler.getQueue().get(0).getTrack().getInfo().title+"**" + (josa.equals("을") ? "으로" : "로") + " 스킵합니다.");
        handler.getPlayer().stopTrack();
    }
}
