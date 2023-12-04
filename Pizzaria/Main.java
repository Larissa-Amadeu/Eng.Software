public class Main {
    public static void main(String[] args) {
      Pizza margherita = new Margherita();
      Pizza queijoMargherita = new Queijo(margherita);
      
      Pizza pepperoniQueijoMargherita = new Pepperoni(queijoMargherita);

      Comando comandoPedidoPizza = new ComandoPedidoPizza(pepperoniQueijoMargherita);
      //Comando comandoCancelarPedido = new ComandoCancelarPedido(queijoMargherita);

      Garcom garcom = new Garcom();
      garcom.anotarPedido(comandoPedidoPizza);
      //garcom.anotarPedido(comandoCancelarPedido);

      garcom.notificarPedidos();
    }
}