package Compilador;

/**
 * Classe que contêm os métodos responsáveis por fechar os arquivos de leitura,
 * contêm também o método de leitura de tokens que é recebido do Lexer, método
 * que apontam erros, método de avançar entrada (token), método de consumir
 * entrada (token) e todos os métodos de tomada de decisão que o compilador deve
 * escolher para identificar as entradas e os erros no analisador sintático.
 *
 * @author Raphael Dos Santos e Diego Victor
 */
public class Parser {

    /**
     * Variável do tipo lexer que recebe os tokens do analisador léxico.
     */
    private final Lexer lexer;

    /**
     * Variável token que recebe os tokens passados do analisador léxico.
     */
    private Token token;

    /**
     * Método onde é ralizado a leitura dos tokens do lexer, tornando
     * obrigatoria a leitura inicial do primeiro símbolo.
     *
     * @param lexer Variável do tipo lexer.
     */
    public Parser(Lexer lexer) {
        this.lexer = lexer;
        token = lexer.proxToken(); // Leitura inicial obrigatoria do primeiro simbolo
        System.out.println("[DEBUG]" + token.toString());
    }

    /**
     * Fecha os arquivos de entrada e de tokens.
     */
    public void fechaArquivos() {
        lexer.fechaArquivo();
    }

    /**
     * Método onde aponta o erro do analisador sintático, mostrando a linha e a
     * coluna na onde o erro se encontra.
     *
     * @param mensagem Mensagem que mostra o que era esperado e o que foi encontrado.
     */
    public void erroSintatico(String mensagem) {

        System.out.println("[Erro Sintatico] na linha " + token.getLinha() + " e coluna " + token.getColuna());
        System.out.println(mensagem + "\n");
    }

    /**
     * Método que avança entrada, ou seja, faz a chamada do próximo token.
     */
    public void advance() {
        token = lexer.proxToken();
        System.out.println("[DEBUG]" + token.toString());
    }

    /**
     * Verifica token esperado t.
     *
     * @param t token
     * @return
     */
    public boolean eat(Tag t) {
        if (token.getClasse() == t) {
            advance();
            return true;
        } else {
            return false;
        }
    }

    /**
     * P → “program” “id” body
     */
    public void Programa() {

        if (token.getClasse() == Tag.KW_PROGRAM) { // espera program

            if (!eat(Tag.KW_PROGRAM)) {
                erroSintatico("Esperado \"program\", encontrado " + token.getLexema());
                System.exit(1);
            }

            if (!eat(Tag.ID)) { // Espera ID
                erroSintatico("Esperado um \"ID\", encontrado " + token.getLexema());
                System.exit(1);
            }

            body();
        } else {
            erroSintatico("Esperado \"program\", encontrado " + token.getLexema());
            System.exit(1);
        }
    }

    /**
     * body → decl-list "{" stmt-list "}"
     */
    public void body() {

        if (token.getClasse() == Tag.KW_CHAR || token.getClasse() == Tag.KW_NUM || token.getClasse() == Tag.SMB_OBC) {

            declList();

            if (!eat(Tag.SMB_OBC)) { // Espera "{"
                erroSintatico("Esperado \"{\", encontrado " + token.getLexema());
                System.exit(1);
            }

            stmtList();

            if (!eat(Tag.SMB_CBC)) { // Espera "}"
                erroSintatico("Esperado \"}\", encontrado " + token.getLexema());
                System.exit(1);
            }
        } else {
            erroSintatico("Esperado \"num, char, {\", encontrado " + token.getLexema());
            System.exit(1);
        }
    }

    /**
     * decl-list → decl ";" decl-list | ε
     */
    public void declList() {

        if (token.getClasse() == Tag.KW_CHAR || token.getClasse() == Tag.KW_NUM) {

            decl();

            if (!eat(Tag.SMB_SEM)) { // Espera ";"
                erroSintatico("Esperado \";\", encontrado " + token.getLexema());
                System.exit(1);
            }

            declList();

            // decl-list → ε
        } else if (token.getClasse() == Tag.SMB_OBC) {
            return;
        } else {
            erroSintatico("Esperado \"num, char, {\", encontrado " + token.getLexema());
            System.exit(1);
        }
    }

