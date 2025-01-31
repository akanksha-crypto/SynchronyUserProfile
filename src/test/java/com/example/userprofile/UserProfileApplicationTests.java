package com.example.userprofile;

import com.example.userprofile.Controller.UserController;
import com.example.userprofile.Models.User;
import com.example.userprofile.Repository.UserRepo;
import com.example.userprofile.Service.ImgurService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.*;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest
class UserProfileApplicationTests {

	@Test
	void contextLoads() {
	}

	@Mock
	private RestTemplate restTemplate;

	@InjectMocks
	private ImgurService imgurService;

	private final String CLIENT_ID = "test-client-id";

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		imgurService = new ImgurService(restTemplate);
	}

	@Test
	void testUploadImage_Success() throws Exception {
		MultipartFile mockFile = mock(MultipartFile.class);
		String apiUrl = "https://api.imgur.com/3/upload";

		Map<String, Object> responseBody = new HashMap<>();
		Map<String, Object> data = new HashMap<>();
		data.put("link", "https://i.imgur.com/testImage.jpg");
		responseBody.put("data", data);

		ResponseEntity<Map> responseEntity = new ResponseEntity<>(responseBody, HttpStatus.OK);

		when(restTemplate.exchange(eq(apiUrl), eq(HttpMethod.POST), any(HttpEntity.class), eq(Map.class)))
				.thenReturn(responseEntity);

		String result = imgurService.uploadImage(mockFile);

		assertNotNull(result);
		assertEquals("https://i.imgur.com/testImage.jpg", result);
	}

	@Test
	void testUploadImage_Failure() {
		MultipartFile mockFile = mock(MultipartFile.class);
		String apiUrl = "https://api.imgur.com/3/upload";

		ResponseEntity<Map> responseEntity = new ResponseEntity<>(HttpStatus.BAD_REQUEST);

		when(restTemplate.exchange(eq(apiUrl), eq(HttpMethod.POST), any(HttpEntity.class), eq(Map.class)))
				.thenReturn(responseEntity);

		Exception exception = assertThrows(Exception.class, () -> imgurService.uploadImage(mockFile));
		assertEquals("Image upload failed.", exception.getMessage());
	}

	@Mock
	private UserRepo userRepository;

	@InjectMocks
	private UserController userController;

	private MockMvc mockMvc;
	private ObjectMapper objectMapper;

	@BeforeEach
	void setUp1() {
		MockitoAnnotations.openMocks(this);
		mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
		objectMapper = new ObjectMapper();
	}

	@Test
	void testUploadImage_UserNotFound() throws Exception {
		when(userRepository.findById("testuser")).thenReturn(java.util.Optional.empty());

		mockMvc.perform(multipart("/api/users/testuser/uploadImage")
						.file("file", "test".getBytes()))
				.andExpect(status().isNotFound())
				.andExpect(content().string("User not found."));
	}

	@Test
	void testGetUserProfile_Success() throws Exception {
		User user = new User();
		user.setUsername("testuser");
		user.setImageLink("https://i.imgur.com/testImage.jpg");

		when(userRepository.findById("testuser")).thenReturn(java.util.Optional.of(user));

		mockMvc.perform(get("/api/users/testuser/profile"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.username").value("testuser"))
				.andExpect(jsonPath("$.imageUrl").value("https://i.imgur.com/testImage.jpg"));
	}

	@Test
	void testGetUserProfile_NotFound() throws Exception {
		when(userRepository.findById("testuser")).thenReturn(java.util.Optional.empty());

		mockMvc.perform(get("/api/users/testuser/profile"))
				.andExpect(status().isNotFound());
	}
}
