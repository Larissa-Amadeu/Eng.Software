public class ComandoCancelarPedido implements Comando {
    private Pizza pizza;

    public ComandoCancelarPedido(Pizza pizza) {
        this.pizza = pizza;
    }

    public void executar() {
        System.out.printf("Cancelado: %s%n", pizza.getDescricao());
    }
}
