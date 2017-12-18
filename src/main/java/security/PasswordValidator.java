package security;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PasswordValidator{

    private static Pattern pattern;
    private static Matcher matcher;

    private static final String PASSWORD_PATTERN =
            "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%]).{6,20})";

    /**
     * Validate password with regular expression. Must contain digit, lower/uppercase character, special symbol (@#$%) and at least 6 characters
     * @param password password for validation
     * @return true valid password, false invalid password
     */
    public static boolean validate(final String password){
        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);
        return matcher.matches();

    }
}