    /**
     * stmt-list → stmt ";" stmt-list | ε
     */
    public void stmtList() {

        if (token.getClasse() == Tag.ID || token.getClasse() == Tag.KW_IF || token.getClasse() == Tag.KW_WHILE || token.getClasse() == Tag.KW_READ || token.getClasse() == Tag.KW_WRITE) {

            stmt();

            if (!eat(Tag.SMB_SEM)) { // Espera ";"
                erroSintatico("Esperado \";\", encontrado " + token.getLexema());
                System.exit(1);
            }

            stmtList();

            // stmt-list → ε
        } else if (token.getClasse() == Tag.SMB_CBC) {
            return;
        } else {
            erroSintatico("Esperado \"id, if, while, read, write, }\", encontrado " + token.getLexema());
            System.exit(1);
        }
    }

    /**
     * decl → type id-list
     */
    public void decl() {

        if (token.getClasse() == Tag.KW_CHAR || token.getClasse() == Tag.KW_NUM) {

            type();
            idList();

        } else {
            erroSintatico("Esperado \"num, char\", encontrado " + token.getLexema());
            System.exit(1);
        }
    }

    /**
     * stmt → assign-stmt | if-stmt | while-stmt | read-stmt | write-stmt
     */
    public void stmt() {

        if (token.getClasse() == Tag.ID || token.getClasse() == Tag.KW_IF || token.getClasse() == Tag.KW_WHILE || token.getClasse() == Tag.KW_READ || token.getClasse() == Tag.KW_WRITE) {

            if (eat(Tag.ID)) {
                assignStmt();
            }

            if (eat(Tag.KW_IF)) {
                ifStmt();
            }

            if (eat(Tag.KW_WHILE)) {
                whileStmt();
            }

            if (eat(Tag.KW_READ)) {
                readStmt();
            }

            if (eat(Tag.KW_WRITE)) {
                writeStmt();
            }
        } else {
            erroSintatico("Esperado \"id, if, while, read, write\", encontrado " + token.getLexema());
            System.exit(1);
        }
    }

    /**
     * type → "num" | "char"
     */
    public void type() {

        if (!eat(Tag.KW_NUM) && !eat(Tag.KW_CHAR)) {
            erroSintatico("Esperado \"num, char\", encontrado " + token.getLexema());
            System.exit(1);
        }
    }

    /**
     * id-list → "id" id-list'
     */
    public void idList() {

        if (token.getClasse() == Tag.ID) {

            if (!eat(Tag.ID)) {
                erroSintatico("Esperado \"id\", encontrado " + token.getLexema());
                System.exit(1);
            }

            idListLinha();
        } else {
            erroSintatico("Esperado \"id\", encontrado " + token.getLexema());
            System.exit(1);
        }
    }

    /**
     * id-list' → "," id-list | ε
     */
    public void idListLinha() {

        if (token.getClasse() == Tag.SMB_COM) {

            if (!eat(Tag.SMB_COM)) {
                erroSintatico("Esperado \",\", encontrado " + token.getLexema());
                System.exit(1);
            }

            idList();

            // id-list' → ε
        } else if (token.getClasse() == Tag.SMB_SEM) {
            return;
        } else {
            erroSintatico("Esperado \", ou ;\", encontrado " + token.getLexema());
            System.exit(1);
        }
    }

    /**
     * assign-stmt → "id" "=" simple-expr
     */
    public void assignStmt() {

        if (!eat(Tag.OP_ASS)) {
            erroSintatico("Esperado \"=\", encontrado " + token.getLexema());
            System.exit(1);
        }

        simpleExpr();

    }

