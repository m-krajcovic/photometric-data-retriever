package cz.muni.physics.pdr.app.utils;

import org.apache.commons.lang3.StringUtils;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 17/04/16
 */
public class NumberUtils extends org.apache.commons.lang3.math.NumberUtils {
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
