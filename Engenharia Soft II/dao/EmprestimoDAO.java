package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import bd.ConexaoBD;
import classes.*;

public class EmprestimoDAO {

    public boolean registrarEmprestimo(Emprestimo emprestimo, String ra) {
        String sql = "INSERT INTO Emprestimo (ra_aluno, codigo_livro, data_emprestimo, data_prevista_devolucao) VALUES (?, ?, ?, ?)";

        try (Connection conn = ConexaoBD.obterConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, ra);
            stmt.setInt(2, emprestimo.getCodigoLivro());
            stmt.setDate(3, new java.sql.Date(emprestimo.getDataEmprestimo().getTime()));
            stmt.setDate(4, new java.sql.Date(emprestimo.getDataPrevistaDevolucao().getTime()));
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean verificarAtraso(String raAluno, int codigoLivro) {
        String sql = "SELECT prazo_emprestimo FROM livros WHERE ra_aluno = ? AND codigo_livro = ?";
        
        try (Connection conn = ConexaoBD.obterConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, raAluno);
            stmt.setInt(2, codigoLivro);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    LocalDate dataPrevistaDevolucao = rs.getDate("data_prevista_devolucao").toLocalDate();
                    LocalDate dataDevolucao = LocalDate.now();
                    return dataDevolucao.isAfter(dataPrevistaDevolucao);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Emprestimo> buscarEmprestimosPorRA(String RA) {
        List<Emprestimo> emprestimos = new ArrayList<>();
        String sql = "SELECT * FROM Emprestimo WHERE ra_aluno = ?";

        try (Connection conn = ConexaoBD.obterConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, RA);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Emprestimo emprestimo = new Emprestimo();
                    emprestimo.setId(rs.getInt("id"));
                    emprestimo.setDataEmprestimo(rs.getDate("data_emprestimo"));
                    emprestimo.setDataPrevistaDevolucao(rs.getDate("data_prevista_devolucao"));

                    // Carregar aluno associado ao empréstimo
                    Aluno aluno = new AlunoDAO().buscarAlunoPorRA(RA);
                    emprestimo.setAluno(aluno);

                    // Carregar livro associado ao empréstimo
                    int codigoLivro = rs.getInt("codigo_livro");
                    Livro livro = new LivroDAO().buscarLivroPorCodigo(codigoLivro);
                    emprestimo.setLivro(livro);

                    emprestimos.add(emprestimo);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return emprestimos;
    }
}
