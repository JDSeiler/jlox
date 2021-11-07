package com.craftinginterpreters.lox;

/*
* Individual characters can br grouped into *lexemes*
* Lexemes are groups of characters that mean something in our program.
*
* At the point we recognize the lexeme, we categorize it.
* The categorized version is called a *token*
*
* We want to use tokens because strings are slow and abstracting lexemes
* into tokens decouples the Scanner from the rest of the code!
*
* */

public enum TokenType {
    // Single-character tokens.
    LEFT_PAREN, RIGHT_PAREN, LEFT_BRACE, RIGHT_BRACE,
    COMMA, DOT, MINUS, PLUS, SEMICOLON, SLASH, STAR,

    // 1 or 2 characters
    BANG, BANG_EQUAL,
    EQUAL, EQUAL_EQUAL,
    GREATER, GREATER_EQUAL,
    LESS, LESS_EQUAL,

    // Literals
    IDENTIFIER, STRING, NUMBER,

    // Keywords
    AND, CLASS, ELSE, FALSE, FUN, FOR, IF, NIL, OR,
    PRINT, RETURN, SUPER, THIS, TRUE, VAR, WHILE,

    EOF
}
