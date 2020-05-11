package com.jagrosh.jmusicbot.commands.dj;


import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jmusicbot.Bot;
import com.jagrosh.jmusicbot.audio.AudioHandler;
import com.jagrosh.jmusicbot.audio.QueuedTrack;
import com.jagrosh.jmusicbot.commands.DJCommand;
import com.jagrosh.jmusicbot.queue.FairQueue;
import com.github.LastorderDC.josaformatter.KoreanUtils;

/**
 * Command that provides users the ability to move a track in the playlist.
 */
public class MoveTrackCmd extends DJCommand
{

    public MoveTrackCmd(Bot bot)
    {
        super(bot);
        this.name = "movetrack";
        this.help = "대기열에서 노래를 다른 위치로 이동합니다";
        this.arguments = "<기존 위치> <새 위치>";
        this.aliases = bot.getConfig().getAliases(this.name);
        this.bePlaying = true;
    }

    @Override
    public void doCommand(CommandEvent event)
    {
        int from;
        int to;

        String[] parts = event.getArgs().split("\\s+", 2);
        if(parts.length < 2)
        {
            event.replyError("유효한 두개의 위치가 필요합니다.");
            return;
        }

        try
        {
            // Validate the args
            from = Integer.parseInt(parts[0]);
            to = Integer.parseInt(parts[1]);
        }
        catch (NumberFormatException e)
        {
            event.replyError("위치 지정이 잘못되었습니다.");
            return;
        }

        if (from == to)
        {
            event.replyError("같은 위치로 이동할수 없습니다.");
            return;
        }

        // Validate that from and to are available
        AudioHandler handler = (AudioHandler) event.getGuild().getAudioManager().getSendingHandler();
        FairQueue<QueuedTrack> queue = handler.getQueue();
        if (isUnavailablePosition(queue, from))
        {
            String reply = KoreanUtils.format("`%d`는 대기열에서 올바른 위치가 아닙니다!", from);
            event.replyError(reply);
            return;
        }
        if (isUnavailablePosition(queue, to))
        {
            String reply = KoreanUtils.format("`%d`는 대기열에서 올바른 위치가 아닙니다!", to);
            event.replyError(reply);
            return;
        }

        // Move the track
        QueuedTrack track = queue.moveItem(from - 1, to - 1);
        String trackTitle = track.getTrack().getInfo().title;
        String josa = KoreanUtils.format("%s를",trackTitle);
        josa = Character.toString(josa.charAt(josa.length() - 1));
        String reply = String.format("`%d` 번째 노래 **%s**%s `%d`번째로 옮겼습니다.", from, trackTitle, josa, to);
        event.replySuccess(reply);
    }

    private static boolean isUnavailablePosition(FairQueue<QueuedTrack> queue, int position)
    {
        return (position < 1 || position > queue.size());
    }
}