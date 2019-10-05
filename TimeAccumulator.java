/*
 * Eric Yager
 */
package paint;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * Class to accumulate time the user has spent using various tools.
 * @author ericyager
 * @param <E> Tool type
 */
public class TimeAccumulator<E> {
    
    private final HashMap<E, Long> toolTimeDict;
    private E currentTool;
    private long switchTime;
    private String saveFile;

    /**
     * Constructor.
     * @param firstTool first tool selected
     * @param tools ArrayList of tools
     * @param saveFile file to save to
     */
    public TimeAccumulator(E firstTool, ArrayList<E> tools, String saveFile) {
        
        toolTimeDict = new HashMap<>();
        tools.forEach((x) -> {
            toolTimeDict.put(x, (long)0);
        });
        currentTool = firstTool;
        switchTime = System.currentTimeMillis();
        this.saveFile = saveFile;
        
    }
    
    /**
     * Constructor.
     * @param firstTool first tool selected
     * @param tools array of tools
     * @param saveFile file to save to
     */
    public TimeAccumulator(E firstTool, E[] tools, String saveFile) {
        
        toolTimeDict = new HashMap<>();
        for (E x: tools) {
            toolTimeDict.put(x, (long)0);
        }
        currentTool = firstTool;
        switchTime = System.currentTimeMillis();
        this.saveFile = saveFile;
        
    }
    
    /**
     * Log a switch in the tool being used.
     * @param newTool 
     */
    public void switchTools(E newTool) {
        
        long timeSpent = System.currentTimeMillis() - switchTime;
        switchTime = System.currentTimeMillis();
        toolTimeDict.put(currentTool, toolTimeDict.get(currentTool)+timeSpent);
        currentTool = newTool;
        
    }
    
    /**
     * Log tool use time even if a switch in tools has not occurred.
     */
    public void updateCurrentTime() {
        
        long timeSpent = System.currentTimeMillis() - switchTime;
        switchTime = System.currentTimeMillis();
        toolTimeDict.put(currentTool, toolTimeDict.get(currentTool)+timeSpent);
        
    }
    
    /**
     * Get strings indicating the total use time for each tool.
     * @return String array of the time spent using each tool.
     */
    public String[] getToolTimes() {
        
        String[] times = new String[toolTimeDict.size()];
        int index = 0;
        for (E key: toolTimeDict.keySet()) {
            long time = toolTimeDict.get(key)/1000;
            times[index] = key.toString() + ": " + time/60 + " minutes, " + time%60 + " seconds";
            index++;
        }
        return times;
        
    }
    
    /**
     * Get an array of string arrays in which the first element is the tool and
     * the second element is the time spent using that tool.
     * 
     * @return tool use times
     */
    public String[][] exportToolTimes() {
        
        String[][] times = new String[2][toolTimeDict.size()];
        int index = 0;
        for (E key: toolTimeDict.keySet()) {
            times[index][0] = key.toString();
            times[index][1] = Long.toString(toolTimeDict.get(key)/1000);
            index++;
        }
        return times;
        
    }
    
    /**
     * Save data to the log file.
     */
    public void saveToFile() {
        
        try {
            String oldData = new String(Files.readAllBytes(new File(saveFile).toPath()));
            FileWriter fw = new FileWriter(new File(saveFile));
            BufferedWriter writer = new BufferedWriter(fw);
            writer.write(new Date().toString() + "\n");
            for (String time: getToolTimes()) {
                writer.write(time+"\n");
            }
            writer.write("\n\n" + oldData);
            writer.close();
            fw.close();
        } catch (IOException ex) {
        }
        
        
    }
    
}