    /**
     * if-stmt → "if" "(" condition ")" "{" stmt-list "}" if-stmt'
     */
    public void ifStmt() {

        if (!eat(Tag.SMB_OPA)) {  // Espera "("
            erroSintatico("Esperado \"(\", encontrado " + token.getLexema());
            System.exit(1);
        }

        condition();

        if (!eat(Tag.SMB_CPA)) { // Espera ")"
            erroSintatico("Esperado \")\", encontrado " + token.getLexema());
            System.exit(1);
        }

        if (!eat(Tag.SMB_OBC)) { // Espera "{"
            erroSintatico("Esperado \"{\", encontrado " + token.getLexema());
            System.exit(1);
        }

        stmtList();

        if (!eat(Tag.SMB_CBC)) { // Espera "}"
            erroSintatico("Esperado \"}\", encontrado " + token.getLexema());
            System.exit(1);
        }

        ifStmtLinha();
    }

    /**
     * while-stmt → stmt-prefix "{" stmt-list "}"
     */
    public void whileStmt() {

        stmtPrefix();

        if (!eat(Tag.SMB_OBC)) { // Espera "{"
            erroSintatico("Esperado \"{\", encontrado " + token.getLexema());
            System.exit(1);
        }

        stmtList();

        if (!eat(Tag.SMB_CBC)) { // Espera "}"
            erroSintatico("Esperado \"}\", encontrado " + token.getLexema());
            System.exit(1);
        }
    }

    /**
     * stmt-prefix → “while” “(“ condition “)”
     */
    public void stmtPrefix() {

        if (!eat(Tag.SMB_OPA)) {
            erroSintatico("Esperado \"(\", encontrado " + token.getLexema());
            System.exit(1);
        }

        condition();

        if (!eat(Tag.SMB_CPA)) {
            erroSintatico("Esperado \")\", encontrado " + token.getLexema());
            System.exit(1);
        }
    }

    /**
     * read-stmt → "read" "id"
     */
    public void readStmt() {

        if (!eat(Tag.ID)) { // Espera "id"
            erroSintatico("Esperado \"id\", encontrado " + token.getLexema());
            System.exit(1);
        }
    }

    /**
     * write-stmt → "write" writable
     */
    public void writeStmt() {

        writable();
    }

    /**
     * simple-expr → term simple-expr'
     */
    public void simpleExpr() {

        if (token.getClasse() == Tag.KW_NOT || token.getClasse() == Tag.ID || token.getClasse() == Tag.SMB_OPA || token.getClasse() == Tag.CON_NUM || token.getClasse() == Tag.CON_CHAR) {

            term();
            simpleExprLinha();

        } else {
            erroSintatico("Esperado \"not, id, (, num_const, char_const\", encontrado " + token.getLexema());
            System.exit(1);
        }

    }

    /**
     * condition → expression
     */
    public void condition() {

        if (token.getClasse() == Tag.KW_NOT || token.getClasse() == Tag.ID || token.getClasse() == Tag.SMB_OPA || token.getClasse() == Tag.CON_NUM || token.getClasse() == Tag.CON_CHAR) {

            expression();

        } else {
            erroSintatico("Esperado \"not, id, (, num_const, char_const\", encontrado " + token.getLexema());
            System.exit(1);
        }

    }

    /**
     * if-stmt' → "else" "{" stmt-list "}" | ε
     */
    public void ifStmtLinha() {

        if (token.getClasse() == Tag.KW_ELSE) {

            if (!eat(Tag.KW_ELSE)) { // Espera "else"
                erroSintatico("Esperado \"else\", encontrado " + token.getLexema());
                System.exit(1);
            }

            if (!eat(Tag.SMB_OBC)) { // Espera "{"
                erroSintatico("Esperado \"{\", encontrado " + token.getLexema());
                System.exit(1);
            }

            stmtList();

            if (!eat(Tag.SMB_CBC)) { // Espera "}"
                erroSintatico("Esperado \"}\", encontrado " + token.getLexema());
                System.exit(1);
            }

            // if-stmt' → ε
        } else if (token.getClasse() == Tag.SMB_SEM) {
            return;
        } else {
            erroSintatico("Esperado \"else, ;\", encontrado " + token.getLexema());
            System.exit(1);
        }
    }

