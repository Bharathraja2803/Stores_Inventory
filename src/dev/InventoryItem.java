package dev;

public class InventoryItem {
	private Product product;
	private double price;
	private int qtyTotal;
	private int qtyReserved;
	private int qtyLow;
	private int qtyReorder;
	public InventoryItem(Product product, double price, int qtyTotal, int qtyLow) {
		
		this.product = product;
		this.price = price;
		this.qtyTotal = qtyTotal;
		this.qtyLow = qtyLow;
		this.qtyReorder = qtyTotal;
	}
	public Product getProduct() {
		return product;
	}
	public double getPrice() {
		return price;
	}
	
	public boolean reserveItem(int qty) {
		if(qtyReserved + qty <= qtyTotal) {
			qtyReserved+=qty;
			return true;
		}
		return false;
	}
	
	public void releaseItem(int qty) {
		if(qtyReserved >= qty) {
			qtyReserved -=qty;
		}else {
			qtyReserved = 0;
		}
		
	}
	
	public boolean sellItem(int qty) {
		if(qtyTotal >= qty) {
			qtyTotal -= qty;
			releaseItem(qty);
			if(qtyTotal<=qtyLow) {
				placeInventoryOrder();
			}
			return true;
			
		}
		return false;
	}
	
	public int availableQty() {
		return qtyTotal - qtyReserved;
	}
	
	public void placeInventoryOrder() {
		System.out.printf("Ordering %s : %d Nos%n",product, qtyReorder);
		qtyTotal+=qtyReorder;
	}
	
	public String toString() {
		return "%s, Rs.%.2f : [%04d, %2d]".formatted(product, price, qtyTotal, qtyReserved);
	}
}
