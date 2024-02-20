package classes;

import java.util.Scanner;

public class Main {

	public static void main(String[] args) {

		//funcionalidades completas em 'app > Menu.java'
		
		 Scanner entrada = new Scanner (System.in);
		 int[] codigos= {0,0,0,0,0,0,0,0,0,0,0,0,0,0};
		 System.out.print("RA: ");
		 String aluno = entrada.nextLine();
		 System.out.print("livros: ");
		 int num = entrada.nextInt();
		 int aux;
		 for(int i=0;i<num;i++)
		 {
			 System.out.print("codigo do livro "+(i+1) +": ");
			 aux=entrada.nextInt();
			 codigos[i]=aux;
		 }
		 
        Controle c = new Controle();
        c.Emprestar(aluno, null, codigos, num);
    		 
	}

}
