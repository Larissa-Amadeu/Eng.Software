public class ComandoPedidoPizza implements Comando {
    private Pizza pizza;

    public ComandoPedidoPizza(Pizza pizza) {
        this.pizza = pizza;
    }

    public void executar() {
        System.out.printf("Pedido: %s - Custo: R$%.2f%n", pizza.getDescricao(), pizza.custo());
    }
}
