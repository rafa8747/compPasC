
package Compilador;

/**
 * Classe que contêm as TAG's reservadas do programa, como fim de arquivo, operadores, símbolos, identificador, números, strings e palavra reservada.
 * @author Raphael Dos Santos e Diego Victor
 */
public enum Tag {
    //Fim de arquivo
    EOF,
    
    //Operadores
    OP_EQ,
    OP_NE,
    OP_GT,
    OP_LT,
    OP_GE,
    OP_LE,
    OP_AD,
    OP_MIN,
    OP_MUL,
    OP_DIV,
    OP_ASS,
   
    
    //Simbolos
    SMB_OBC,
    SMB_CBC,
    SMB_OPA,
    SMB_CPA,
    SMB_COM,
    SMB_SEM,
    
    //identificador
    ID,
    
    //Números
    CON_NUM,
    
    //Strings
    LIT,
    CON_CHAR,
    
    //Palavra reservada
    KW_ELSE,
    KW_READ,
    KW_NUM,
    KW_WRITE,
    KW_OR,
    KW_NOT,
    KW_PROGRAM,
    KW_IF,
    KW_WHILE,
    KW_AND,
    KW_CHAR;
}
