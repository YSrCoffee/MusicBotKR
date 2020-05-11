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
import com.jagrosh.jmusicbot.audio.QueuedTrack;
import com.jagrosh.jmusicbot.commands.MusicCommand;
import com.jagrosh.jmusicbot.settings.Settings;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.User;
import com.github.LastorderDC.josaformatter.KoreanUtils;

/**
 *
 * @author John Grosh <john.a.grosh@gmail.com>
 */
public class RemoveCmd extends MusicCommand 
{
    public RemoveCmd(Bot bot)
    {
        super(bot);
        this.name = "remove";
        this.help = "대기열에서 노래를 제거합니다";
        this.arguments = "<위치|ALL>";
        this.aliases = bot.getConfig().getAliases(this.name);
        this.beListening = true;
        this.bePlaying = true;
    }

    @Override
    public void doCommand(CommandEvent event) 
    {
        AudioHandler handler = (AudioHandler)event.getGuild().getAudioManager().getSendingHandler();
        if(handler.getQueue().isEmpty())
        {
            event.replyError("대기열이 비어 있습니다!");
            return;
        }
        if(event.getArgs().equalsIgnoreCase("all"))
        {
            int count = handler.getQueue().removeAll(event.getAuthor().getIdLong());
            if(count==0)
                event.replyWarning("대기열이 비어 있습니다!!");
            else
                event.replySuccess(count+"개의 노래를 제거했습니다.");
            return;
        }
        int pos;
        try {
            pos = Integer.parseInt(event.getArgs());
        } catch(NumberFormatException e) {
            pos = 0;
        }
        if(pos<1 || pos>handler.getQueue().size())
        {
            event.replyError("위치는 1과 "+handler.getQueue().size()+" 사이 정수여야 합니다!");
            return;
        }
        Settings settings = event.getClient().getSettingsFor(event.getGuild());
        boolean isDJ = event.getMember().hasPermission(Permission.MANAGE_SERVER);
        if(!isDJ)
            isDJ = event.getMember().getRoles().contains(settings.getRole(event.getGuild()));
        QueuedTrack qt = handler.getQueue().get(pos-1);
        if(qt.getIdentifier()==event.getAuthor().getIdLong())
        {
            handler.getQueue().remove(pos-1);
            String josa = KoreanUtils.format("%s를",qt.getTrack().getInfo().title);
            josa = Character.toString(josa.charAt(josa.length() - 1));
            event.replySuccess("대기열에서 **"+qt.getTrack().getInfo().title+"** " + josa + " 제거했습니다");
        }
        else if(isDJ)
        {
            handler.getQueue().remove(pos-1);
            User u;
            try {
                u = event.getJDA().getUserById(qt.getIdentifier());
            } catch(Exception e) {
                u = null;
            }
            String josa = KoreanUtils.format("%s를",qt.getTrack().getInfo().title);
            josa = Character.toString(josa.charAt(josa.length() - 1));
            event.replySuccess("대기열에서 **"+qt.getTrack().getInfo().title
                    +"** " + josa + " 제거했습니다 (신청자 "+(u==null ? "누군가" : "**"+u.getName()+"**")+")");
        }
        else
        {
            String josa = KoreanUtils.format("%s를",qt.getTrack().getInfo().title);
            josa = Character.toString(josa.charAt(josa.length() - 1));
            event.replyError("본인이 추가하지 않은 노래이므로 **"+qt.getTrack().getInfo().title+"** "+ josa + " 제거할수 없습니다!");
        }
    }
}
