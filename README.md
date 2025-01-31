# SynchronyUserProfile

# Features
  User Registration: Register a user with basic information (username and password).
  Image Upload: Upload images to Imgur via the Imgur API and associate the images with the user’s profile.
  Image Deletion: Delete images uploaded by the user on Imgur.
  View User Profile: View the basic user information and the associated images.
  Authentication: Username and password authentication for accessing image upload/view/delete functionalities.


# Technologies Used
  Spring Boot 3.x.x
  Java 17 (JDK 17)
  H2 Database (in-memory)
  JPA (Java Persistence API) for ORM
  Imgur API for image upload, view, and delete operations
  JUnit for testing

# Imgur Integration
  # API Endpoints
    # Register User:

   POST /users/register
   Registers a user with basic information (username and password).
	 
   # Login User:

   POST /users/login
   Authenticates the user with username and password.
   Upload Image:

  POST /users/upload-image
  Uploads an image to Imgur (requires authentication).

  Delete Image:

  DELETE /users/images/{imageId}
  Deletes an image from Imgur (requires authentication).
