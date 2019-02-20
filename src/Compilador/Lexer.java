
package Compilador;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Classe que contêm os métodos responsáveis por abrir, ler e fechar os arquivos jvn, contêm também o método de busca de tokens.
 * Dentro da classe existe variáveis globais de contagem de linha, coluna, referência de arquivo, EOF(End of File), Tabela de Simbolos e uma variável que armazena o último caractere lido do arquivo.
 * 
 * @author Raphael Dos Santos e Diego Victor
 */
public class Lexer {
    /**
     * Constante para o fim de arquivo.
     */
    private static final int END_OF_FILE = -1;
    /**
     * Armazena o último caractere lido do arquivo.	
     */
    private static int lookahead = 0;
    /**
     * Variável global utilizada para contar as linhas no momento da leitura do arquivo.
     */
    public static int n_line = 1;
    /**
     * Variável global utilizada para contar as colunas no momento da leitura do arquivo.
     */
    public static int n_column = 1;
    /**
     *  Referência para o arquivo.
     */
    private RandomAccessFile instance_file;
    /**
     * Tabela de Símbolos.
     */
    private static TS tabelaSimbolos;
    
    /**
     * Método que tem a função de abrir a instance_file de input_data, ou seja, método de abertura de arquivo a ser lido.
     * RandomAccessFile recebe como parametro input_data e "r". O parametro "r" significa que deve ser feito apenas leitura do arquivo a ser aberto.
     * @param input_data 
     */
    public Lexer(String input_data) {
		
        // Abre instance_file de input_data
	try {
            instance_file = new RandomAccessFile(input_data, "r");
	}
	catch(IOException e) {
            System.out.println("Erro de abertura do arquivo " + input_data + "\n" + e);
            System.exit(1);
	}
	catch(Exception e) {
            System.out.println("Erro do programa ou falha da tabela de simbolos\n" + e);
            System.exit(2);
	}
         tabelaSimbolos = new TS(); // Tabela de Simbolos
    }
    
    public void printTS() {
      System.out.println("");
      System.out.println("--------Tabela de Simbolos--------");
      System.out.println(tabelaSimbolos.toString());  
      System.out.println();
   }
    
    /**
     * Método utilizado para fechar arquivo que esta ou estava sendo lindo no momento.
     */
    public void fechaArquivo() {

        try {
            instance_file.close();
        }
	catch (IOException errorFile) {
            System.out.println ("Erro ao fechar arquivo\n" + errorFile);
            System.exit(3);
	}
    }
   
     /**
      * Método utilizado para sinalizar erro.
      * @param mensagem - Mensagem recebida por parametro que contêm informaçôes sobre o erro lexico, como linha e coluna onde ele se encontra e qual é o erro.
      */
    public void sinalizaErro(String mensagem) {
        System.out.println("[Erro Lexico]: " + mensagem);
    }
    
    /**
     * Método utilizado para voltar uma posição do buffer de leitura.
     */
    public void retornaPonteiro(){

        try {
            // Não é necessário retornar o ponteiro em caso de Fim de Arquivo
            if (lookahead != END_OF_FILE) {
                instance_file.seek(instance_file.getFilePointer() - 1);
            }    
        }
        catch(IOException e) {
            System.out.println("Falha ao retornar a leitura\n" + e);
            System.exit(4);
        }
    }
   
