package com.craftinginterpreters.lox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.craftinginterpreters.lox.TokenType.*;



public class Scanner {
    private final String SOURCE;
    private final List<Token> TOKENS = new ArrayList<>();
    private int START_OF_LEXEME = 0;
    private int CURRENT_POS = 0;
    private int LINE = 1;

    private static final Map<String, TokenType> keywords;

    static {
        keywords = new HashMap<>();
        keywords.put("and",    AND);
        keywords.put("class",  CLASS);
        keywords.put("else",   ELSE);
        keywords.put("false",  FALSE);
        keywords.put("for",    FOR);
        keywords.put("fun",    FUN);
        keywords.put("if",     IF);
        keywords.put("nil",    NIL);
        keywords.put("or",     OR);
        keywords.put("print",  PRINT);
        keywords.put("return", RETURN);
        keywords.put("super",  SUPER);
        keywords.put("this",   THIS);
        keywords.put("true",   TRUE);
        keywords.put("var",    VAR);
        keywords.put("while",  WHILE);
    }

    Scanner(String source) {
        this.SOURCE = source;
    }

    List<Token> scanTokens() {
        while (!isAtEnd()) {
            START_OF_LEXEME = CURRENT_POS;
            scanToken();
        }

        TOKENS.add(new Token(EOF, "", null, LINE));
        return TOKENS;
    }

    private boolean isAtEnd() {
        return CURRENT_POS >= SOURCE.length();
    }

    private void scanToken() {
        char c = advance();
        switch (c) {
            case '(': addToken(LEFT_PAREN); break;
            case ')': addToken(RIGHT_PAREN); break;
            case '{': addToken(LEFT_BRACE); break;
            case '}': addToken(RIGHT_BRACE); break;
            case ',': addToken(COMMA); break;
            case '.': addToken(DOT); break;
            case '-': addToken(MINUS); break;
            case '+': addToken(PLUS); break;
            case ';': addToken(SEMICOLON); break;
            case '*': addToken(STAR); break;
            case '!':
                addToken(match('=') ? BANG_EQUAL : BANG);
                break;
            case '=':
                addToken(match('=') ? EQUAL_EQUAL : EQUAL);
                break;
            case '<':
                addToken(match('=') ? LESS_EQUAL : LESS);
                break;
            case '>':
                addToken(match('=') ? GREATER_EQUAL : GREATER);
                break;
            case '/':
                if (match('/')) {
                    while (peek() != '\n' && !isAtEnd()) { advance(); }
                } else {
                    addToken(SLASH);
                }
                break;
            case ' ':
            case '\r':
            case '\t':
                // Ignore whitespace.
                break;
            case '\n':
                LINE++;
                break;
            case '"': string(); break;
            default:
                if (isDigit(c)) {
                    number();
                } else if (isAlpha(c)) {
                    identifier();
                } else {
                    Lox.error(LINE, "Unexpected character1.");
                }
                break;
        }
    }

    private void identifier() {
        while (isAlphaNumeric(peek())) {
            advance();
        }

        String text = SOURCE.substring(START_OF_LEXEME, CURRENT_POS);
        TokenType type = keywords.get(text);
        if (type == null) {
            type = IDENTIFIER;
        }
        addToken(type);
    }

    private void number() {
        while (isDigit(peek())) {
            advance();
        }

        if (peek() == '.' && isDigit(peekNext())) {
            advance();

            while (isDigit((peek()))) {
                advance();
            }
        }

        addToken(NUMBER, Double.parseDouble(SOURCE.substring(START_OF_LEXEME, CURRENT_POS)));
    }

    private void string() {
        while (peek() != '"' && !isAtEnd()) {
            // Supports multi-line strings.
            if (peek() == '\n') {
                LINE++;
            }
            advance();
        }

        if (isAtEnd()) {
            Lox.error(LINE, "Unterminated string literal.");
            return;
        }

        // Implicitly we've encountered the closing quotation mark.
        advance();

        String value = SOURCE.substring(START_OF_LEXEME + 1, CURRENT_POS - 1);
        addToken(STRING, value);
    }

    /**
     * Conditional version of advance, only consumes if the character after
     * the current character matches expected.
     * @param expected the query character
     * @return whether or not the character after CURRENT_POS matches or not
     */
    private boolean match(char expected) {
        if (isAtEnd()) {
            return false;
        }

        if (SOURCE.charAt(CURRENT_POS) != expected) {
           return false;
        }

        CURRENT_POS++;
        return true;
    }

    /**
     * Returns the current character without consuming it
     * @return the current character in the source, or \0
     */
    private char peek() {
        if (isAtEnd()) {
            return '\0';
        }
        return SOURCE.charAt(CURRENT_POS);
    }

    private char peekNext() {
        if (CURRENT_POS + 1 >= SOURCE.length()) {
            return '\0';
        }

        return SOURCE.charAt(CURRENT_POS + 1);
    }

    private boolean isAlphaNumeric(char c) {
        return isAlpha(c) || isDigit(c);
    }

    private boolean isAlpha(char c) {
        return (c >= 'a' && c <= 'z') ||
                (c >= 'A' && c <= 'Z') ||
                c == '_';
    }

    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    private char advance() {
        return SOURCE.charAt(CURRENT_POS++);
    }

    private void addToken(TokenType type) {
        addToken(type, null);
    }

    private void addToken(TokenType type, Object literal) {
        String text = SOURCE.substring(START_OF_LEXEME, CURRENT_POS);
        TOKENS.add(new Token(type, text, literal, LINE));
    }
}
