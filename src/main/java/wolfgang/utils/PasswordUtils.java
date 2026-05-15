package wolfgang.utils;

import at.favre.lib.crypto.bcrypt.BCrypt;

import java.util.regex.Pattern;

public class PasswordUtils {
    private static final String PASSWORD_REGEX = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
    private static final Pattern PATTERN = Pattern.compile(PASSWORD_REGEX);

    /**
     * @param password
     * @return le mot de passe haché
     */
    public static String hashPassword(String password) {
        return BCrypt.withDefaults().hashToString(12, password.toCharArray());
    }

    /**
     * Compare un mot de passe entré au hash stocké dans la bdd
     * @param password
     * @param hash
     * @return vrai si le mot de passe est correct, faux sinon
     */
    public static boolean verifyPassword(String password, String hash) {
        BCrypt.Result result = BCrypt.verifyer().verify(password.toCharArray(), hash);

       return result.verified;
    }

    /***
     * verifie si le mdp est bien au bon format
     * @param password
     * @return vrai si le mdp correspond au format
     */
    public static boolean isValidPassword(String password){
        if (password == null) return false;
        return PATTERN.matcher(password).matches();
    }
}