    /**
     * Método utilizado para pegar o próximo Token.
     * Dentro do método possui um switch case onde verifica cada caractere e constrói uma StringBuilder para classificar o token, ou apontar um erro quando houver.
     * @return
     */
    public Token proxToken() {

	StringBuilder lexema = new StringBuilder();
	int estado = 1;
	char c;
	
        while(true) {
            c = '\u0000'; // null char
            
            // Avança caractere
            try {
                lookahead = instance_file.read(); 
		if(lookahead != END_OF_FILE) {
                    c = (char) lookahead;
                }
            }
            catch(IOException e) {
                System.out.println("Erro na leitura do arquivo");
                System.exit(3);
            }
            
            // Movimentação do automato
            switch(estado) {
                case 1:
                    if (lookahead == END_OF_FILE){
                        n_column = n_column + 1;
                        return new Token(Tag.EOF, "EOF", n_line, n_column);
                    }    
                    else if (c == ' ' || c == '\t' || c == '\n' || c == '\r') {
                        // Permance no estado = 1
                        if (c == ' '){
                            n_column = n_column + 1;
                        }
                        
                        if(c == '\n')  {
                            n_line = n_line + 1;
                            n_column = 1;
                        }
                        else if(c == '\t') {
                            n_column = n_column + 3;
                        }
                    }
                     else if (Character.isLetter(c)){
                        lexema.append(c);
                        n_column = n_column + 1;
                        estado = 14;
                    }
                    else if (Character.isDigit(c)) {
                        lexema.append(c);
                        n_column = n_column + 1;
                        estado = 12;
                    }
                    
                    else if (c == '<') {
                        estado = 9;
                    }
                    else if (c == '>') {
                        estado = 6;
                    }
                    else if (c == '=') {
                        estado = 2;
                    }
                    else if (c == '!') {
                        n_column = n_column + 1;
                        estado = 4;
                    }
                    else if (c == '"'){
                        n_column = n_column + 1;
                        estado = 16;
                    }
                    else if (c == '\'' ){
                        n_column = n_column + 1;
                        estado = 18;
                    }
                
                    else if (c == '/') {
                        estado = 21;
                        n_column = n_column + 1;
                    }
                    else if (c == '*') {
                        n_column = n_column + 1;
                        return new Token(Tag.OP_MUL, "*", n_line, n_column);
                    }
                    else if(c == '+') {
                        n_column = n_column + 1;
                        return new Token(Tag.OP_AD, "+", n_line, n_column);
                    }
                    else if(c == '-') {
                        n_column = n_column + 1;
                        return new Token(Tag.OP_MIN, "-", n_line, n_column);
                    }
                    else if(c == ';') {
                        n_column = n_column + 1;
                        return new Token(Tag.SMB_SEM, ";", n_line, n_column);
                    }
                    else if(c == ',') {
                        n_column = n_column + 1;
                        return new Token(Tag.SMB_COM, ",", n_line, n_column);
                    }
                    else if(c == '(') {
                        n_column = n_column + 1;
                        return new Token(Tag.SMB_OPA, "(", n_line, n_column);
                    }
                    else if(c == ')') {
                        n_column = n_column + 1;
                        return new Token(Tag.SMB_CPA, ")", n_line, n_column);
                    }
                    else if(c == '{') {
                        n_column = n_column + 1;
                        return new Token(Tag.SMB_OBC, "{", n_line, n_column);
                    }
                    else if(c == '}') {
                        n_column = n_column + 1;
                        return new Token(Tag.SMB_CBC, "}", n_line, n_column);
                    }
                                        
                    else {
                        n_column = n_column + 1;
                        sinalizaErro("Caractere invalido " + c + " na linha " + n_line + " e coluna " + n_column);
                        return proxToken();
                    }
                    break;
                case 2:
                    if (c == '='){
                        n_column = n_column + 2;
                        return new Token(Tag.OP_EQ, "==", n_line, n_column);
                    }
                    else {
                        retornaPonteiro();
                        n_column = n_column + 1;
                        return new Token(Tag.OP_ASS, "=", n_line, n_column);
                    }
                case 4:
                    if (c == '='){ 
                        n_column = n_column + 1;
                        return new Token(Tag.OP_NE, "!=", n_line, n_column);
                    }
                    else if (c == ' ' || c == '\t' || c == '\n' || c == '\r') {
                        
                        if (c == ' '){
                            n_column = n_column + 1;
                            sinalizaErro("Token incompleto para o caractere ! na linha " + n_line + " e coluna " + n_column); 
                            estado = 28;
                        }
                        if(c == '\n')  {
                            n_line = n_line + 1;
                            n_column = 1;
                            sinalizaErro("Token incompleto para o caractere ! na linha " + n_line + " e coluna " + n_column);
                            estado = 28;
                            
                        }
                        else if(c == '\t') {
                            n_column = n_column + 3;
                            sinalizaErro("Token incompleto para o caractere ! na linha " + n_line + " e coluna " + n_column);
                            estado = 28;
                        }
                    }
                    else if (lookahead == END_OF_FILE) {
                        sinalizaErro("Token deve ser completo para o caractere ! antes do fim de arquivo na linha " + n_line + " e coluna " + n_column); 
			return proxToken();
                    }
                    else {  
                        n_column = n_column + 1;
                        sinalizaErro("Token incompleto para o caractere ! na linha " + n_line + " e coluna " + n_column);
                        estado = 28;
                    }
                    break;
                case 28:
                    if (c == '='){ 
                        n_column = n_column + 1;
                        return new Token(Tag.OP_NE, "!=", n_line, n_column);
                    }  else if (c == ' ' || c == '\t' || c == '\n' || c == '\r') {
                        
                        if (c == ' '){
                            n_column = n_column + 1;
                            sinalizaErro("Token incompleto para o caractere ! na linha " + n_line + " e coluna " + n_column); 
                        }
                        if(c == '\n')  {
                            n_line = n_line + 1;
                            n_column = 1;
                            sinalizaErro("Token incompleto para o caractere ! na linha " + n_line + " e coluna " + n_column); 
                            
                        }
                        else if(c == '\t') {
                            n_column = n_column + 3;
                            sinalizaErro("Token incompleto para o caractere ! na linha " + n_line + " e coluna " + n_column); 
                        }
                    }
                    else if (lookahead == END_OF_FILE) {
                        sinalizaErro("Token deve ser completo para o caractere ! antes do fim de arquivo na linha " + n_line + " e coluna " + n_column); 
			return proxToken();
                    }
                    else {  
                        n_column = n_column + 1;
                        sinalizaErro("Token incompleto para o caractere ! na linha " + n_line + " e coluna " + n_column); 
                    }    
                    break;
                case 6:
                    if (c == '='){
                        n_column = n_column + 2;
                        return new Token(Tag.OP_GE, ">=", n_line, n_column);
                    }
                    else {
                        retornaPonteiro();
                        n_column = n_column +1;
                        return new Token(Tag.OP_GT, ">", n_line, n_column);
                    }
                case 9:
                    if (c == '='){
                        n_column = n_column +2;
                        return new Token(Tag.OP_LE, "<=", n_line, n_column);
                    }
                    else {
                        retornaPonteiro();
                        n_column = n_column + 1;
                        return new Token(Tag.OP_LT, "<", n_line, n_column);
                    }
                case 12:
                    if (Character.isDigit(c)) {
                        lexema.append(c);
                        n_column = n_column +1;
                    }
                    else if (c == '.') {
                        lexema.append(c);
                        n_column = n_column + 1;
                        estado = 23;
                    }
                    else {
                        retornaPonteiro();
			return new Token(Tag.CON_NUM, lexema.toString(), n_line, n_column);
                    }
                    break;
                case 14:
                    if (Character.isLetterOrDigit(c) || c == '_') {
                        lexema.append(c);
                        n_column = n_column + 1;
			// Permanece no estado 14
                    }
                    else { 
			retornaPonteiro();
                        Token token = tabelaSimbolos.retornaToken(lexema.toString(), n_line, n_column);
                        
                        if (token == null) {
                            return new Token(Tag.ID, lexema.toString(), n_line, n_column);
                        }
                        return token; 
                    }
                    break;
                case 16:
                    if (c == '"' && !lexema.toString().isEmpty()) {
                        n_column = n_column + 1;
                        return new Token(Tag.LIT, lexema.toString().replace("\r\n", ""), n_line, n_column);
                    }
                    if (c == '"' && lexema.toString().isEmpty()) {
                        n_column = n_column + 1;
                        sinalizaErro("LIT não pode ser vazio - Linha: " + n_line + "  Coluna: " + n_column);
                        return proxToken();
                    } else if (c == ' ' || c == '\t' || c == '\n' || c == '\r') {
                        
                        if (c == ' '){
                            n_column = n_column + 1;
                            lexema.append(c);
                        }
                        if(c == '\n')  {
                            n_line = n_line + 1;
                            n_column = 1;
                            sinalizaErro("LIT não pode contêr QUEBRA DE LINHA - Linha: " + n_line + "  Coluna: " + n_column);
                            estado = 27;
                        }
                        else if(c == '\t') {
                            lexema.append(c);
                            n_column = n_column + 3;
                        }
                    }
                    else if (lookahead == END_OF_FILE) {
                        sinalizaErro("LIT deve ser fechada com \" antes do fim de arquivo na linha " + n_line + " e coluna " + n_column);
			return proxToken();
                    }
                    else { 
                        lexema.append(c);
                        n_column = n_column + 1;
                    }
                    break;
                case 27:
                    if (c == '"' && !lexema.toString().isEmpty()) {
                        n_column = n_column + 1;
                        return new Token(Tag.LIT, lexema.toString().replace("\r\n", ""), n_line, n_column);
                    }
                    if (c == '"' && lexema.toString().isEmpty()) {
                        n_column = n_column + 1;
                        sinalizaErro("LIT não pode ser vazio - Linha: " + n_line + "  Coluna: " + n_column);
                        return proxToken();
                    } else {
                        
                        if (c == ' ' || c == '\t' || c == '\n' || c == '\r') {
                        
                        if (c == ' '){
                            n_column = n_column + 1;
                            sinalizaErro("LIT não pode contêr QUEBRA DE LINHA - Linha: " + n_line + "  Coluna: " + n_column);
                        }   
                        if(c == '\n')  {
                            n_line = n_line + 1;
                            n_column = 1;
                            sinalizaErro("LIT não pode contêr QUEBRA DE LINHA - Linha: " + n_line + "  Coluna: " + n_column);
                            }
                        else if(c == '\t') {
                            n_column = n_column + 3;
                            sinalizaErro("LIT não pode contêr QUEBRA DE LINHA - Linha: " + n_line + "  Coluna: " + n_column);
                        }
                    }
                    else if (lookahead == END_OF_FILE) {
                        sinalizaErro("LIT deve ser fechada com \" antes do fim de arquivo na linha " + n_line + " e coluna " + n_column);
			return proxToken();
                    }
                    else {
                        n_column = n_column + 1;
                        sinalizaErro("LIT não pode contêr QUEBRA DE LINHA - Linha: " + n_line + "  Coluna: " + n_column);
                    }   
                }
                    break; 
                case 18:
                    if (c == '\'' && lexema.toString().isEmpty()) {
                        n_column = n_column + 1;
                        sinalizaErro("CON_CHAR não pode ser vazio - Linha: " + n_line + "  Coluna: " + n_column);
                        return proxToken();
                    }
                    else if (c == ' ' || c == '\t' || c == '\n' || c == '\r') {                       
                        if (c == ' '){
                            n_column = n_column + 1;
                            lexema.append(c);
                            estado = 19;
                        }
                        if (c == '\n'){
                            n_line = n_line + 1;
                            n_column = 1;
                            lexema.append(' ');
                            estado = 19;
                        }
                        if(c == '\r'){
                         //   n_column = n_column + 1;
                         //   lexema.append(c);
                         //   estado = 19;
                        }
                        else if(c == '\t'){
                            n_column = n_column + 3;
                            lexema.append(c);
                            estado = 19;
                        }
                    }
                    else if (lookahead == END_OF_FILE){
                        sinalizaErro("CON_CHAR deve ser fechada com \' antes do fim de arquivo na linha " + n_line + " e coluna " + n_column);
			return proxToken();
                    }
                    else { 
                        lexema.append(c);
                        n_column = n_column + 1;
                        estado = 19;
                    }
                    break;
                case 19:
                    if (c == '\''){
                       n_column = n_column +1;
                       return new Token(Tag.CON_CHAR, lexema.toString() , n_line, n_column);
                    } 
                    else {
                        if (c == ' ' || c == '\t' || c == '\n' || c == '\r') {
                       
                        if (c == ' '){
                            n_column = n_column + 1;
                            sinalizaErro("Token incompleto para o caractere ' na linha " + n_line + " e coluna " + n_column);                             
                        }
                        if (c == '\n'){
                            n_line = n_line + 1;
                            n_column = 1;
                            sinalizaErro("Token incompleto para o caractere ' na linha " + n_line + " e coluna " + n_column);
                            
                        }
                        else if (c == '\t'){
                            n_column = n_column + 3;
                            sinalizaErro("Token incompleto para o caractere ' na linha " + n_line + " e coluna " + n_column);                       
                        }
                    }
                    else if (lookahead == END_OF_FILE){
                        sinalizaErro("CON_CHAR deve ser fechada com \' antes do fim de arquivo na linha " + n_line + " e coluna " + n_column);
			return proxToken();
                    }
                    else{
                        n_column = n_column + 1;
                        sinalizaErro("Token incompleto para o caractere ' na linha " + n_line + " e coluna " + n_column);  
                    }
                }
                    break;
                case 21:
                    if (c == '/') 
		    {
                        n_column = n_column + 1;
                        estado = 22;
                    }
                    else if (c == '*'){
                        n_column = n_column + 1;
                        estado = 25;
                    }
                    else {
                        retornaPonteiro();
			return new Token(Tag.OP_DIV, "/", n_line, n_column);
                    }
                    break;
                case 22:
                    if (c == '\n') {
                        n_line = n_line + 1;
                        n_column = 1;
                        estado = 1;
                    }  
                    else if (lookahead == END_OF_FILE) 
                    { 
                       return proxToken();
                    }
                    break;
                case 23:
                    if (Character.isDigit(c)) {
                        lexema.append(c);
                        n_column = n_column + 1;
                        estado = 24;
                    }
                    else {
                        if (c == ' ' || c == '\t' || c == '\n' || c == '\r') {
                       
                        if (c == ' '){
                            n_column = n_column + 1;
                            sinalizaErro("Padrao para CON_NUM invalido na linha " + n_line + " coluna " + n_column);
                        }
                        if(c == '\n'){
                            n_line = n_line + 1;
                            n_column = 1;
                            sinalizaErro("Padrao para CON_NUM invalido na linha " + n_line + " coluna " + n_column);
                        }
                        else if(c == '\t'){
                            n_column = n_column + 3;
                            sinalizaErro("Padrao para CON_NUM invalido na linha " + n_line + " coluna " + n_column);
                        }
                    }
                    else{
                        n_column = n_column + 1;
                        sinalizaErro("Padrao para CON_NUM invalido na linha " + n_line + " coluna " + n_column);
                        }
                    }
                    break;
                case 24:
                    if (Character.isDigit(c)) {
                        lexema.append(c);
                        n_column = n_column + 1;
                    }
                    else {
                        retornaPonteiro();
			return new Token(Tag.CON_NUM, lexema.toString(), n_line, n_column);
                    }
                    break;
                case 25:
                    if (c == ' ' || c == '\t' || c == '\n' || c == '\r') {
                       
                        if (c == ' '){
                            n_column = n_column + 1;
                        }
                        
                        if(c == '\n')  {
                            n_line = n_line + 1;
                            n_column = 1;
                        }
                        else if(c == '\t') {
                            n_column = n_column + 3;
                        }
                    } 
                    else if (c == '*'){
                        n_column = n_column +1;
                        estado = 26;
                    }
                    else if (lookahead == END_OF_FILE) {
                        sinalizaErro("Comentário deve ser fechada com */ antes do fim de arquivo na linha " + n_line + " e coluna " + n_column);
			return proxToken();
                    }
                    else {
                       n_column = n_column + 1;
                    } break;
                case 26:
                    if (c == '/'){
                        n_column = n_column + 1;
                        estado = 1;
                    }
                    else if (c == '*'){
                       n_column = n_column +1;
                       estado = 26;
                    }
                    else {
                        n_column = n_column + 1;
                        estado = 25;
                    } 
                    break;
            }  // Fim Switch
        }  // Fim While
    }  // Fim proxToken
}
