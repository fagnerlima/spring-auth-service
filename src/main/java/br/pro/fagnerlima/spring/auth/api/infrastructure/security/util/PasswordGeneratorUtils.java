package br.pro.fagnerlima.spring.auth.api.infrastructure.security.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import br.pro.fagnerlima.spring.auth.api.application.service.exception.InvalidPasswordException;

public class PasswordGeneratorUtils {

    public static final String PASSWORD_PATTERN = "(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,30}";

    private static Pattern pattern;
    private static Matcher matcher;

    private static PasswordEncoder encoder = new BCryptPasswordEncoder();

    public static void main(String[] args) {
        System.out.println(PasswordGeneratorUtils.encode("admin"));
    }

    public static String encode(String password) {
        if (!isValid(password)) {
            throw new InvalidPasswordException();
        }

        return encoder.encode(password);
    }

    public static Boolean validate(String rawPassword, String encodedPassword) {
        return encoder.matches(rawPassword, encodedPassword);
    }
    
    public static boolean isValid(final String password) {
        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);

        return matcher.matches();
    }

}