    /**
     * writable → simple-expr | "literal"
     */
    public void writable() {

        if (token.getClasse() == Tag.KW_NOT || token.getClasse() == Tag.ID || token.getClasse() == Tag.SMB_OPA || token.getClasse() == Tag.CON_NUM || token.getClasse() == Tag.CON_CHAR) {

            simpleExpr();

            // writable → "literal"
        } else if (token.getClasse() == Tag.LIT) {
            if (!eat(Tag.LIT)) {
                erroSintatico("Esperado \"literal\", encontrado " + token.getLexema());
                System.exit(1);
            }
        } else {
            erroSintatico("Esperado \"not, id, (, num_const, char_const, literal\", encontrado " + token.getLexema());
            System.exit(1);
        }
    }

    /**
     * term → factor-a term'
     */
    public void term() {

        if (token.getClasse() == Tag.KW_NOT || token.getClasse() == Tag.ID || token.getClasse() == Tag.SMB_OPA || token.getClasse() == Tag.CON_NUM || token.getClasse() == Tag.CON_CHAR) {

            factorA();

            termLinha();

        } else {
            erroSintatico("Esperado \"not, id, (, num_const, char_const\", encontrado " + token.getLexema());
            System.exit(1);
        }
    }

    /**
     * simple-expr' → addop term simple-expr' | ε
     */
    public void simpleExprLinha() {

        if (token.getClasse() == Tag.OP_AD || token.getClasse() == Tag.OP_MIN || token.getClasse() == Tag.KW_OR) {

            addop();
            term();
            simpleExprLinha();

            // simple-expr' → ε
        } else if (token.getClasse() == Tag.SMB_SEM || token.getClasse() == Tag.OP_EQ || token.getClasse() == Tag.OP_GT || token.getClasse() == Tag.OP_GE || token.getClasse() == Tag.OP_LT || token.getClasse() == Tag.OP_LE || token.getClasse() == Tag.OP_NE || token.getClasse() == Tag.SMB_CPA) {
            return;
        } else {
            erroSintatico("Esperado \"+, -, or, ;, ==, >, >=, <, <=, !=, )\", encontrado " + token.getLexema());
            System.exit(1);
        }
    }

    /**
     * factor-a → factor-a' factor
     */
    public void factorA() {

        if (token.getClasse() == Tag.KW_NOT || token.getClasse() == Tag.ID || token.getClasse() == Tag.SMB_OPA || token.getClasse() == Tag.CON_NUM || token.getClasse() == Tag.CON_CHAR) {

            factorALinha();
            factor();
        } else {
            erroSintatico("Esperado \"not, id, (, num_const, char_const\", encontrado " + token.getLexema());
            System.exit(1);
        }
    }

    /**
     * termLinha → mulop factor-a term' | ε
     */
    public void termLinha() {

        if (token.getClasse() == Tag.OP_MUL || token.getClasse() == Tag.OP_DIV || token.getClasse() == Tag.KW_AND) {

            mulop();
            factorA();
            termLinha();

            // termLinha → ε
        } else if (token.getClasse() == Tag.OP_AD || token.getClasse() == Tag.OP_MIN || token.getClasse() == Tag.KW_OR || token.getClasse() == Tag.SMB_SEM || token.getClasse() == Tag.OP_EQ || token.getClasse() == Tag.OP_GT || token.getClasse() == Tag.OP_GE || token.getClasse() == Tag.OP_LT || token.getClasse() == Tag.OP_LE || token.getClasse() == Tag.OP_NE || token.getClasse() == Tag.SMB_CPA) {
            return;
        } else {
            erroSintatico("Esperado \"*, /, and, +, -, or, ;, ==, >, >=, <, <=, !=, )\", encontrado " + token.getLexema());
            System.exit(1);
        }
    }

    /**
     * addop → "+" | "-" | "or"
     */
    public void addop() {

        if (!eat(Tag.OP_AD) && !eat(Tag.OP_MIN) && !eat(Tag.KW_OR)) {
            erroSintatico("Esperado \"+, -, or\", encontrado " + token.getLexema());
            System.exit(1);
        }
    }

