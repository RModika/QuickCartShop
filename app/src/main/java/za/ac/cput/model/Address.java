package za.ac.cput.model;

public class Address {

    private Long id;                 // ✅ Matches backend field
    private String streetNumber;
    private String streetName;
    private String suburb;
    private String city;
    private String province;
    private String country;
    private String postalCode;
    private Long userId;

    // No-arg constructor (required for Retrofit & Gson)
    public Address() {
    }

    // Full constructor
    public Address(Long id, String streetNumber, String streetName, String suburb, String city,
                   String province, String country, String postalCode, Long userId) {
        this.id = id;
        this.streetNumber = streetNumber;
        this.streetName = streetName;
        this.suburb = suburb;
        this.city = city;
        this.province = province;
        this.country = country;
        this.postalCode = postalCode;
        this.userId = userId;
    }

    // Constructor without ID (for creating a new address before saving)
    public Address(String streetNumber, String streetName, String suburb, String city,
                   String province, String country, String postalCode, Long userId) {
        this(null, streetNumber, streetName, suburb, city, province, country, postalCode, userId);
    }

    // --- Getters and Setters ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStreetNumber() {
        return streetNumber;
    }

    public void setStreetNumber(String streetNumber) {
        this.streetNumber = streetNumber;
    }

    public String getStreetName() {
        return streetName;
    }

    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }

    public String getSuburb() {
        return suburb;
    }

    public void setSuburb(String suburb) {
        this.suburb = suburb;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
