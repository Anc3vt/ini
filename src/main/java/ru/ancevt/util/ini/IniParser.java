package ru.ancevt.util.ini;

public final class IniParser {

    public static Ini createIni(String iniData) {
        final IniParser iniParser = new IniParser();
        return iniParser.parse(iniData);
    }

    private Ini ini;
    private String sourceData;
    private IniSection section;

    public IniParser() {

    }

    public Ini parse(Ini ini, String iniData) {
        this.ini = ini;
        this.sourceData = iniData;
        actualParse();
        return ini;
    }

    public Ini parse(String iniData) {
        return parse(new Ini(), iniData);
    }

    private final void actualParse() {
        final String delimiter = Character.toString(IniChar.END_LINE);
        final String[] lines = sourceData.split(delimiter);

        section = ini.getGlobalSection();

        for (final String l : lines) {
            if (isLineEmpty(l)) {
                section.addLine(new IniLine());
                continue;
            }

            final String trimmed = l.trim();
            final char firstChar = trimmed.charAt(0);

            switch (firstChar) {
                case IniChar.COMMENT1:
                case IniChar.COMMENT2:
                    parseCommentLine(l);
                    break;
                case IniChar.OPEN_BRACE:
                    parseSectionLine(l);
                    break;
                default:
                    parseLine(l);
                    break;
            }

        }

    }

    private static boolean isLineEmpty(String line) {
        return line.trim().length() == 0;
    }

    private void parseLine(String line) {
        final String delimiter = Character.toString(IniChar.EQUALS); // =

        final IniLine iniLine = new IniLine();

        if (!isLineEmpty(line) && !line.contains(delimiter)) {
            throw new IniException(String.format("There is no '=' char in line \"%s\"", line));
        } else {
            final String[] s = line.split(delimiter, 2);
            final String left = s[0].trim();
            final String right = s.length > 1 ? s[1] : "";//.trim();
            iniLine.setKey(left);
            iniLine.setValue(right);
        }

        section.addLine(iniLine);
    }

    private void parseCommentLine(String line) {
        final String commentText = line.trim().substring(1).trim();
        final IniLine iniLine = new IniLine(commentText);
        section.addLine(iniLine);
    }

    private void parseSectionLine(String line) {
        line = line.trim();
        final String sectionName = line.substring(1, line.length() - 1);
        section = new IniSection(sectionName);
        ini.addSection(section);
    }

}
