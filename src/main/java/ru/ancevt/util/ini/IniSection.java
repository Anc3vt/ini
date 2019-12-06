package ru.ancevt.util.ini;

import java.util.ArrayList;
import java.util.List;

public class IniSection {

    private String name;
    private final List<IniLine> lines;
    private boolean global;

    static boolean ignoreCase;

    IniSection(boolean globalSection) {
        this.global = globalSection;
        this.lines = new ArrayList<IniLine>();
    }

    public IniSection() {
        this(false);
    }

    public IniSection(String name) {
        this();
        this.name = name;
    }

    @Override
    protected IniSection clone() throws CloneNotSupportedException {
        final IniSection result = new IniSection(this.getName());
        for (final IniLine l : lines) {
            result.addLine(l.clone());
        }
        return result;
    }

    public final void merge(IniSection sectionMergeWith) {
        final int size = sectionMergeWith.size();
        for (int i = 0; i < size; i++) {
            final IniLine l = sectionMergeWith.getLine(i);
            try {
                addLine(l.clone());
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
        }
    }

    public final boolean hasLine(String key) {
        return getLine(key) != null;
    }

    public final void addLine(IniLine line) {
        final String key = line.getKey();

        if (key != null && hasLine(key)) {
            final IniLine l = getLine(key);
            l.setValue(line.getValue());
        } else {
            lines.add(line);
        }
    }

    public final boolean isEmpty() {
        return size() == 0;
    }

    public final void clear() {
        for (IniLine l : lines) {
            l.clear();
        }
        lines.clear();
    }

    public final void clearComment() {
        for (final IniLine l : lines) {
            if (l.isComment()) {
                lines.remove(l);
                clearComment();
                break;
            }
        }
    }

    public final void clearEmpty() {
        for (final IniLine l : lines) {
            if (l.isEmpty()) {
                lines.remove(l);
                clearEmpty();
                break;
            }
        }
    }

    /**
     * @return the global
     */
    public final boolean isGlobal() {
        return global;
    }

    /**
     * @param global the global to set
     */
    public final void setGlobal(boolean global) {
        this.global = global;
    }

    public final void removeLine(int index) {
        lines.remove(index);
    }

    public final void removeLine(String key) {
        for (final IniLine line : lines) {
            if (ignoreCase) {
                if (key.equalsIgnoreCase(line.getKey())) {
                    lines.remove(line);
                    break;
                }
            } else {
                if (key.equals(line.getKey())) {
                    lines.remove(line);
                    break;
                }
            }
        }
    }

    public final IniLine getLine(String key) {
        for (final IniLine line : lines) {
            if (ignoreCase) {
                if (key.equalsIgnoreCase(line.getKey())) {
                    return line;
                }
            } else {
                if (key.equals(line.getKey())) {
                    return line;
                }
            }
        }

        return null;
    }

    public final IniLine getLine(int index) {
        return lines.get(index);
    }

    public final int size() {
        return lines.size();
    }

    public final int effectiveSize() {
        int result = 0;
        for (final IniLine line : lines) {
            if (!line.isComment() && !line.isEmpty()) {
                result++;
            }
        }
        return result;
    }

    /**
     * @return the name
     */
    public final String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public final void setName(String name) {
        this.name = name;
    }

    public final String stringify(char commentChar) {
        final StringBuilder stringBuilder = new StringBuilder();

        if (!isGlobal()) {
            stringBuilder.append(IniChar.OPEN_BRACE).append(getName()).append(IniChar.CLOSE_BRRACE).append(IniChar.BR);
        }

        for (final IniLine l : lines) {
            stringBuilder.append(l.stringify(commentChar)).append(IniChar.BR);
        }

        return stringBuilder.toString();
    }

    public final String stringify() {
        return stringify(IniChar.COMMENT_DEFAULT);
    }
}
