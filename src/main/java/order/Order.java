package order;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Order implements Serializable {

	private String productCode;
	
	public Order(String productCode) {
		this.productCode = productCode;
	}
	
	public String getProductCode() {
		return productCode;
	}

	@Override
	public int hashCode() {
		return productCode.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (getClass() != obj.getClass())
			return false;
		Order other = (Order) obj;
		return productCode.equals(other.productCode);
	}
	
	@Override
	public String toString() {
		return String.format("Product %s", productCode);
	}

}
