public class Pepperoni extends ToppingDecorator {
    public Pepperoni(Pizza pizza) {
        super(pizza);
    }

    public String getDescricao() {
        return super.getDescricao() + ", Pepperoni";
    }

    public double custo() {
        return super.custo() + 14.40;
    }
}