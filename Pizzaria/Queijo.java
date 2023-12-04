public class Queijo extends ToppingDecorator {
    public Queijo(Pizza pizza) {
        super(pizza);
    }

    public String getDescricao() {
        return super.getDescricao() + ", Queijo";
    }

    public double custo() {
        return super.custo() + 5.90;
    }
}
