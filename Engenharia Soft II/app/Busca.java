
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import bd.ConexaoBD;
import classes.*;
import dao.*;

public class Busca extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Buscar");
   
            String resultado = realizarBusca(tipoBusca, termoBusca);
            //Aluno resultado = AlunoDAO.buscarAlunoPorRA(termoBusca);

            
            txtResultados.setText(resultado);
        });

        btnVoltarInicio.setOnAction(e ->  primaryStage.close());

        Scene scene = new Scene(root, 400, 300);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

   
    private String realizarBusca(String tipoBusca, String termoBusca) {
        switch (tipoBusca) {
            case "Aluno":
                return buscarAluno(termoBusca);
            case "Livro":
                return buscarLivro(termoBusca);
            default:
                return "Tipo de busca não suportado.";
        }
    }

    
    private String buscarAluno(String termoBusca) {
        try (Connection connection = ConexaoBD.obterConexao()) {
            String sql = "SELECT ra, nome, debito FROM alunos WHERE ra = ? OR nome LIKE ?";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setString(1, termoBusca);
                stmt.setString(2, "%" + termoBusca + "%");
                ResultSet resultSet = stmt.executeQuery();
                if (resultSet.next()) {
                    String ra = resultSet.getString("ra");
                    String nome = resultSet.getString("nome");
                    boolean debito = resultSet.getBoolean("debito");
                    return exibirDetalhesAluno(ra, nome, debito);
                } else {
                    return "Aluno não encontrado.";
                }
            }
        } catch (SQLException ex) {
            return "Erro ao acessar o banco de dados.";
        }
    }

   
   
   o
    private List<String> obterEmprestimosPendentes(String ra) {
        List<String> emprestimosPendentes = new ArrayList<>();
        try (Connection connection = ConexaoBD.obterConexao()) {
            String sql = "SELECT data_emprestimo FROM emprestimo WHERE ra_aluno = ? AND data_devolucao IS NULL";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setString(1, ra);
                ResultSet resultSet = stmt.executeQuery();
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                while (resultSet.next()) {
                    Date dataEmprestimo = resultSet.getDate("data_emprestimo");
                    String dataFormatada = dateFormat.format(dataEmprestimo);
                    emprestimosPendentes.add("Data do Empréstimo: " + dataFormatada);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return emprestimosPendentes;
    }

       
    private String buscarLivro(String termoBusca) {
        StringBuilder resultado = new StringBuilder();
        try (Connection connection = ConexaoBD.obterConexao();
            PreparedStatement statement = connection.prepareStatement("SELECT id, titulo, disponivel, prazo_emprestimo FROM livros WHERE titulo LIKE ? OR id = ?")) {
            statement.setString(1, "%" + termoBusca + "%");
            // Verificar se o termo de busca é um número válido
            int id = 0;
            try {
                id = Integer.parseInt(termoBusca);
            } catch (NumberFormatException ex) {
                // Ignorar se o termo de busca não for um número válido
            }
            statement.setInt(2, id);

            ResultSet resultSet = statement.executeQuery();
            boolean livroEncontrado = false;
            while (resultSet.next()) {
                int idLivro = resultSet.getInt("id");
                String titulo = resultSet.getString("titulo");
                boolean disponibilidade = resultSet.getBoolean("disponivel");
                Date prazoEmprestimo = resultSet.getDate("prazo_emprestimo");

                resultado.append("ID: ").append(idLivro).append("\n")
                        .append("Título: ").append(titulo).append("\n")
                        .append("Disponibilidade: ").append(disponibilidade ? "Sim" : "Não");
                if (!disponibilidade) {
                    resultado.append("\n").append("Prazo de Empréstimo: ");
                    if (prazoEmprestimo != null) {
                        resultado.append(prazoEmprestimo.toString());
                    } else {
                        resultado.append("Indisponível");
                    }
                }
                resultado.append("\n\n");
                livroEncontrado = true;
            }
            if (!livroEncontrado) {
                resultado.append("Livro não encontrado.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultado.toString();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
