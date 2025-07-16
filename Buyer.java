/**
 * Buyer.java
 * Created by Arvind Kumar S
 * Represents a buyer/customer in the inventory management system.
 */
public class Buyer {
    private int id;
    private String name;
    private String email;
    private String phone;
    private String address;

    /** Default constructor */
    public Buyer() {
        this.name = "";
        this.email = "";
        this.phone = "";
        this.address = "";
    }

    /**
     * Constructor with ID (used when loading from database)
     * @param id buyer ID
     * @param name buyer name
     * @param email buyer email
     * @param phone buyer phone
     * @param address buyer address
     */
    public Buyer(int id, String name, String email, String phone, String address) {
        this.id = id;
        setName(name);
        setEmail(email);
        setPhone(phone);
        setAddress(address);
    }

    /**
     * Constructor without ID (used when creating new buyers)
     */
    public Buyer(String name, String email, String phone, String address) {
        setName(name);
        setEmail(email);
        setPhone(phone);
        setAddress(address);
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) {
        if (name == null || name.trim().isEmpty())
            throw new IllegalArgumentException("Name cannot be empty.");
        this.name = name.trim();
    }

    public String getEmail() { return email; }
    public void setEmail(String email) {
        this.email = (email != null) ? email.trim() : "";
    }

    public String getPhone() { return phone; }
    public void setPhone(String phone) {
        this.phone = (phone != null) ? phone.trim() : "";
    }

    public String getAddress() { return address; }
    public void setAddress(String address) {
        this.address = (address != null) ? address.trim() : "";
    }

    @Override
    public String toString() {
        return String.format("Buyer [ID=%d, Name='%s', Email='%s', Phone='%s', Address='%s']",
                id, name, email, phone, address);
    }
}
