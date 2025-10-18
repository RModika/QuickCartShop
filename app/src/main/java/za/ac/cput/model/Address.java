package za.ac.cput.model;
import android.os.Parcel;
import android.os.Parcelable;
public class Address implements Parcelable{

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
    protected Address(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readLong();
        }
        streetNumber = in.readString();
        streetName = in.readString();
        suburb = in.readString();
        city = in.readString();
        postalCode = in.readString();
        if (in.readByte() == 0) {
            userId = null;
        } else {
            userId = in.readLong();
        }
    }

    public static final Creator<Address> CREATOR = new Creator<Address>() {
        @Override
        public Address createFromParcel(Parcel in) {
            return new Address(in);
        }

        @Override
        public Address[] newArray(int size) {
            return new Address[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        if (id == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeLong(id);
        }
        parcel.writeString(streetNumber);
        parcel.writeString(streetName);
        parcel.writeString(suburb);
        parcel.writeString(city);
        parcel.writeString(postalCode);
        if (userId == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeLong(userId);
        }
    }
}
