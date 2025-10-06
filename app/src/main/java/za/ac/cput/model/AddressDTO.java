package za.ac.cput.model;

public class AddressDTO {

    private Long id;
    private String streetNumber;
    private String streetName;
    private String suburb;
    private String city;
    private String postalCode;
    private Long userId;  // Only the ID of the user is needed for the DTO

    // Default constructor
    public AddressDTO() {
    }

    // Constructor matching the Address domain model
    public AddressDTO(Long id, String streetNumber, String streetName, String suburb,
                      String city, String postalCode, Long userId) {
        this.id = id;
        this.streetNumber = streetNumber;
        this.streetName = streetName;
        this.suburb = suburb;
        this.city = city;
        this.postalCode = postalCode;
        this.userId = userId;
    }

    // Getters and setters

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
//package za.ac.cput.model;
//
//public class AddressDTO {
//
//    private Long id;
//    private String streetNumber;
//    private String streetName;
//    private String suburb;
//    private String city;
//    private String province;
//    private String country;
//    private String postalCode;
//    private Long userId;  // Link to the user
//
//    // Default constructor (required for Retrofit/Gson)
//    public AddressDTO() {
//    }
//
//    // Full constructor (optional convenience)
//    public AddressDTO(Long id, String streetNumber, String streetName, String suburb, String city,
//                      String province, String country, String postalCode, Long userId) {
//        this.id = id;
//        this.streetNumber = streetNumber;
//        this.streetName = streetName;
//        this.suburb = suburb;
//        this.city = city;
//        this.province = province;
//        this.country = country;
//        this.postalCode = postalCode;
//        this.userId = userId;
//    }
//
//    // Getters and setters
//
//    public Long getId() {
//        return id;
//    }
//
//    public void setId(Long id) {
//        this.id = id;
//    }
//
//    public String getStreetNumber() {
//        return streetNumber;
//    }
//
//    public void setStreetNumber(String streetNumber) {
//        this.streetNumber = streetNumber;
//    }
//
//    public String getStreetName() {
//        return streetName;
//    }
//
//    public void setStreetName(String streetName) {
//        this.streetName = streetName;
//    }
//
//    public String getSuburb() {
//        return suburb;
//    }
//
//    public void setSuburb(String suburb) {
//        this.suburb = suburb;
//    }
//
//    public String getCity() {
//        return city;
//    }
//
//    public void setCity(String city) {
//        this.city = city;
//    }
//
//    public String getProvince() {
//        return province;
//    }
//
//    public void setProvince(String province) {
//        this.province = province;
//    }
//
//    public String getCountry() {
//        return country;
//    }
//
//    public void setCountry(String country) {
//        this.country = country;
//    }
//
//    public String getPostalCode() {
//        return postalCode;
//    }
//
//    public void setPostalCode(String postalCode) {
//        this.postalCode = postalCode;
//    }
//
//    public Long getUserId() {
//        return userId;
//    }
//
//    public void setUserId(Long userId) {
//        this.userId = userId;
//    }
//}
