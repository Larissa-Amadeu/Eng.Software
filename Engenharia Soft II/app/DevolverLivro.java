
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

  
    }

    private void devolverLivro(String raAluno, int codigoLivro) {
        try (Connection connection = ConexaoBD.obterConexao()) {
            String sqlVerificarEmprestimo = "SELECT * FROM emprestimo WHERE ra_aluno = ? AND codigo_livro = ? AND data_devolucao IS NULL";
            
            try (PreparedStatement stmtVerificarEmprestimo = connection.prepareStatement(sqlVerificarEmprestimo)) {
                stmtVerificarEmprestimo.setString(1, raAluno);
                stmtVerificarEmprestimo.setInt(2, codigoLivro);
                ResultSet resultSet = stmtVerificarEmprestimo.executeQuery();
               

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
