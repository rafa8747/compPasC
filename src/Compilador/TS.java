
package Compilador;

import java.util.HashMap;

/**
 * Classe que possui a criação de um HashMap para a Tabela de Simbolos.
 * @author Raphael Dos Santos e Diego Victor
 */
public class TS {
    private HashMap<Token, Identificador> tabelaSimbolos; // Tabela de símbolos do ambiente

    public TS() {
        tabelaSimbolos = new HashMap();

        // Inserindo as palavras reservadas
        Token word;
        word = new Token(Tag.KW_PROGRAM, "program", 0, 0);
        this.tabelaSimbolos.put(word, new Identificador());
        
        word = new Token(Tag.KW_IF, "if", 0, 0);
        this.tabelaSimbolos.put(word, new Identificador());
        
        word = new Token(Tag.KW_ELSE, "else", 0, 0);
        this.tabelaSimbolos.put(word, new Identificador());
        
        word = new Token(Tag.KW_WHILE, "while", 0, 0);
        this.tabelaSimbolos.put(word, new Identificador());
        
        word = new Token(Tag.KW_WRITE, "write", 0, 0);
        this.tabelaSimbolos.put(word, new Identificador());
        
        word = new Token(Tag.KW_READ, "read", 0, 0);
        this.tabelaSimbolos.put(word, new Identificador());
        
        word = new Token(Tag.KW_NUM, "num", 0, 0);
        this.tabelaSimbolos.put(word, new Identificador());
        
        word = new Token(Tag.KW_CHAR, "char", 0, 0);
        this.tabelaSimbolos.put(word, new Identificador());
        
        word = new Token(Tag.KW_NOT, "not", 0, 0);
        this.tabelaSimbolos.put(word, new Identificador());
        
        word = new Token(Tag.KW_OR, "or", 0, 0);
        this.tabelaSimbolos.put(word, new Identificador());
        
        word = new Token(Tag.KW_AND, "and", 0, 0);
        this.tabelaSimbolos.put(word, new Identificador());
    }
    
    /**
     * Método utilizado para colocar dados dentro da Tabela de Símbolos.
     * @param w - Token, ou palavra
     * @param i - Identificador
     */
    public void put(Token w, Identificador i) {
        tabelaSimbolos.put(w, i);
    }

    /**
     * Retorna um identificador de um determinado token.
     * @param w - Identificador
     * @return 
     */
    public Identificador getIdentificador(Token w) {
        Identificador infoIdentificador = (Identificador) tabelaSimbolos.get(w);
        return infoIdentificador;
    }

    /**
     * Pesquisa na tabela de símbolos se há algum tokem com determinado lexema.
     * Método utilizado para diferenciar ID e KW
     * @param lexema - Lexema do Token
     * @param linha - Linha do Lexema
     * @param coluna - Coluna do Lexema
     * @return 
     */
    public Token retornaToken(String lexema, int linha, int coluna) {
        for (Token token : tabelaSimbolos.keySet()) {
            if (token.getLexema().equals(lexema)) {
                return token;
            }
        } 
        
        Token ID; 
        ID = new Token(Tag.ID, lexema , linha, coluna);
        this.tabelaSimbolos.put(ID, new Identificador());
        return ID;
    }
    
    /**
     * Método que imprime as palavras da Tabela de Símbolos
     * @return 
     */
    @Override
    public String toString() {
        String saida = "";
        int i = 1;
        for (Token token : tabelaSimbolos.keySet()) {
            saida += ("Posição " + i + ": \t" + token.toString()) + "\n";
            i++;
        }
        return saida;
    }
}
