package cz.muni.physics.pdr.backend.utils;

import org.apache.commons.lang3.StringUtils;

/**
 * Extends Apache Commons NumberUtils
 * @author Michal Krajčovič
 * @version 1.0
 * @since 17/04/16
 * @see org.apache.commons.lang3.math.NumberUtils
 */
public class NumberUtils extends org.apache.commons.lang3.math.NumberUtils {
    /**
     * Checks if string is parsable, only changed from NumberUtils in Apache Commons is that it
     * also takes strings with '+' on start of string
     * @param str string to be checked if it is parsable
     * @return
     * @see org.apache.commons.lang3.math.NumberUtils
     */
    public static boolean isParsable(final String str) {
        if (str.endsWith(".")) {
            return false;
        }
        if (str.startsWith("-") || str.startsWith("+")) {
            return isDigits(StringUtils.replaceOnce(str.substring(1), ".", StringUtils.EMPTY));
        }
        return isDigits(StringUtils.replaceOnce(str, ".", StringUtils.EMPTY));
    }
}
