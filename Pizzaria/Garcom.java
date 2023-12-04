import java.util.ArrayList;
import java.util.List;

public class Garcom {
    private List<Comando> pedidos = new ArrayList<>();

    public void anotarPedido(Comando comando) {
        pedidos.add(comando);
        comando.executar();
    }

    public void cancelarPedido(Comando comando) {
        pedidos.remove(comando);
        comando.executar();
    }

    public void notificarPedidos() {
        System.out.println("Informando a cozinha sobre os pedidos:");
        for (Comando pedido : pedidos) {
            pedido.executar();
        }
    }
}
