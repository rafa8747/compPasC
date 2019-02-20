
package Compilador;

    /**
     * Função Main, onde aponta o nome do arquivo a ser lido e inicia o lexer e o parser.
     * 
     * @author Raphael Dos Santos e Diego Victor Dias Laurindo
     */
    public class Compiladores {
    
        /**
         * Método Principal
         * @param args 
         */
        public static void main(String[] args) {
        
        Lexer lexer = new Lexer("TesteCerto1.txt");
        Parser parser = new Parser(lexer);

       /**
        * primeiro procedimento do Javinha
        */
       parser.Programa();

        /**
         * Procediemento para fechar o arquivo que está sendo lido.
         */
        parser.fechaArquivos();
         
        /**
         * Imprimir a tabela de simbolos
         */
        lexer.printTS();

        System.out.println("Compilação de Programa Realizada!");
    }
    
}