    /**
     * factor-a' → "not" | ε
     */
    public void factorALinha() {

        if (token.getClasse() == Tag.KW_NOT) {

            if (!eat(Tag.KW_NOT)) {
                erroSintatico("Esperado \"not\", encontrado " + token.getLexema());
                System.exit(1);
            }
        } else if (token.getClasse() == Tag.ID || token.getClasse() == Tag.SMB_OPA || token.getClasse() == Tag.CON_NUM || token.getClasse() == Tag.CON_CHAR) {
            return;
        } else {
            erroSintatico("Esperado \"not, id, (, num_const, char_const\", encontrado " + token.getLexema());
            System.exit(1);
        }
    }

    /**
     * factor → "id" | constant | "(" expression ")"
     */
    public void factor() {

        if (token.getClasse() == Tag.ID) {

            if (!eat(Tag.ID)) {
                erroSintatico("Esperado \"id\", encontrado " + token.getLexema());
                System.exit(1);
            }
            // factor → constant
        } else if (token.getClasse() == Tag.CON_NUM || token.getClasse() == Tag.CON_CHAR) {

            constant();

            // factor → "(" expression ")"
        } else if (token.getClasse() == Tag.SMB_OPA) {
            if (!eat(Tag.SMB_OPA)) {
                erroSintatico("Esperado \"(\", encontrado " + token.getLexema());
                System.exit(1);
            }

            expression();

            if (!eat(Tag.SMB_CPA)) {
                erroSintatico("Esperado \")\", encontrado " + token.getLexema());
                System.exit(1);
            }
        } else {
            erroSintatico("Esperado \"id, num_const, char_const, (\", encontrado " + token.getLexema());
            System.exit(1);
        }
    }

    /**
     * mulop → “*” | “/” | “and”
     */
    public void mulop() {
        if (!eat(Tag.OP_MUL) && !eat(Tag.OP_DIV) && !eat(Tag.KW_AND)) {
            erroSintatico("Esperado \"*, /, and\", encontrado " + token.getLexema());
            System.exit(1);
        }
    }

    /**
     * constant → “num_const” | “char_const”
     */
    public void constant() {
        if (!eat(Tag.CON_NUM) && !eat(Tag.CON_CHAR)) {
            erroSintatico("Esperado \"num_const, char_const\", encontrado " + token.getLexema());
            System.exit(1);
        }
    }

    /**
     * expression → simple-expr expression'
     */
    public void expression() {

        if (token.getClasse() == Tag.KW_NOT || token.getClasse() == Tag.ID || token.getClasse() == Tag.SMB_OPA || token.getClasse() == Tag.CON_NUM || token.getClasse() == Tag.CON_CHAR) {

            simpleExpr();
            expressionLinha();

        } else {
            erroSintatico("Esperado \"not, id, (, num_const, char_const\", encontrado " + token.getLexema());
            System.exit(1);
        }
    }

    /**
     * expression' → relop simple-expr | ε
     */
    public void expressionLinha() {

        if (token.getClasse() == Tag.OP_EQ || token.getClasse() == Tag.OP_GT || token.getClasse() == Tag.OP_GE || token.getClasse() == Tag.OP_LT || token.getClasse() == Tag.OP_LE || token.getClasse() == Tag.OP_NE) {
            relop();
            simpleExpr();
        } else if (token.getClasse() == Tag.SMB_CPA) {
            return;
        } else {
            erroSintatico("Esperado \"==, >, >=, <, <=, !=, )\", encontrado " + token.getLexema());
            System.exit(1);
        }
    }

    /**
     * relop → (Todos os sinais, como os de atribuição, negação e comparação, não coloquei por que estava dando erro no javadoc)
     */
    public void relop() {
        if (!eat(Tag.OP_EQ) && !eat(Tag.OP_GT) && !eat(Tag.OP_GE) && !eat(Tag.OP_LT) && !eat(Tag.OP_LE) && !eat(Tag.OP_NE)) {
            erroSintatico("Esperado \"==, >, >=, <, <=, !=\", encontrado " + token.getLexema());
            System.exit(1);
        }
    }

} // Fim Parser
