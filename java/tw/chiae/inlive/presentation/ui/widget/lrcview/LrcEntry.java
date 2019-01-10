package tw.chiae.inlive.presentation.ui.widget.lrcview;

import android.graphics.Color;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.format.DateUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import tw.chiae.inlive.presentation.ui.main.mergefilm.Log;

/**
 * Created by hzwangchenyan on 2016/10/19.
 */
class LrcEntry implements Comparable<LrcEntry> {

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    private String color;
    private int LrcParseMode = 0;
    private long time;
    private String text;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    private StaticLayout staticLayout;
    private TextPaint paint;

    private LrcEntry(long time, String text, String color) {
        this.time = time;
        this.text = text;
        this.color = color;
    }

    void init(TextPaint paint, int width) {
        this.paint = paint;
        paint.setColor(Color.parseColor(color));
        staticLayout = new StaticLayout(text, paint, width, Layout.Alignment.ALIGN_CENTER, 1f, 0f, false);
    }

    long getTime() {
        return time;
    }

    StaticLayout getStaticLayout() {
        return staticLayout;
    }

    float getTextHeight() {
        if (paint == null || staticLayout == null) {
            return 0;
        }
        return staticLayout.getLineCount() * paint.getTextSize();
    }

    @Override
    public int compareTo(LrcEntry entry) {
        if (entry == null) {
            return -1;
        }
        return (int) (time - entry.getTime());
    }

    static List<LrcEntry> parseLrc(File lrcFile) {
        if (lrcFile == null || !lrcFile.exists()) {
            return null;
        }

        List<LrcEntry> entryList = new ArrayList<>();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(lrcFile), "utf-8"));
            String line;
            while ((line = br.readLine()) != null) {
                List<LrcEntry> list = parseLine(line);
                if (list != null && !list.isEmpty()) {
                    entryList.addAll(list);
                }
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Collections.sort(entryList);
        return entryList;
    }

    static List<LrcEntry> parseLrc(String lrcText) {
        if (TextUtils.isEmpty(lrcText)) {
            return null;
        }

        List<LrcEntry> entryList = new ArrayList<>();
        String[] array = lrcText.split("\\n");
        for (String line : array) {
            List<LrcEntry> list = parseLine(line);
            if (list != null && !list.isEmpty()) {
                entryList.addAll(list);
            }
        }

        Collections.sort(entryList);
        return entryList;
    }

    private static List<LrcEntry> parseLine(String line) {
        if (TextUtils.isEmpty(line)) {
            return null;
        }

        if(line.charAt(0)==65279){
            if(line.length()>1) {
                line = line.substring(1);
            }
        }
        line = line.trim();

        Matcher lineMatcher = Pattern.compile("((\\[\\d\\d:\\d\\d\\.\\d\\d\\])+)(;\\w\\w\\w\\w\\w\\w;)(.+)").matcher(line);
        if (!lineMatcher.matches()) {
            return null;
        }

        String times = lineMatcher.group(1);
        String color = lineMatcher.group(3);
        String text = lineMatcher.group(4);
        Matcher colorMatcher = Pattern.compile("(\\w\\w\\w\\w\\w\\w)").matcher(color);
        if (!colorMatcher.find()) {
            color = "#FF"+"FFAA33";
        }else{
            color = "#FF"+colorMatcher.group(1);
        }

       // Matcher colorMather  = Pattern.compile("").matcher(text);
        //String color = lineMatcher.
        List<LrcEntry> entryList = new ArrayList<>();

        Matcher timeMatcher = Pattern.compile("\\[(\\d\\d):(\\d\\d)\\.(\\d\\d)\\]").matcher(times);
        while (timeMatcher.find()) {
            long min = Long.parseLong(timeMatcher.group(1));
            long sec = Long.parseLong(timeMatcher.group(2));
            long mil = Long.parseLong(timeMatcher.group(3));
            long time = min * DateUtils.MINUTE_IN_MILLIS + sec * DateUtils.SECOND_IN_MILLIS + mil * 10;
            entryList.add(new LrcEntry(time, text,color));
        }
        return entryList;
    }
}
