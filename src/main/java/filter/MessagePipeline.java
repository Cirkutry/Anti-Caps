package filter;

import com.AntiCaps;
import java.util.Locale;

/**
 * Filters messages with excessive capitalization.
 */
public class MessagePipeline {
    private final AntiCaps plugin;

    public MessagePipeline(AntiCaps plugin) {
        this.plugin = plugin;
    }

    /**
     * Returns the percentage (0.0-100.0) of letters that are uppercase.
     */
    public double countPercentage(String message) {
        int total = 0;
        int caps = 0;
        for (char c : message.toCharArray()) {
            if (Character.isLetter(c)) {
                total++;
                if (Character.isUpperCase(c))
                    caps++;
            }
        }
        if (total == 0)
            return 0.0;
        return ((double) caps / (double) total) * 100.0;
    }

    /**
     * Converts message to lowercase while preserving the first letter's capitalization.
     */
    private String normalizeCaps(String message) {
        int firstAlphaIndex = -1;
        boolean firstAlphaUpper = false;
        for (int i = 0; i < message.length(); i++) {
            char c = message.charAt(i);
            if (Character.isLetter(c)) {
                firstAlphaIndex = i;
                firstAlphaUpper = Character.isUpperCase(c);
                break;
            }
        }

        char[] normalized = message.toLowerCase(Locale.ROOT).toCharArray();
        if (firstAlphaIndex >= 0 && firstAlphaUpper) {
            normalized[firstAlphaIndex] = Character.toUpperCase(normalized[firstAlphaIndex]);
        }
        return new String(normalized);
    }

    /**
     * Filters the message by normalizing capitalization if it exceeds the configured ratio.
     */
    public String filter(String message) {
        double perc = countPercentage(message);
        if (perc < getRatio())
            return message;
        if (message.length() < getLowerLimit())
            return message;

        return normalizeCaps(message);
    }

    public double getRatio() {
        return plugin.getConfig().getDouble("ratio");
    }

    public int getLowerLimit() {
        return plugin.getConfig().getInt("allow-messages-under");
    }
}