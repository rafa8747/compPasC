
package Compilador;

/**
 * Classe que contêm os métodos de manipulação de Token e seu construtor, como getters e setters.
 * @author Raphael Dos Santos e Diego Victor
 */
public class Token {    
    private String lexema;
    private Tag classe;
    private int linha;
    private int coluna;
    
    /**
     * Método construtor de Token.
     * @param classe - O tipo desse Token, se ele é uma KW, ID e entre outros.
     * @param lexema - O "nome" deste token, forma de identificar ele.
     * @param linha - Linha que ele se encontra
     * @param coluna - Coluna que ele se encontra
     */
    public Token(Tag classe, String lexema, int linha, int coluna) {

        this.classe = classe;
        this.lexema = lexema;
        this.linha = linha;
        this.coluna = coluna;
    }
	
    /**
     * Método que pega a classe da Tag.
     * @return 
     */
    public Tag getClasse() {	
	return classe;
    }
    
    /**
     * Método que altera a classe da Tag.
     * @param classe 
     */
    public void setClasse(Tag classe) {
	this.classe = classe;
    }
    
    /**
     * Método que pega o lexema do Token
     * @return 
     */
    public String getLexema() {
	return lexema;
    }
	
    /**
     * Método que altera o lexema do Token.
     * @param lexema 
     */
    public void setLexema(String lexema) {
		
	this.lexema = lexema;
    }
    
    /**
     * Método que pega a linha do token
     * @return 
     */
    public int getLinha() {
        return linha;
    }
    
    /**
     * Método que altera a linha do token
     * @param linha 
     */
    public void setLinha(int linha) {
        this.linha = linha;
    }
    
    /**
     * Método que pega a coluna do token
     * @return 
     */
    public int getColuna() {
        return coluna;
    }

    /**
     * Método que altera a coluna do token
     * @param coluna 
     */
    public void setColuna(int coluna) {
        this.coluna = coluna;
    }
    
    /**
     * Método que exibe a classe e o lexema de um determinado token.
     * @return 
     */
    @Override
    public String toString() {
        return "<" + classe + ", \"" + lexema + "\">";
    }
}
