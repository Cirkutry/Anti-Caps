package filter;

import com.AntiCaps;

public class MessagePipeline {
    private final AntiCaps plugin;

    public MessagePipeline(AntiCaps plugin) {
        this.plugin = plugin;
    }

    /**
     * This method will return what percentage (0.0-100.0) of the
     * message is in caps.
     * 
     * @param message - The message to check
     * @return - The percentage of the message that is in caps
     */
    public double countPercentage(String message) {
        int total = message.length();
        if (total == 0)
            return 0.0;
        int caps = 0;
        for (char c : message.toCharArray()) {
            if (Character.isUpperCase(c))
                caps++;
        }
        return ((double) caps / (double) total) * 100.0;
    }

    private String decapitalize(String word) {
        boolean firstCapital = Character.isUpperCase(word.charAt(0));

        if (firstCapital)
            return word.charAt(0) + word.substring(1).toLowerCase();
        else
            return word.toLowerCase();
    }

    public String filter(String message) {
        double perc = countPercentage(message);
        if (perc < getRatio())
            return message;
        if (message.length() < getLowerLimit())
            return message;

        // Go backwards, decapitalizing words as we go
        String[] words = message.split(" ");
        int index = words.length - 1;
        while (perc > getRatio() && index >= 0) {
            words[index] = decapitalize(words[index]);
            perc = countPercentage(String.join(" ", words));
            index--;
        }

        return String.join(" ", words);
    }

    public double getRatio() {
        return plugin.getConfig().getDouble("ratio");
    }

    public int getLowerLimit() {
        return plugin.getConfig().getInt("allow-messages-under");
    }
}
