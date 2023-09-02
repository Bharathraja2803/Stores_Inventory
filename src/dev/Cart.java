package dev;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class Cart {
	
	enum CartType{PHYSICAL, VIRTUAL}
	private static int lastId =1;
	private int id;
	private CartType cartType;
	private LocalDate cartDate;
	private Map<String, Integer> products;
	
	
	public Cart(CartType cartType, int days) {
		id = lastId++;
		this.cartType = cartType;
		this.cartDate = LocalDate.now().minusDays(days);
		products = new HashMap<>();
	}
	
	public Cart(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public LocalDate getCartDate() {
		return cartDate;
	}
	
	
	
	public Map<String, Integer> getProducts() {
		return products;
	}

	public void addItem(InventoryItem item, int qty) {
		
		if(item.availableQty() >= qty) {
			item.reserveItem(qty);
			System.out.printf("%s successfully added to %d cart%n",item.getProduct().name(),id);
			products.merge(item.getProduct().sku(), qty, Integer::sum);
		}else {
			System.out.println("Something went wrong reservation undone!");
			System.out.printf("%s not added to %d cart%n",item.getProduct().name(),id);
			products.merge(item.getProduct().sku(), 0, Integer::sum);
		}
			
		
	}
	
	public void removeCart(InventoryItem item, int qty) {
		int currentQty = products.get(item.getProduct().sku());
		if(currentQty <= qty) {
			qty = currentQty;
			products.remove(item.getProduct().sku());
			System.out.printf("%s is removed from cart",item.getProduct().name());;
		}else {
			products.merge(item.getProduct().sku(), qty, (oldVal, newVal) -> oldVal - newVal);
			System.out.printf("%d [%s]s removed from cart%n",qty,item.getProduct().name());
		}
		item.releaseItem(qty);
	}
	
	public void printSalesSlip(Map<String, InventoryItem> inventory) {
		double total =0;
		
		System.out.println("-".repeat(40));
		System.out.println("Thanks For Shopping");
		for (var cartItem : products.entrySet()) {
			InventoryItem item = inventory.get(cartItem.getKey());
			int qty = cartItem.getValue();
			double itemizedPrize = item.getPrice() * qty;
			total += itemizedPrize;
			System.out.printf("\t%s %-10s %.2f*(%d) %10.2f%n",cartItem.getKey(), item.getProduct().name(),item.getPrice(),qty,itemizedPrize);
		}
		System.out.printf("\tTotal prize : %.2f%n", total);
		System.out.println("-".repeat(40));
	}
	
	@Override
	public String toString() {
		return "Cart%3d %-3s %-3s".formatted(id, cartDate, products);
	}
}
