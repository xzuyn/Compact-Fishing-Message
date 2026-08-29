package starship.cfm.wallMessage;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.scores.DisplaySlot;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.Scoreboard;
import org.joml.Matrix3x2f;
import org.joml.Matrix3x2fStack;
import starship.cfm.CompactFishingMessage;
import starship.cfm.modMenu.ConfigData;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// Tracks & renders stats for the "Hole in the Wall" minigame, driven purely by reading
// end-of-game chat lines ("Dodged X walls.", "Survived X traps.", "Outlived X players.")
// plus the scoreboard sidebar title (mirrors RecordOverlay's fishing-island detection).
public class WallMessage {
    private static final Pattern MCCI_TITLE_PATTERN = Pattern.compile("MCCI: (.+)");
    private static final String GAME_MODE_NAME = "HOLE IN THE WALL";

    private static final Pattern DODGED_PATTERN = Pattern.compile("Dodged (\\d+) walls?\\.");
    private static final Pattern SURVIVED_PATTERN = Pattern.compile("Survived (\\d+) traps?\\.");
    private static final Pattern OUTLIVED_PATTERN = Pattern.compile("Outlived (\\d+) players?\\.");

    private static WallMessage instance;
    private static Minecraft client;

    public boolean ifInHoleInTheWall = false;
    private int linboTickCounter = 0;

    private int gamesPlayed = 0;
    private int totalWallsDodged = 0;
    private int totalTrapsSurvived = 0;
    private int totalPlayersOutlived = 0;

    private int lastWallsDodged = -1;
    private int lastTrapsSurvived = -1;
    private int lastPlayersOutlived = -1;

    public WallMessage(CompactFishingMessage cfm) {
        WallMessage.instance = this;
    }

    public static WallMessage getInstance() {
        return instance;
    }

    public void tick(Minecraft client) {
        if (client != null && client.player != null && client.level != null) {
            WallMessage.client = client;
            getGameStateFromScoreboard();
        } else {
            ifInHoleInTheWall = false;
            linboTickCounter = 0;
        }
    }

    private void getGameStateFromScoreboard() {
        Scoreboard scoreboard = client.level.getScoreboard();
        Objective objective = scoreboard.getDisplayObjective(DisplaySlot.SIDEBAR);
        if (objective != null) {
            String objectiveName = objective.getDisplayName().getString();
            Matcher matcher = MCCI_TITLE_PATTERN.matcher(objectiveName);
            if (matcher.find()) {
                String modeName = matcher.group(1);
                ifInHoleInTheWall = modeName.contains(GAME_MODE_NAME);
                linboTickCounter = 0;
            } else {
                tickLinboTimeout();
            }
        } else {
            tickLinboTimeout();
        }
    }

    private void tickLinboTimeout() {
        if (linboTickCounter >= 20 * 3) {
            ifInHoleInTheWall = false;
            linboTickCounter = 0;
        } else linboTickCounter++;
    }

    // called for every incoming system chat message (see MixinChatListener), regardless
    // of whether ifInHoleInTheWall is currently true, since the end-of-game summary lines
    // can arrive right as the scoreboard is switching back to the lobby.
    public void detectText(Component message) {
        if (!ConfigData.getInstance().enableWallRecordOverlay) return;
        String msg = message.getString();

        Matcher dodgedMatcher = DODGED_PATTERN.matcher(msg);
        if (dodgedMatcher.find()) {
            lastWallsDodged = Integer.parseInt(dodgedMatcher.group(1));
            totalWallsDodged += lastWallsDodged;
        }

        Matcher survivedMatcher = SURVIVED_PATTERN.matcher(msg);
        if (survivedMatcher.find()) {
            lastTrapsSurvived = Integer.parseInt(survivedMatcher.group(1));
            totalTrapsSurvived += lastTrapsSurvived;
        }

        Matcher outlivedMatcher = OUTLIVED_PATTERN.matcher(msg);
        if (outlivedMatcher.find()) {
            lastPlayersOutlived = Integer.parseInt(outlivedMatcher.group(1));
            totalPlayersOutlived += lastPlayersOutlived;
            gamesPlayed++; // "Outlived" is always the last of the three summary lines
        }
    }

    public void render(GuiGraphicsExtractor guiGraphics) {
        if (client == null || client.options == null || client.player == null || client.level == null) return;
        if (!ConfigData.getInstance().enableWallRecordOverlay) return;
        if (!ConfigData.getInstance().wallRecordOverlayAlwaysShows && !ifInHoleInTheWall) return;
        if (gamesPlayed == 0) return; // nothing recorded yet this session

        int x = ConfigData.getInstance().wallRecordRenderTextX;
        int y = ConfigData.getInstance().wallRecordRenderTextY;
        float scale = ConfigData.getInstance().wallRecordRenderScale;
        int backgroundColor = ConfigData.getInstance().wallRecordBackgroundAlphaColor << 24;
        int textColor = 0xFF000000 | (ConfigData.getInstance().wallRecordTextRGBColor & 0xFFFFFF);

        Font font = client.font;
        Matrix3x2fStack matrices = guiGraphics.pose();
        Matrix3x2f backupMatrix = new Matrix3x2f(matrices);

        List<String> lines = getRenderLines();

        int maxTextWidth = 0;
        for (String line : lines) maxTextWidth = Math.max(maxTextWidth, font.width(line));
        int textStartX = 4;
        int rightPadding = 4;
        int maxWidth = textStartX + maxTextWidth + rightPadding;
        float lineHeight = 11.5f;
        int totalHeight = (int) ((lineHeight + 1) * lines.size());

        guiGraphics.fill(x, y, x + (int) (maxWidth * scale), y + (int) (totalHeight * scale), backgroundColor);
        matrices.translate(x, y);
        matrices.scale(scale, scale);

        int i = 0;
        for (String line : lines) {
            int lineY = (int) (4 + i * lineHeight);
            i++;
            guiGraphics.text(font, line, textStartX, lineY, textColor, false);
        }
        matrices.set(backupMatrix);
    }

    private List<String> getRenderLines() {
        List<String> lines = new ArrayList<>();
        lines.add("Hole in the Wall");
        lines.add("Games: " + gamesPlayed);
        lines.add("Walls Dodged: " + totalWallsDodged + lastValueSuffix(lastWallsDodged));
        lines.add("Traps Survived: " + totalTrapsSurvived + lastValueSuffix(lastTrapsSurvived));
        lines.add("Players Outlived: " + totalPlayersOutlived + lastValueSuffix(lastPlayersOutlived));
        return lines;
    }

    private String lastValueSuffix(int lastValue) {
        return lastValue >= 0 ? " (last: " + lastValue + ")" : "";
    }

    public void resetSession() {
        gamesPlayed = 0;
        totalWallsDodged = 0;
        totalTrapsSurvived = 0;
        totalPlayersOutlived = 0;
        lastWallsDodged = -1;
        lastTrapsSurvived = -1;
        lastPlayersOutlived = -1;
    }
}
