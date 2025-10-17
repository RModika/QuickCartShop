package za.ac.cput.ui.product;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import com.google.gson.Gson;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import java.io.InputStream;
import java.io.IOException;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import za.ac.cput.R;
import za.ac.cput.model.Category;
import za.ac.cput.model.Review;
import za.ac.cput.model.User;
import za.ac.cput.services.ApiClient;
import za.ac.cput.services.CategoryApiService;
import za.ac.cput.services.ReviewApiService;

public class ReviewProductsActivity extends AppCompatActivity {

    private Spinner categorySpinner;
    private EditText reviewEditText;
    private Button submitButton;

    private CategoryApiService categoryApiService;
    private ReviewApiService reviewApiService;
    private List<Category> categoryList = new ArrayList<>();

    private Long loggedInUserId;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri selectedImageUri;
    private ImageView selectedImageView;
    private Button selectImageButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_products);

        categorySpinner = findViewById(R.id.categorySpinner);
        reviewEditText = findViewById(R.id.reviewEditText);
        submitButton = findViewById(R.id.submitReviewButton);

        selectedImageView = findViewById(R.id.selectedImage);
        selectImageButton = findViewById(R.id.selectImageButton);

        categoryApiService = ApiClient.getCategoryApiService(this);
        reviewApiService = ApiClient.getReviewApiService(this);

        // Get user ID from SharedPreferences
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        loggedInUserId = prefs.getLong("userId", -1L);

        loadCategories();

        submitButton.setOnClickListener(v -> submitReview());

        selectImageButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            selectedImageView.setImageURI(selectedImageUri);
        }
    }

    private void loadCategories() {
        categoryApiService.getAllCategories().enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    categoryList = response.body();
                    categoryList.add(new Category(-1L, "Others", "User-defined category"));

                    ArrayAdapter<Category> adapter = new ArrayAdapter<>(
                            ReviewProductsActivity.this,
                            android.R.layout.simple_spinner_item,
                            categoryList
                    );
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    categorySpinner.setAdapter(adapter);
                } else {
                    Toast.makeText(ReviewProductsActivity.this, "Failed to load categories", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {
                Toast.makeText(ReviewProductsActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void submitReview() {
        String reviewText = reviewEditText.getText().toString().trim();
        if (reviewText.isEmpty()) {
            Toast.makeText(this, "Please write your review", Toast.LENGTH_SHORT).show();
            return;
        }

        Category selectedCategory = (Category) categorySpinner.getSelectedItem();

        Review review = new Review();
        review.setContent(reviewText);

        // Category
        if (selectedCategory.getCategoryId() == -1L) {
            review.setCategoryNameIfOther("Other");
        } else {
            review.setCategoryId(selectedCategory.getCategoryId());
        }

        // User
        User user = new User();
        user.setUserId(loggedInUserId);
        review.setUser(user);

        // Step 1: Convert Review object to JSON
        Gson gson = new Gson();
        String reviewJsonString = gson.toJson(review);

// Step 2: Create 'review' part as RequestBody
//        RequestBody reviewRequestBody = RequestBody.create(
//                reviewJsonString,
//                MediaType.parse("text/plain") // or "application/json" also works
//        );
        RequestBody reviewRequestBody = RequestBody.create(
                reviewJsonString,
                MediaType.parse("application/json")
        );


// Step 3: Create 'image' part (optional)
        MultipartBody.Part imagePart = null;

        if (selectedImageUri != null) {
            try {
                InputStream inputStream = getContentResolver().openInputStream(selectedImageUri);
                byte[] imageBytes = toByteArray(inputStream);

                RequestBody imageBody = RequestBody.create(
                        imageBytes,
                        MediaType.parse(getContentResolver().getType(selectedImageUri))
                );

                String fileName = "review_" + System.currentTimeMillis() + ".jpg";
                imagePart = MultipartBody.Part.createFormData("image", fileName, imageBody);

            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Image upload failed", Toast.LENGTH_SHORT).show();
                return;
            }
        }

// Step 4: Call the API
        Call<Review> call = reviewApiService.createReviewWithImage(reviewRequestBody, imagePart);

        call.enqueue(new Callback<Review>() {
            @Override
            public void onResponse(Call<Review> call, Response<Review> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ReviewProductsActivity.this, "Review submitted!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(ReviewProductsActivity.this, "Submission failed: " + response.code(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Review> call, Throwable t) {
                Toast.makeText(ReviewProductsActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });


    }

    // ✅ Moved here — outside onCreate()
    private byte[] toByteArray(InputStream input) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int nRead;
        byte[] data = new byte[16384];
        while ((nRead = input.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }
        buffer.flush();
        return buffer.toByteArray();
    }

}
