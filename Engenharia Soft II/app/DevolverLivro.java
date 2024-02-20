import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import bd.ConexaoBD;
import classes.*;
import dao.*;

public class DevolverLivro extends Application {

    @Override
    public void start(Stage primaryStage) {
        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(10));
        gridPane.setHgap(10);
        gridPane.setVgap(10);

        Label lblRA = new Label("RA do Aluno:");
        TextField txtRA = new TextField();
        gridPane.add(lblRA, 0, 0);
        gridPane.add(txtRA, 1, 0);

        Label lblCodigoLivro = new Label("Código do Livro:");
        TextField txtCodigoLivro = new TextField();
        gridPane.add(lblCodigoLivro, 0, 1);
        gridPane.add(txtCodigoLivro, 1, 1);

        Button btnDevolver = new Button("Devolver Livro");
        Button btnVoltarInicio = new Button("Voltar ao Início");

        btnDevolver.setOnAction(e -> devolverLivro(txtRA.getText(), Integer.parseInt(txtCodigoLivro.getText())));
        btnVoltarInicio.setOnAction(e ->  primaryStage.close());

        VBox vbox = new VBox(10);
        vbox.setAlignment(Pos.CENTER);
        vbox.getChildren().addAll(gridPane, btnDevolver, btnVoltarInicio);

        Scene scene = new Scene(vbox, 300, 200);
        primaryStage.setTitle("Devolução de Livro");
        primaryStage.setScene(scene);
        primaryStage.show();


    }

    private void devolverLivro(String raAluno, int codigoLivro) {
        try (Connection connection = ConexaoBD.obterConexao()) {
            String sqlVerificarEmprestimo = "SELECT * FROM emprestimo WHERE ra_aluno = ? AND codigo_livro = ? AND data_devolucao IS NULL";
            
            try (PreparedStatement stmtVerificarEmprestimo = connection.prepareStatement(sqlVerificarEmprestimo)) {
                stmtVerificarEmprestimo.setString(1, raAluno);
                stmtVerificarEmprestimo.setInt(2, codigoLivro);
                ResultSet resultSet = stmtVerificarEmprestimo.executeQuery();
                
                if (resultSet.next()) {
                    // Obtém a data prevista de devolução do livro
                    Date dataPrevistaDevolucao = resultSet.getDate("data_prevista_devolucao");
                    // Obtém a data atual
                    Date dataAtual = new Date(System.currentTimeMillis());

                    // Verifica se a data de devolução ocorreu após a data prevista de devolução
                    if (dataAtual.after(dataPrevistaDevolucao)) {
                        AlunoDAO.criarDebito(raAluno);
                    }

                    String sqlDevolverLivro = "UPDATE emprestimo SET data_devolucao = CURRENT_TIMESTAMP WHERE ra_aluno = ? AND codigo_livro = ?";
                    LivroDAO.marcarLivroComoDisponivel(codigoLivro); // Marcar livro como disponível novamente
                    
                    try (PreparedStatement stmtDevolverLivro = connection.prepareStatement(sqlDevolverLivro)) {
                        stmtDevolverLivro.setString(1, raAluno);
                        stmtDevolverLivro.setInt(2, codigoLivro);
                        int rowsAffected = stmtDevolverLivro.executeUpdate();
                        if (rowsAffected > 0) {
                            exibirAlerta("Devolução Bem-Sucedida", "O livro foi devolvido com sucesso.");
                        } else {
                            exibirAlerta("Erro na Devolução", "Não foi possível devolver o livro.");
                        }
                    }
                } else {
                    exibirAlerta("Livro não Emprestado", "O livro não está emprestado para este aluno.");
                }
            }
        } catch (SQLException ex) {
            exibirAlerta("Erro de Conexão", "Não foi possível conectar ao banco de dados.");
            ex.printStackTrace();
        }
    }

    private void exibirAlerta(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
