@Entity
@Table(name = "order_has_dishes")
@Data
@NoArgsConstructor
public class OrderDish {

	@EmbeddedId
	private OrderDishId id;
	@EqualsAndHashCode.Exclude
	@ManyToOne(fetch = FetchType.LAZY)
	@MapsId("orderId")
	private Order order;
	@EqualsAndHashCode.Exclude
	@ManyToOne
	@MapsId("dishId")
	private Dish dish;
	private int count;
	private BigDecimal amount;

	public OrderDish(Order order, Dish dish, int count) {
		this.id = new OrderDishId(order.getId(), dish.getId());
		this.order = order;
		this.dish = dish;
		this.count = count;
		this.amount = calculateAmount(count);
	}

	public void changeCountOfDishesInOrder(int count) {
		this.count = count;
		this.amount = calculateAmount(count);
	}

	private BigDecimal calculateAmount(int count) {
		return dish.getPrice().multiply(valueOf(count));
	}

}