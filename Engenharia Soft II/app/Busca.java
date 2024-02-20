import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.sql.Connection;
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

        BorderPane root = new BorderPane();

        // Criando uma caixa de seleção para escolher o tipo de busca
        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.getItems().addAll("Aluno", "Livro");
        comboBox.setValue("Aluno");

        // Criando campos de texto para entrada de dados
        TextField txtBuscar = new TextField();
        txtBuscar.setPromptText("Digite o ID ou nome");
        txtBuscar.setMaxWidth(150);

        // Botão para iniciar a busca
        Button btnBuscar = new Button("Buscar");
        Button btnVoltarInicio = new Button("Voltar ao Início");

        // Área para exibir resultados da busca
        TextArea txtResultados = new TextArea();
        txtResultados.setEditable(false);
        txtResultados.setMaxWidth(350);
        txtResultados.setMaxHeight(110);

        // Adicionando componentes ao layout
        VBox vbox = new VBox(10);
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(20));
        vbox.getChildren().addAll(comboBox, txtBuscar, btnBuscar, btnVoltarInicio);
        BorderPane.setAlignment(vbox, Pos.CENTER);

        root.setTop(vbox);
        root.setCenter(txtResultados);

        // Definindo o comportamento do botão de busca
        btnBuscar.setOnAction(e -> {
            String tipoBusca = comboBox.getValue();
            String termoBusca = txtBuscar.getText();

            // Realizar a busca com base no tipo selecionado
            String resultado = realizarBusca(tipoBusca, termoBusca);
            //Aluno resultado = AlunoDAO.buscarAlunoPorRA(termoBusca);

            // Exibir o resultado na área de texto
            txtResultados.setText(resultado);
        });

        btnVoltarInicio.setOnAction(e ->  primaryStage.close());

        Scene scene = new Scene(root, 400, 300);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Método para realizar a busca com base no tipo selecionado
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

    // Método para buscar detalhes do aluno
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

    // Método para exibir detalhes do aluno
    private String exibirDetalhesAluno(String ra, String nome, boolean debito) {
        StringBuilder detalhes = new StringBuilder();
        detalhes.append("Detalhes do Aluno:\n");
        detalhes.append("Nome: ").append(nome).append("\n");
        detalhes.append("RA: ").append(ra).append("\n");
        detalhes.append("Débito: ").append(debito ? "Sim" : "Não").append("\n");

        // Obtendo empréstimos pendentes do aluno
        List<String> emprestimosPendentes = obterEmprestimosPendentes(ra);
        if (emprestimosPendentes.isEmpty()) {
            detalhes.append("Empréstimos Pendentes: Não há empréstimos pendentes.");
        } else {
            detalhes.append("Empréstimos Pendentes:\n");
            for (String emprestimo : emprestimosPendentes) {
                detalhes.append(emprestimo).append("\n");
            }
        }
        return detalhes.toString();
    }

    // Método para obter empréstimos pendentes do aluno
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

        // Método para buscar detalhes do livro
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
