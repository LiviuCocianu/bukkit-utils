package io.github.idoomful.bukkitutils.object;
import java.util.NoSuchElementException;

import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Team;
import org.bukkit.scoreboard.Scoreboard;

import static org.bukkit.Bukkit.getScoreboardManager;
import static org.bukkit.Bukkit.getServer;
import static org.bukkit.ChatColor.COLOR_CHAR;
import static org.bukkit.ChatColor.getLastColors;
import static org.bukkit.scoreboard.DisplaySlot.SIDEBAR;

/**
 * A 32 character no flicker scoreboard implementation, fast and lightweight.
 * Implementation created by
 * @author Exerosis
 */
@SuppressWarnings("deprecation")
public class ExeScoreboard {
    private static final int MAX_LINES = 15;
    private static final int MAX_CHARACTERS;
    private static final String[] BLANKS = new String[MAX_LINES];
    private final Team[] teams = new Team[MAX_LINES];
    private final Objective objective;

    static {
        MAX_CHARACTERS = !getServer().getVersion().contains("1.1") ? 16 : 64;
        for (int i = 0; i < MAX_LINES; ++i)
            BLANKS[i] = new String(new char[]{COLOR_CHAR, (char) ('s' + i)});
    }

    /**
     * Construct a new {@link ExeScoreboard} wrapping the given {@link org.bukkit.scoreboard.Scoreboard}.
     *
     * @param board
     * 		- The {@link org.bukkit.scoreboard.Scoreboard} to wrap.
     */
    public ExeScoreboard(Scoreboard board) {
        board.clearSlot(SIDEBAR);
        objective = board.registerNewObjective("sidebar", "dummy");
        objective.setDisplaySlot(SIDEBAR);
        for (int i = 0; i < MAX_LINES; i++)
            (teams[i] = board.registerNewTeam(BLANKS[i])).addEntry(BLANKS[i]);
    }

    /**
     * Construct a new {@link ExeScoreboard} wrapping the main {@link org.bukkit.scoreboard.Scoreboard}.
     */
    public ExeScoreboard() { this(getScoreboardManager().getMainScoreboard()); }

    /**
     * Sets the title to the given value.
     *
     * @param title
     * 		- The new title.
     */
    public void title(Object title) { objective.setDisplayName(title.toString()); }

    /**
     * Sets the line at a given index to the given text and score.
     *
     * @param index
     * 		- The index of the line.
     * @param text
     * 		- The text 32-128 characters max.
     * @param score
     * 		- The score to display for this line.
     * @return - The index of the line.
     */
    public int line(int index, String text, int score) {
        final int max = MAX_CHARACTERS, min = MAX_CHARACTERS - 1;
        final int split = text.length() < max ? 0 : text.charAt(min) == '§' ? min : max;
        final String prefix = split == 0 ? text : text.substring(0, split);
        final String suffix = split == 0 ? "" : text.substring(split);
        objective.getScore(BLANKS[index]).setScore(score);
        if (split != max) teams[index].setSuffix(suffix); else
            teams[index].setSuffix(getLastColors(prefix) + suffix);
        teams[index].setPrefix(prefix); return index;
    }

    /**
     * Sets the line at a given index to the given text with a score of 1.
     *
     * @param index
     * 		- The index of the line.
     * @param text
     * 		- The text. ~32 characters max.
     * @return - The index of the line.
     */
    public int line(int index, String text) { return line(index, text, index); }

    /**
     * Sets the first empty line to the given text with a score of 1.
     *
     * @param text
     * 		- The text. ~32 characters max.
     * @return - The index of the line.
     */
    public int line(String text) {
        for (int i = 0; i < MAX_LINES; i++)
            if (teams[i].getPrefix().isEmpty())
                return line(i, text);
        throw new NoSuchElementException("No empty lines");
    }


    /**
     * Removes the line at the given index.
     *
     * @param index
     * 		- The index of the line.
     * @return - {@code true} if the line was previously set.
     */
    public boolean remove(int index) {
        if (index >= MAX_LINES) return false;
        objective.getScoreboard().resetScores(BLANKS[index]);
        return true;
    }
}