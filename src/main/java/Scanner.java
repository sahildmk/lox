
import java.util.ArrayList;
import java.util.List;

public class Scanner {

    private final String source;
    private final List<Token> tokens = new ArrayList<>();

    // Start character position of lexeme
    private int start = 0;

    // Current character position in lexeme
    private int current = 0;

    // Source line number for lexeme
    private int line = 1;

    Scanner(String source) {
        this.source = source;
    }

    List<Token> scanTokens() {
        while (!isAtEnd()) {
            start = current;
            scanToken();
        }

        tokens.add(new Token(TokenType.EOF, "", null, line));
        return tokens;
    }

    private boolean isAtEnd() {
        return current >= source.length();
    }

    private void scanToken() {
        char c = advance();

        switch (c) {
            case '(' ->
                addToken(TokenType.LEFT_PAREN);
            case ')' ->
                addToken(TokenType.RIGHT_PAREN);
            case '{' ->
                addToken(TokenType.LEFT_BRACE);
            case '}' ->
                addToken(TokenType.RIGHT_BRACE);
            case ',' ->
                addToken(TokenType.COMMA);
            case '.' ->
                addToken(TokenType.DOT);
            case '-' ->
                addToken(TokenType.MINUS);
            case '+' ->
                addToken(TokenType.PLUS);
            case ';' ->
                addToken(TokenType.SEMICOLON);
            case '*' ->
                addToken(TokenType.STAR);
            case '!' ->
                addToken(match('=') ? TokenType.BANG_EQUAL : TokenType.BANG);
            case '=' ->
                addToken(match('=') ? TokenType.EQUAL_EQUAL : TokenType.EQUAL);
            case '<' ->
                addToken(match('=') ? TokenType.LESS_EQUAL : TokenType.LESS);
            case '>' ->
                addToken(match('=') ? TokenType.GREATER_EQUAL : TokenType.GREATER);
            case '/' -> {
                if (match('/')) {
                    // Comment continues till the end of the line
                    while (isPeekingChar('\n')) {
                        advance();
                    }
                } else {
                    addToken(TokenType.SLASH);
                }
            }
            case ' ', '\r', '\t' -> {
            }
            case '\n' ->
                line++;
            case '"' ->
                string();
            default ->
                Lox.error(line, "Unexpected character.");
        }
        // Ignore whitespace.
    }

    // Returns next character
    private char advance() {
        return source.charAt(current++);
    }

    private void addToken(TokenType type) {
        addToken(type, null);
    }

    private void addToken(TokenType type, Object literal) {
        String text = source.substring(start, current);
        tokens.add(new Token(type, text, literal, line));
    }

    private boolean match(char expected) {
        if (isAtEnd()) {
            return false;
        }
        if (source.charAt(current) != expected) {
            return false;
        }

        current++;
        return true;
    }

    private char peek() {
        if (isAtEnd()) {
            return '\0';
        }

        return source.charAt(current);
    }

    private boolean isPeekingChar(char c) {
        return peek() != c && !isAtEnd();
    }

    private void string() {
        while (isPeekingChar('"')) {
            if (peek() == '\n') {
                line++;
            }

            advance();
        }

        if (isAtEnd()) {
            Lox.error(line, "Unterminated string.");
            return;
        }

        // Reach the closing "
        advance();

        String value = source.substring(start + 1, current - 1);
        addToken(TokenType.STRING, value);
    }
}
