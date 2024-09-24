/* Grupo: Alexandre Bezerra de Lima
 * 		  Ryann Carlos de Arruda Quintino
 * */

package main;

import lexical.Scanner;
import syntactic.Syntactic;

public class Compiler {
	public static void main(String[] args) {	
		Scanner sc = new Scanner("testes/teste08.mc");
		Syntactic st = new Syntactic(sc.generateTokenTable());
		
		st.check();
		
		System.out.println("Successful Compiler");
	}
}
