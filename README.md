# Compiler Construction

This project was carried out during the Compiler Construction course at UFPB, taught by Professors Maelson B. and Dr. Clauirton de Albuquerque.

The main objective of this project was to implement the three constituent parts of a conventional compiler: Lexical, Syntactic and Semantic Analyzer, performing error detection and formatting appropriate for the user, so that he can easily identify errors and correct them.

The descriptions of each analyzer are described in the sections below.

## Lexical Analyzer

The Tokens that must be recognized are:

a. Keywords: program, var, integer, real, boolean, procedure, begin, end, if, then, else, while, do, True, False;

b. Identifiers: recognizes a letter followed by letters (lowercase/uppercase), digits or underscores “_”;

c. Integers: represents natural numbers of the form [0..9]+;

d. Real numbers: represents unsigned numbers of the form ([0..9]+.[0..9]*);

e. Delimiters: ; (semicolon), . (full stop), : (colon), ( ) (opening and closing parentheses), , (comma);

f. Assignment command: :=;

g. Relational operators: =, <, >, <=, >=, <>;

h. Additive operators: +, -;

i. Multiplicative operators: *, /.

j. Logical operators: or, and, not.

## Syntactic Analyzer

The grammar of the language to be implemented was provided by Professor Dr. Clauirton de Albuquerque, as presented below.

<pre>
program &rarr;
    &nbsp <b>program id</b>;
    &nbsp variable_declarations
    &nbsp subprogram_declarations
    &nbsp compound_command
    &nbsp .

variable_declarations &rarr;
   &nbsp <b>var</b> variable_declaration_list 
   &nbsp| &epsilon;

variable_declaration_list &rarr;
    &nbsp variable_declaration_list list_of_identifiers<b>:</b>type<b>;</b>
    &nbsp| list_of_identifiers<b>:</b>type<b>;</b>

list_of_identifiers &rarr;
    &nbsp<b>id</b>
    &nbsp| list_of_identifiers<b>,id</b>

type &rarr;
    &nbsp <b>integer</b>
    &nbsp |<b>real</b>
    &nbsp |<b>boolean</b>

subprogram_declarations &rarr;
    &nbsp subprogram_declarations subprogram_declaration<b>;</b>
    &nbsp| &epsilon;

subprogram_declaration &rarr;
    &nbsp <b>procedure id</b> arguments<b>;</b>
    &nbsp variable_declarations
    &nbsp subprogram_declarations
    &nbsp compound_command

arguments &rarr;
    &nbsp (parameter_list)
    &nbsp | &epsilon;

parameter_list &rarr;
    &nbsp list_of_identifiers<b>:</b>type
    &nbsp |parameter_list<b>;</b> list_of_identifiers<b>:</b>type

compound_command &rarr;
    &nbsp <b>begin</b>
    &nbsp optional_commands
    &nbsp <b>end</b>

optional_commands &rarr;
    &nbsp list_of_commands
    &nbsp | &epsilon;

list_of_commands &rarr;
    &nbsp command
    &nbsp |list_of_commands<b>;</b>command

command &rarr;
    &nbsp variable := expression
    &nbsp |procedure_activation
    &nbsp |compound_command
    &nbsp |<b>if</b> expression <b>then</b> command else_part
    &nbsp |<b>while</b> expression <b>do</b> command

else_part &rarr;
    &nbsp <b>else</b> command
    &nbsp | &epsilon;

variable &rarr;
    &nbsp <b>id</b>

procedure_activation &rarr;
    &nbsp <b>id</b>
    &nbsp <b>id</b> (list_of_expressions)

list_of_expressions &rarr;
    &nbsp expression
    &nbsp | list_of_expressions<b>,</b> expression

expression &rarr;
    &nbsp simple_expression
    &nbsp | simple_expression relational_op simple_expression

simple_expression &rarr;
    &nbsp term
    &nbsp | signal term
    &nbsp | simple_expression additive_op term

term &rarr;
    &nbsp factor
    &nbsp | term multiplicative_op factor

factor &rarr;
    &nbsp <b>id</b>
    &nbsp | <b>id</b> (list_of_expressions)
    &nbsp | <b>int_num</b>
    &nbsp | <b>real_num</b>
    &nbsp | <b>true</b>
    &nbsp | <b>false</b>
    &nbsp | (expression)
    &nbsp | <b>not</b> factor

signal &rarr;
    &nbsp + 
    &nbsp | -

relational_op &rarr;
    &nbsp =
    &nbsp | <
    &nbsp | >
    &nbsp | <=
    &nbsp | >=
    &nbsp | <>

additive_op &rarr;
    &nbsp +
    &nbsp | -
    &nbsp | <b>or</b>

multiplicative_op &rarr;
    &nbsp *
    &nbsp | /
    &nbsp | <b>and</b>
</pre>

However, it is clear that this provided grammar contains left recursions and inconsistencies, such as logical operators being together with additive and multiplicative operators, respectively.

Therefore, it was necessary to apply some modifications to adapt it, as can be seen in its updated version below.

## Authors

* Alexandre Bezerra ([Alexandreprog](https://github.com/Alexandreprog))
* Ryann Carlos ([ryann-arruda](https://github.com/ryann-arruda))