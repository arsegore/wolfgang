package wolfgang.utils;

import at.favre.lib.crypto.bcrypt.BCrypt;

public class PasswordUtils {

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
}
