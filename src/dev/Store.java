package dev;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableSet;
import java.util.Random;
import java.util.TreeMap;
import java.util.TreeSet;


public class Store {

	private static Random random = new Random();
	private Map<String, InventoryItem> inventory;
	private NavigableSet<Cart> carts = new TreeSet<>(Comparator.comparing(Cart::getId));
	private Map<Category, Map<String,InventoryItem>> aisleInventory;
	
	
	public static void main(String[] args) {
		Store myStore = new Store();
		
		myStore.stockStore();
		myStore.printHeader("Inventory Item");
		myStore.listInventory();
		
		myStore.stockAisles();
		myStore.printHeader("List by category");
		myStore.listProductsByCategory();
		
		myStore.printHeader("List by category(Detailed)");
		myStore.listProductsByCategory(false, true);
		
		
		myStore.printHeader("Cart details");
		myStore.manageCarts();
		
		myStore.printHeader("After adding cart Inventory details");
		myStore.listInventory();
		
		myStore.printHeader("Abondon old carts");
		myStore.abondonCarts();
		myStore.listProductsByCategory(false, true);
        myStore.carts.forEach(System.out::println);

	}
	
	private void manageCarts() {
		Cart cart1 = new Cart(Cart.CartType.PHYSICAL, 10);
		InventoryItem item = aisleInventory.get(Category.DAIRY).get("milk");
		carts.add(cart1);
		cart1.addItem(item, 2);
		cart1.addItem(aisleInventory.get(Category.CEREAL).get("rice chex"), 5);
		cart1.addItem(aisleInventory.get(Category.MEAT).get("chicken"), 1);
		cart1.removeCart(aisleInventory.get(Category.CEREAL).get("rice chex"), 2);
		
		
		Cart cart2 = new Cart(Cart.CartType.VIRTUAL,1);
		carts.add(cart2);
		cart2.addItem(inventory.get("Y001"), 10);
		cart2.addItem(inventory.get("C333"), 20);
		
		
		Cart cart3 = new Cart(Cart.CartType.VIRTUAL, 0);
		carts.add(cart3);
		cart3.addItem(inventory.get("R777"), 101);
		if(!checkOutCart(cart3)) {
			System.out.println("Something went wrong");
		}
		carts.forEach(System.out::println);
		
		Cart cart4 =  new Cart(Cart.CartType.PHYSICAL, 0);
        carts.add(cart4);
        cart4.addItem(aisleInventory.get(Category.BEVERAGE).get("tea"), 1);
        
        carts.forEach(System.out::println);
		
	}
	
	private boolean checkOutCart(Cart cart) {
		for (var cartItem : cart.getProducts().entrySet()) {
			var item = inventory.get(cartItem.getKey());
			int qty = cartItem.getValue();
			if(!item.sellItem(qty)) return false;
		}
		cart.printSalesSlip(inventory);
		carts.remove(cart);
		return true;
	}
	
	private void abondonCarts() {
		int dayOfYear = LocalDate.now().getDayOfYear();
		Cart lastCart = null;
		
		for(Cart cart : carts) {
			if(cart.getCartDate().getDayOfYear() == dayOfYear) {
				break;
			}
			lastCart = cart;
		}
		
		var oldCart = carts.headSet(lastCart,true);
		Cart abondonCart = null;
		while((abondonCart = oldCart.pollFirst()) != null) {
			for(String sku : abondonCart.getProducts().keySet()) {
				var item = inventory.get(sku);
				item.releaseItem(abondonCart.getProducts().get(sku));
			}
		}
		
	}
	
	private void listProductsByCategory() {
		listProductsByCategory(true, false);
	}
	
	private void listProductsByCategory(boolean includeHeader, boolean includeDetail) {
		aisleInventory.keySet().forEach(
				k -> {
					
					if(includeHeader) printHeader(k.toString());
					if(includeDetail) {
						aisleInventory.get(k).values().forEach(System.out::println);
					}else {
						aisleInventory.get(k).keySet().forEach(System.out::println);
					}
				}
				);
		
	}
	
	private void stockStore() {
		inventory = new HashMap<>();
		List<Product> products = new ArrayList<>(
				List.of(
						new Product("A100","apple","local",Category.PRODUCE),
		                new Product("B100","banana","local",Category.PRODUCE),
		                new Product("P100","pear","local",Category.PRODUCE),
		                new Product("L103","lemon","local",Category.PRODUCE),
		                new Product("M201","milk","farm",Category.DAIRY),
		                new Product("Y001","yogurt","farm",Category.DAIRY),
		                new Product("C333","cheese","farm",Category.DAIRY),
		                new Product("R777","rice chex","Nabisco",Category.CEREAL),
		                new Product("G111","granola","Nat Valley",Category.CEREAL),
		                new Product("BB11","ground beef","butcher",Category.MEAT),
		                new Product("CC11","chicken","butcher",Category.MEAT),
		                new Product("BC11","bacon","butcher",Category.MEAT),
		                new Product("BC77","coke","coca cola",Category.BEVERAGE),
		                new Product("BC88","coffee","value",Category.BEVERAGE),
		                new Product("BC99","tea","herbal",Category.BEVERAGE)
						)
				
				);
		
		products.forEach(p -> inventory.put(p.sku(), new InventoryItem(p,random.nextDouble(50,150),1000,5)));
		
		
	}
	
	
	private void stockAisles() {
		aisleInventory = new EnumMap<>(Category.class);
		for (InventoryItem item : inventory.values()) {
			Category aisle = item.getProduct().category();
			Map<String, InventoryItem> productMap = aisleInventory.get(aisle);
			
			if(productMap == null) {
				productMap = new TreeMap<>();
			}
			productMap.put(item.getProduct().name(), item);
			aisleInventory.putIfAbsent(aisle, productMap);
		}
	}
	
	private void listInventory() {
		inventory.values().forEach(System.out::println);
		
	}
	
	private void printHeader(String header) {
		System.out.println("-".repeat(30));
		System.out.println(header);
		System.out.println("=".repeat(header.length()));
	}
}
