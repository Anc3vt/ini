package ru.ancevt.util.ini;

import java.util.ArrayList;
import java.util.List;

public class Ini {

    private IniSection globalSection;
    private List<IniSection> sections;
    private char commentChar;
    private boolean ignoreCase;

    public Ini() {
        commentChar = IniChar.COMMENT_DEFAULT;
        globalSection = new IniSection(true);
        sections = new ArrayList<IniSection>();
    }

    public Ini(String sourceIniData) {
        this();
        setSourceData(sourceIniData);
    }

    public final void merge(Ini ini) {
        globalSection.merge(ini.getGlobalSection());

        final int sectionCount = ini.getSectionCount();
        for (int i = 0; i < sectionCount; i++) {
            final IniSection section = ini.getSection(i);
            final String sectionName = section.getName();

            if (hasSection(sectionName)) {
                final IniSection sectionHere = getSection(sectionName);
                sectionHere.merge(section);
            } else {
                try {
                    final IniSection sectionHere = section.clone();
                    addSection(sectionHere);

                } catch (CloneNotSupportedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public final void setIgnoreCase(boolean value) {
        this.ignoreCase = value;
    }

    public boolean getIgnoreCase() {
        return ignoreCase;
    }

    public final void setCommentChar(char commentChar) {
        IniUtil.validateCommentChar(commentChar);
        this.commentChar = commentChar;
    }

    public final char getCommentChar() {
        return commentChar;
    }

    public final void setSourceData(String sourceIniData) {
        clear();
        final IniParser parser = new IniParser();
        parser.parse(this, sourceIniData);
    }

    //public final String getSourceData() {
    //    return this.stringify(getCommentChar(), false);
    //}

    public final void clear() {
        getGlobalSection().clear();
        while (sections.size() > 0) {
            sections.remove(0).clear();
        }
    }

    public final int getSectionCount() {
        return sections.size();
    }

    public final IniSection getSection(int index) {
        return sections.get(index);
    }

    public final IniSection getSection(String name) {
        if (name == null) {
            return getGlobalSection();
        }

        for (final IniSection s : sections) {
            if (getIgnoreCase()) {
                if (name.equalsIgnoreCase(s.getName())) {
                    return s;
                }
            } else {
                if (name.equals(s.getName())) {
                    return s;
                }
            }
        }

        return null;
    }

    public final IniSection getGlobalSection() {
        return globalSection;
    }

    public final void addSection(IniSection section) {
        sections.add(section);
    }

    public final void removeSection(IniSection section) {
        sections.remove(section);
    }

    public final void removeSection(String sectionName) {
        removeSection(getSection(sectionName));
    }

    public final String stringify(char commentChar, boolean unixEndLine) {
        final StringBuilder stringBuilder = new StringBuilder();

        if (!globalSection.isEmpty()) {
            stringBuilder.append(globalSection.stringify(commentChar, unixEndLine));
        }

        for (final IniSection s : sections) {
            stringBuilder.append(s.stringify(commentChar, unixEndLine));
        }

        return stringBuilder.toString();
    }

    public final String stringify() {
        return stringify(IniChar.COMMENT_DEFAULT, false);
    }

    public final void addLine(String key, String value) {
        getGlobalSection().addLine(new IniLine(key, value));
    }

    public final void addLine(String sectionName, String key, String value) {
        final IniSection section = getSection(sectionName);
        section.addLine(new IniLine(key, value));
    }

    public final void removeLine(String sectionName, String key) {
        final IniSection section = getSection(sectionName);
        section.removeLine(key);
    }

    public final void removeLine(String key) {
        IniSection.ignoreCase = ignoreCase;
        getGlobalSection().removeLine(key);
        IniSection.ignoreCase = false;
    }

    public final boolean hasSection(String sectionName) {
        return getSection(sectionName) != null;
    }
    
    public final boolean hasKey(String sectionName, String key) {
        final IniSection section = sectionName == null ? getGlobalSection() : getSection(sectionName);
        if(section == null) {
            return false;
        }
        IniSection.ignoreCase = ignoreCase;
        final IniLine line = section.getLine(key);
        IniSection.ignoreCase = false;
        if (line == null) {
            return false;
        }
        return true;
    }

    public final String getString(String sectionName, String key) {
        final IniSection section = sectionName == null ? getGlobalSection() : getSection(sectionName);
        if (section == null) {
            return null;
        }
        IniSection.ignoreCase = ignoreCase;
        final IniLine line = section.getLine(key);
        IniSection.ignoreCase = false;
        if (line == null) {
            return null;
        }
        return line.getValue();
    }

    public final String getString(String sectionName, String key, String defaultValue) {
        final IniSection section = sectionName == null ? getGlobalSection() : getSection(sectionName);
        if (section == null) {
            return defaultValue;
        }
        final IniLine line = section.getLine(key);
        if (line == null) {
            return defaultValue;
        }
        return line.getValue();
    }

    public final int getInt(String sectionName, String key) {
        final String result = getString(sectionName, key);
        return Integer.parseInt(result);
    }

    public final int getInt(String sectionName, String key, int defaultValue) {
        final String result = getString(sectionName, key, String.valueOf(defaultValue));
        return Integer.parseInt(result);
    }

    public final boolean getBoolean(String sectionName, String key) {
        final String result = getString(sectionName, key);
        return Boolean.parseBoolean(result);
    }

    public final boolean getBoolean(String sectionName, String key, boolean defaultValue) {
        final String result = getString(sectionName, key, String.valueOf(defaultValue));
        return Boolean.parseBoolean(result);
    }

    public final float getFloat(String sectionName, String key) {
        final String result = getString(sectionName, key);
        return Float.parseFloat(result);
    }

    public final float getFloat(String sectionName, String key, float defaultValue) {
        final String result = getString(sectionName, key, String.valueOf(defaultValue));
        return Float.parseFloat(result);
    }

    public final double getDouble(String sectionName, String key) {
        final String result = getString(sectionName, key);
        return Double.parseDouble(result);
    }

    public final double getDouble(String sectionName, String key, double defaultValue) {
        final String result = getString(sectionName, key, String.valueOf(defaultValue));
        return Double.parseDouble(result);
    }

    public final long getLong(String sectionName, String key) {
        final String result = getString(sectionName, key);
        return Long.parseLong(result);
    }

    public final long getLong(String sectionName, String key, long defaultValue) {
        final String result = getString(sectionName, key, String.valueOf(defaultValue));
        return Long.parseLong(result);
    }

    public final short getShort(String sectionName, String key) {
        final String result = getString(sectionName, key);
        return Short.parseShort(result);
    }

    public final short getShort(String sectionName, String key, short defaultValue) {
        final String result = getString(sectionName, key, String.valueOf(defaultValue));
        return Short.parseShort(result);
    }

    public final byte getByte(String sectionName, String key) {
        final String result = getString(sectionName, key);
        return Byte.parseByte(result);
    }

    public final byte getByte(String sectionName, String key, byte defaultValue) {
        final String result = getString(sectionName, key, String.valueOf(defaultValue));
        return Byte.parseByte(result);
    }

    public final void clearComment() {
        getGlobalSection().clearComment();

        for (final IniSection s : sections) {
            s.clearComment();
        }
    }

    public final void clearEmpty() {
        getGlobalSection().clearEmpty();

        for (final IniSection s : sections) {
            s.clearEmpty();
        }
    }

}
