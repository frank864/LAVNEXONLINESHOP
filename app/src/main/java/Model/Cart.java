package Model;
public class Cart {
    private String pid, pname, price, discount;
    private int quantity;

    public Cart() {
    }

    public Cart(String pid, String pname, String price, String discount, int quantity) {
        this.pid = pid;
        this.pname = pname;
        this.price = price;
        this.discount = discount;
        this.quantity = quantity;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getPname() {
        return pname;
    }

    public void setPname(String pname) {
        this.pname = pname;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}



