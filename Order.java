@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true, exclude = "orderDishes")
@ToString(exclude = "orderDishes")
@EntityListeners(AuditingEntityListener.class)
public class Order extends AbstractEntity {

	private BigDecimal amount;
	@CreatedDate
	private Date createdOn;
	@LastModifiedDate
	private Date updatedOn;
	@Enumerated(EnumType.STRING)
	private OrderTypeEnum status;
	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;
	@ManyToOne
	@JoinColumn(name = "restaurant_id")
	private Restaurant restaurant;
	@OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private Set<OrderDish> orderDishes;

	public Order(Restaurant restaurant) {
		this.restaurant = restaurant;
		this.orderDishes = new HashSet<>();
		this.amount = BigDecimal.ZERO;
		this.status = OrderTypeEnum.CREATED;
		this.user = getCurrentUser();
	}

	public void addDish(Dish dish, int count) {
		if (count > 0) {
			OrderDish orderDish = new OrderDish(this, dish, count);
			orderDishes.add(orderDish);
			amount = amount.add(orderDish.getAmount());
		}
	}

	public void removeDish(Dish dish) {
		orderDishes.removeIf(orderDish -> orderDish.getDish().equals(dish));
	}

}