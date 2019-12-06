package ru.ancevt.util.ini;

final class IniUtil {

    static final void validateCommentChar(char commentChar) {
        if (commentChar != IniChar.COMMENT1 && commentChar != IniChar.COMMENT2) {
            throw new IniException(String.format("Invalid comment char '%s' (must be ';' or '#')", commentChar));
        }
    }
}
