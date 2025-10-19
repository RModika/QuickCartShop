package za.ac.cput.ui.product;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import za.ac.cput.R;
import za.ac.cput.adapters.AdminProductAdapter;
import za.ac.cput.model.Category;
import za.ac.cput.model.Product;
import za.ac.cput.services.AdminProductApiService;
import za.ac.cput.services.ApiClient;
import za.ac.cput.services.CategoryApiService;

public class AdminProductManagementActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AdminProductAdapter adapter;
    private List<Product> productList;
    private AdminProductApiService apiService;
    private CategoryApiService categoryService;

    private String selectedAssetImageName;
    private ImageView imagePreview;
    private Button addProductButton;
    private List<Category> categoryList;
    private Button viewUsersButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_products);

        recyclerView = findViewById(R.id.adminProductsRecyclerView);
        addProductButton = findViewById(R.id.addProductButton);
        viewUsersButton = findViewById(R.id.viewUsersButton);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        productList = new ArrayList<>();
        categoryList = new ArrayList<>();

        // Initialize API services
        apiService = ApiClient.getClient().create(AdminProductApiService.class);
        categoryService = ApiClient.getClient().create(CategoryApiService.class);

        adapter = new AdminProductAdapter(productList, new AdminProductAdapter.OnItemClickListener() {
            @Override
            public void onEdit(Product product) {
                showEditProductDialog(product);
            }

            @Override
            public void onDelete(Product product) {
                deleteProduct(product);
            }
        });

        recyclerView.setAdapter(adapter);
        loadProducts();

        addProductButton.setOnClickListener(v -> showAddProductDialog());


        viewUsersButton.setOnClickListener(v -> {
            Intent intent = new Intent(AdminProductManagementActivity.this, za.ac.cput.ui.ViewUsersActivity.class);
            startActivity(intent);
        });
    }


    private void showEditProductDialog(Product product) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_product_form, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        EditText inputName = dialogView.findViewById(R.id.inputProductName);
        EditText inputPrice = dialogView.findViewById(R.id.inputProductPrice);
        EditText inputDescription = dialogView.findViewById(R.id.inputProductDescription);
        Spinner availabilitySpinner = dialogView.findViewById(R.id.spinnerAvailability);
        Spinner categorySpinner = dialogView.findViewById(R.id.spinnerCategory);
        Button saveButton = dialogView.findViewById(R.id.saveButton);
        Button selectImageButton = dialogView.findViewById(R.id.selectImageButton);
        imagePreview = dialogView.findViewById(R.id.imagePreview);

        // Change button text to "Update Product"
        saveButton.setText("Update Product");

        // Reset image selection
        selectedAssetImageName = null;

        // Populate fields with existing product data
        inputName.setText(product.getProductName());
        inputPrice.setText(String.valueOf(product.getProductPrice()));
        inputDescription.setText(product.getProductDescription());

        // Availability spinner setup
        String[] availabilityOptions = {"AVAILABLE", "LOW_STOCK", "OUT_OF_STOCK"};
        ArrayAdapter<String> availabilityAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, availabilityOptions);
        availabilityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        availabilitySpinner.setAdapter(availabilityAdapter);

        // Set current availability
        if (product.getStockAvailability() != null) {
            String currentAvailability = product.getStockAvailability();
            for (int i = 0; i < availabilityOptions.length; i++) {
                if (availabilityOptions[i].equals(currentAvailability)) {
                    availabilitySpinner.setSelection(i);
                    break;
                }
            }
        }

        // Load categories and set current category
        categoryService.getAllCategories().enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    categoryList = response.body();
                    ArrayAdapter<Category> adapter = new ArrayAdapter<>(AdminProductManagementActivity.this,
                            android.R.layout.simple_spinner_item, categoryList);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    categorySpinner.setAdapter(adapter);

                    // Set current category
                    if (product.getCategory() != null) {
                        for (int i = 0; i < categoryList.size(); i++) {
                            if (categoryList.get(i).getCategoryId().equals(product.getCategory().getCategoryId())) {
                                categorySpinner.setSelection(i);
                                break;
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {
                Toast.makeText(AdminProductManagementActivity.this,
                        "Error loading categories: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // Load current product image if exists
        if (product.getProductId() != null) {
            loadProductImage(product.getProductId());
        }

        selectImageButton.setOnClickListener(v -> showAssetImagePicker());

        saveButton.setOnClickListener(v -> {
            String name = inputName.getText().toString().trim();
            String priceStr = inputPrice.getText().toString().trim();
            String description = inputDescription.getText().toString().trim();
            String selectedAvailability = availabilitySpinner.getSelectedItem().toString();
            Category selectedCategory = (Category) categorySpinner.getSelectedItem();

            if (name.isEmpty() || priceStr.isEmpty() || description.isEmpty()) {
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
                return;
            }

            double price;
            try {
                price = Double.parseDouble(priceStr);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Invalid price", Toast.LENGTH_SHORT).show();
                return;
            }

            // Determine if we're updating with or without image
            if (selectedAssetImageName != null) {
                // Update with new image
                updateProductWithImage(product.getProductId(), name, description, price,
                        selectedAvailability, selectedCategory.getCategoryId(), dialog);
            } else {
                // Update without changing image
                updateProductWithoutImage(product.getProductId(), name, description, price,
                        selectedAvailability, selectedCategory.getCategoryId(), dialog);
            }
        });

        dialog.show();
    }


    private void loadProductImage(Long productId) {
        if (imagePreview == null) return;

        // Replace "YOUR_BASE_URL" with your actual base URL
        String imageUrl = "http://your-actual-ip-address:8080/Product/image/" + productId;

        // Or if you have a base URL constant in ApiClient:
        // String imageUrl = ApiClient.BASE_URL + "Product/image/" + productId;

        new Thread(() -> {
            try {
                java.net.URL url = new java.net.URL(imageUrl);
                Bitmap bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                runOnUiThread(() -> {
                    if (imagePreview != null) {
                        imagePreview.setImageBitmap(bitmap);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    if (imagePreview != null) {
                        imagePreview.setImageResource(R.drawable.ic_baseline_image_24);
                    }
                });
            }
        }).start();
    }

    private void updateProductWithoutImage(Long productId, String name, String description,
                                           double price, String availability, Long categoryId,
                                           AlertDialog dialog) {

        // Create updated product object
        Product updatedProduct = new Product();
        updatedProduct.setProductId(productId);
        updatedProduct.setProductName(name);
        updatedProduct.setProductDescription(description);
        updatedProduct.setProductPrice(price);
        updatedProduct.setStockAvailability(availability);

        Category category = new Category();
        category.setCategoryId(categoryId);
        updatedProduct.setCategory(category);

        apiService.updateProduct(updatedProduct).enqueue(new Callback<Product>() {
            @Override
            public void onResponse(Call<Product> call, Response<Product> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(AdminProductManagementActivity.this,
                            "Product updated successfully", Toast.LENGTH_SHORT).show();
                    loadProducts();
                    dialog.dismiss();
                } else {
                    Toast.makeText(AdminProductManagementActivity.this,
                            "Failed to update product: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Product> call, Throwable t) {
                Toast.makeText(AdminProductManagementActivity.this,
                        "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateProductWithImage(Long productId, String name, String description,
                                        double price, String availability, Long categoryId,
                                        AlertDialog dialog) {

        File file = getFileFromAsset(selectedAssetImageName);
        if (file == null || !file.exists()) {
            Toast.makeText(this, "Error loading image", Toast.LENGTH_SHORT).show();
            return;
        }

        RequestBody namePart = RequestBody.create(MediaType.parse("text/plain"), name);
        RequestBody descriptionPart = RequestBody.create(MediaType.parse("text/plain"), description);
        RequestBody pricePart = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(price));
        RequestBody availabilityPart = RequestBody.create(MediaType.parse("text/plain"), availability); // ✅ String is fine!
        RequestBody categoryIdPart = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(categoryId));

        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part imagePart = MultipartBody.Part.createFormData("image", file.getName(), requestFile);

        apiService.updateProductWithImage(productId, namePart, descriptionPart, pricePart,
                availabilityPart, categoryIdPart, imagePart).enqueue(new Callback<Product>() {
            @Override
            public void onResponse(Call<Product> call, Response<Product> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(AdminProductManagementActivity.this,
                            "Product updated successfully", Toast.LENGTH_SHORT).show();
                    loadProducts();
                    dialog.dismiss();
                } else {
                    Toast.makeText(AdminProductManagementActivity.this,
                            "Failed to update product: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Product> call, Throwable t) {
                Toast.makeText(AdminProductManagementActivity.this,
                        "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    // -------------------- ASSET SUPPORT --------------------

    private List<String> getImageListFromAssets() {
        List<String> imageList = new ArrayList<>();
        try {
            String[] files = getAssets().list("images");
            if (files != null) {
                for (String filename : files) {
                    if (isImageFile(filename)) {
                        imageList.add("images/" + filename);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return imageList;
    }

    private boolean isImageFile(String filename) {
        String lowerCase = filename.toLowerCase();
        return lowerCase.endsWith(".jpg") || lowerCase.endsWith(".jpeg") ||
                lowerCase.endsWith(".png") || lowerCase.endsWith(".bmp") ||
                lowerCase.endsWith(".webp");
    }

    private Bitmap getBitmapFromAssets(String fileName) {
        try {
            InputStream inputStream = getAssets().open(fileName);
            return BitmapFactory.decodeStream(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void showAssetImagePicker() {
        List<String> imageList = getImageListFromAssets();

        if (imageList.isEmpty()) {
            Toast.makeText(this, "No images found in assets folder", Toast.LENGTH_SHORT).show();
            return;
        }

        String[] displayNames = new String[imageList.size()];
        for (int i = 0; i < imageList.size(); i++) {
            String fullPath = imageList.get(i);
            displayNames[i] = fullPath.substring(fullPath.lastIndexOf("/") + 1);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Product Image");
        builder.setItems(displayNames, (dialog, which) -> {
            selectedAssetImageName = imageList.get(which);
            loadAssetImagePreview(selectedAssetImageName);
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void loadAssetImagePreview(String assetImageName) {
        if (imagePreview != null) {
            Bitmap bitmap = getBitmapFromAssets(assetImageName);
            if (bitmap != null) {
                imagePreview.setImageBitmap(bitmap);
            }
        }
    }

    private File getFileFromAsset(String assetImageName) {
        try {
            InputStream inputStream = getAssets().open(assetImageName);
            String extension = assetImageName.substring(assetImageName.lastIndexOf("."));
            File file = new File(getCacheDir(), "asset_" + System.currentTimeMillis() + extension);

            FileOutputStream outputStream = new FileOutputStream(file);
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            inputStream.close();
            outputStream.close();
            return file;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // -------------------- PRODUCT MANAGEMENT --------------------

    private void loadProducts() {
        apiService.getAllProducts().enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    productList = response.body();

                    Log.d("AdminProduct", "=== PRODUCTS LOADED ===");
                    Log.d("AdminProduct", "Total products: " + productList.size());

                    // Log each product for debugging
                    for (int i = 0; i < productList.size(); i++) {
                        Product product = productList.get(i);
                        Log.d("AdminProduct", "Product " + i + ": " + product.getProductName() +
                                " (ID: " + product.getProductId() + ")");
                    }

                    adapter.setProducts(productList);

                    // Debug adapter state
                    Log.d("AdminProduct", "Adapter item count: " + adapter.getItemCount());
                    Log.d("AdminProduct", "RecyclerView child count: " + recyclerView.getChildCount());

                } else {
                    Log.e("AdminProduct", "Failed to load products. Code: " + response.code());
                    Toast.makeText(AdminProductManagementActivity.this,
                            "Failed to load products", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Log.e("AdminProduct", "Error loading products: " + t.getMessage());
                Toast.makeText(AdminProductManagementActivity.this,
                        "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteProduct(Product product) {
        apiService.deleteProduct(product.getProductId()).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AdminProductManagementActivity.this,
                            "Deleted " + product.getProductName(), Toast.LENGTH_SHORT).show();
                    loadProducts();
                } else {
                    Toast.makeText(AdminProductManagementActivity.this,
                            "Delete failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(AdminProductManagementActivity.this,
                        "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showAddProductDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_product_form, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        EditText inputName = dialogView.findViewById(R.id.inputProductName);
        EditText inputPrice = dialogView.findViewById(R.id.inputProductPrice);
        EditText inputDescription = dialogView.findViewById(R.id.inputProductDescription);
        Spinner availabilitySpinner = dialogView.findViewById(R.id.spinnerAvailability);
        Spinner categorySpinner = dialogView.findViewById(R.id.spinnerCategory);
        Button saveButton = dialogView.findViewById(R.id.saveButton);
        Button selectImageButton = dialogView.findViewById(R.id.selectImageButton);
        imagePreview = dialogView.findViewById(R.id.imagePreview);

        // Reset selections
        selectedAssetImageName = null;

        //  SET UP AVAILABILITY SPINNER WITH DEFAULT SELECTION
        String[] availabilityOptions = {"AVAILABLE", "LOW_STOCK", "OUT_OF_STOCK"};
        ArrayAdapter<String> availabilityAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, availabilityOptions);
        availabilityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        availabilitySpinner.setAdapter(availabilityAdapter);
        availabilitySpinner.setSelection(0);

        // SET UP CATEGORY SPINNER WITH PLACEHOLDER (IMMEDIATELY VISIBLE)
        List<Category> tempList = new ArrayList<>();
        tempList.add(new Category(0L, "Loading categories...", ""));

        ArrayAdapter<Category> tempAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, tempList) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView textView = (TextView) super.getView(position, convertView, parent);
                textView.setText(tempList.get(position).getName());
                return textView;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                TextView textView = (TextView) super.getDropDownView(position, convertView, parent);
                textView.setText(tempList.get(position).getName());
                return textView;
            }
        };
        tempAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(tempAdapter);

        // NOW LOAD REAL CATEGORIES IN BACKGROUND
        categoryService.getAllCategories().enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    categoryList = response.body();

                    ArrayAdapter<Category> realAdapter = new ArrayAdapter<>(AdminProductManagementActivity.this,
                            android.R.layout.simple_spinner_item, categoryList) {
                        @Override
                        public View getView(int position, View convertView, ViewGroup parent) {
                            TextView textView = (TextView) super.getView(position, convertView, parent);
                            textView.setText(categoryList.get(position).getName());
                            return textView;
                        }

                        @Override
                        public View getDropDownView(int position, View convertView, ViewGroup parent) {
                            TextView textView = (TextView) super.getDropDownView(position, convertView, parent);
                            textView.setText(categoryList.get(position).getName());
                            return textView;
                        }
                    };
                    realAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    categorySpinner.setAdapter(realAdapter);

                    // AUTO-SELECT FIRST CATEGORY TO PREVENT NULL
                    categorySpinner.setSelection(0);

                    Log.d("CategoryDebug", "Categories loaded: " + categoryList.size());

                } else {
                    // Show error in spinner
                    List<Category> errorList = new ArrayList<>();
                    errorList.add(new Category(0L, "Failed to load categories", ""));
                    ArrayAdapter<Category> errorAdapter = new ArrayAdapter<>(AdminProductManagementActivity.this,
                            android.R.layout.simple_spinner_item, errorList);
                    categorySpinner.setAdapter(errorAdapter);
                    Log.e("CategoryDebug", "Failed to load categories: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {
                List<Category> errorList = new ArrayList<>();
                errorList.add(new Category(0L, "Network error", ""));
                ArrayAdapter<Category> errorAdapter = new ArrayAdapter<>(AdminProductManagementActivity.this,
                        android.R.layout.simple_spinner_item, errorList);
                categorySpinner.setAdapter(errorAdapter);
                Log.e("CategoryDebug", "Network error: " + t.getMessage());
            }
        });

        selectImageButton.setOnClickListener(v -> showAssetImagePicker());

        //  FIXED SAVE BUTTON WITH COMPREHENSIVE VALIDATION
        saveButton.setOnClickListener(v -> {
            String name = inputName.getText().toString().trim();
            String priceStr = inputPrice.getText().toString().trim();
            String description = inputDescription.getText().toString().trim();

            // VALIDATE BASIC FIELDS
            if (name.isEmpty()) {
                Toast.makeText(this, "Product name is required", Toast.LENGTH_SHORT).show();
                inputName.requestFocus();
                return;
            }

            if (priceStr.isEmpty()) {
                Toast.makeText(this, "Price is required", Toast.LENGTH_SHORT).show();
                inputPrice.requestFocus();
                return;
            }

            if (description.isEmpty()) {
                Toast.makeText(this, "Description is required", Toast.LENGTH_SHORT).show();
                inputDescription.requestFocus();
                return;
            }

            // VALIDATE PRICE
            double price;
            try {
                price = Double.parseDouble(priceStr);
                if (price <= 0) {
                    Toast.makeText(this, "Price must be greater than 0", Toast.LENGTH_SHORT).show();
                    inputPrice.requestFocus();
                    return;
                }
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Invalid price format", Toast.LENGTH_SHORT).show();
                inputPrice.requestFocus();
                return;
            }

            // VALIDATE AVAILABILITY
            if (availabilitySpinner.getSelectedItem() == null) {
                Toast.makeText(this, "Please select availability", Toast.LENGTH_SHORT).show();
                return;
            }
            String selectedAvailability = availabilitySpinner.getSelectedItem().toString();

            // CRITICAL: VALIDATE CATEGORY SELECTION
            if (categorySpinner.getSelectedItem() == null) {
                Log.e("SaveProduct", "Category spinner has no selection");
                Toast.makeText(this, "Please select a category", Toast.LENGTH_SHORT).show();
                return;
            }

            Object selectedItem = categorySpinner.getSelectedItem();
            if (!(selectedItem instanceof Category)) {
                Log.e("SaveProduct", "Selected item is not a Category: " + selectedItem.getClass().getSimpleName());
                Toast.makeText(this, "Invalid category selection", Toast.LENGTH_SHORT).show();
                return;
            }

            Category selectedCategory = (Category) selectedItem;

            // EXTRA SAFETY: Check if category has valid ID
            if (selectedCategory.getCategoryId() == null || selectedCategory.getCategoryId() == 0L) {
                Log.e("SaveProduct", "Selected category has invalid ID: " + selectedCategory.getName());
                Toast.makeText(this, "Please select a valid category", Toast.LENGTH_SHORT).show();
                return;
            }

            Log.d("SaveProduct", "Attempting to save product with category: " +
                    selectedCategory.getName() + " (ID: " + selectedCategory.getCategoryId() + ")");

            // PREPARE REQUEST PARTS
            RequestBody namePart = RequestBody.create(MediaType.parse("text/plain"), name);
            RequestBody descriptionPart = RequestBody.create(MediaType.parse("text/plain"), description);
            RequestBody pricePart = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(price));
            RequestBody availabilityPart = RequestBody.create(MediaType.parse("text/plain"), selectedAvailability);
            RequestBody categoryIdPart = RequestBody.create(MediaType.parse("text/plain"),
                    String.valueOf(selectedCategory.getCategoryId()));

            //  HANDLE IMAGE
            MultipartBody.Part imagePart = null;
            if (selectedAssetImageName != null) {
                File file = getFileFromAsset(selectedAssetImageName);
                if (file != null && file.exists()) {
                    RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
                    imagePart = MultipartBody.Part.createFormData("image", file.getName(), requestFile);
                    Log.d("SaveProduct", "Image included: " + selectedAssetImageName);
                }
            } else {
                Log.d("SaveProduct", "No image selected");
            }

            // MAKE API CALL
            Call<Product> call;
            if (imagePart != null) {
                call = apiService.createProductWithImage(
                        namePart, descriptionPart, pricePart,
                        availabilityPart, categoryIdPart, imagePart
                );
                Log.d("SaveProduct", "Calling createProductWithImage");
            } else {
                // Fallback to without image if no image selected
                Product product = new Product();
                product.setProductName(name);
                product.setProductDescription(description);
                product.setProductPrice(price);
                product.setStockAvailability(selectedAvailability);

                Category cat = new Category();
                cat.setCategoryId(selectedCategory.getCategoryId());
                product.setCategory(cat);

                call = apiService.createProduct(product);
                Log.d("SaveProduct", "Calling createProduct (no image)");
            }

            call.enqueue(new Callback<Product>() {
                @Override
                public void onResponse(Call<Product> call, Response<Product> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Log.d("SaveProduct", "Product created successfully: " + response.body().getProductName());
                        Toast.makeText(AdminProductManagementActivity.this,
                                "Product added successfully!", Toast.LENGTH_SHORT).show();
                        loadProducts();
                        dialog.dismiss();
                    } else {
                        Log.e("SaveProduct", "Failed to create product. Code: " + response.code());
                        try {
                            String errorBody = response.errorBody() != null ? response.errorBody().string() : "No error body";
                            Log.e("SaveProduct", "Error response: " + errorBody);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Toast.makeText(AdminProductManagementActivity.this,
                                "Failed to add product. Error: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Product> call, Throwable t) {
                    Log.e("SaveProduct", "Network error: " + t.getMessage(), t);
                    Toast.makeText(AdminProductManagementActivity.this,
                            "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        dialog.show();
    }
}
