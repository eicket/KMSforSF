package crypto;

import java.security.SecureRandom;

public class StrongPasswordGenerator
{
    private static final String UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
    private static final String DIGITS = "0123456789";
    // private static final String SPECIAL_CHARS = "!@#$%^&*()-_=+<>?";
    private static final String ALL_CHARS = UPPERCASE + LOWERCASE + DIGITS /*+ SPECIAL_CHARS */;

    private static final SecureRandom random = new SecureRandom();

    public static String generatePassword(int length)
    {
        if (length < 10)
        {
            throw new IllegalArgumentException("Password length should be at least 10 characters.");
        }

        StringBuilder password = new StringBuilder(length);

        // Ensure at least one character from each group is included
        password.append(UPPERCASE.charAt(random.nextInt(UPPERCASE.length())));
        password.append(LOWERCASE.charAt(random.nextInt(LOWERCASE.length())));
        password.append(DIGITS.charAt(random.nextInt(DIGITS.length())));
        // password.append(SPECIAL_CHARS.charAt(random.nextInt(SPECIAL_CHARS.length())));

        // Fill the rest of the password with random characters
        for (int i = 4; i < length; i++)
        {
            password.append(ALL_CHARS.charAt(random.nextInt(ALL_CHARS.length())));
        }

        // Shuffle the password to avoid predictable patterns
        return shuffleString(password.toString());
    }

    // Method to shuffle the characters in the string
    private static String shuffleString(String input)
    {
        char[] chars = input.toCharArray();
        for (int i = chars.length - 1; i > 0; i--)
        {
            int j = random.nextInt(i + 1);
            char temp = chars[i];
            chars[i] = chars[j];
            chars[j] = temp;
        }
        return new String(chars);
    }
}
