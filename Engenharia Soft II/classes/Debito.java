package classes;

import dao.*;

public class Debito {
	int codigoAluno;

	public Debito(int aluno){
		this.codigoAluno = aluno;
	}
	
	public boolean verificaDebito(String ra){
		return AlunoDAO.verificarDebitosAluno(ra);
	}

}