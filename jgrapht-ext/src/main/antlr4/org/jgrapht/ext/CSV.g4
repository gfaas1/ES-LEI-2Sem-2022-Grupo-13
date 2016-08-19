grammar CSV;

@lexer::members
{
    char sep = ',';

    public void setSep(char sep)
    {
        this.sep = sep;
    }

    private char getSep()
    {
        return sep;
    }
}

file: header record+ ;

header : record ;

record : field (SEPARATOR field)* '\r'? '\n' ;

field
    : TEXT     #TextField
    | STRING   #StringField
    |          #EmptyField
    ;
    
SEPARATOR: { _input.LA(1) == sep }? . ;
    
TEXT   : TEXTCHAR+ ;

fragment TEXTCHAR: { (_input.LA(1) != sep && _input.LA(1) != '\n' && _input.LA(1) != '\r' && _input.LA(1) != '"') }? .; 

STRING : '"' ('""'|~'"')* '"' ;
