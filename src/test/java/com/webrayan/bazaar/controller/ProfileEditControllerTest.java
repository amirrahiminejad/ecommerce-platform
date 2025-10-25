//package com.webrayan.bazaar.controller;
//
//import com.webrayan.bazaar.modules.acl.entity.User;
//import com.webrayan.bazaar.modules.acl.service.UserService;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureTestMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.security.test.context.support.WithMockUser;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.web.servlet.MockMvc;
//
//import java.util.Optional;
//
//import static org.mockito.ArgumentMatchers.*;
//import static org.mockito.Mockito.when;
//import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@SpringBootTest
//@AutoConfigureTestMockMvc
//@ActiveProfiles("test")
//public class ProfileEditControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @MockBean
//    private UserService userService;
//
//    @Test
//    @WithMockUser(username = "testuser")
//    public void testEditProfilePageLoad() throws Exception {
//        // Setup mock user
//        User mockUser = new User();
//        mockUser.setId(1L);
//        mockUser.setUsername("testuser");
//        mockUser.setEmail("test@example.com");
//        mockUser.setFirstName("Test");
//        mockUser.setLastName("User");
//        mockUser.setLinkdin("https://linkedin.com/in/testuser");
//        mockUser.setFacebook("https://facebook.com/testuser");
//        mockUser.setInstagram("https://instagram.com/testuser");
//
//        when(userService.getUserByUsername("testuser"))
//            .thenReturn(Optional.of(mockUser));
//
//        mockMvc.perform(get("/profile/edit"))
//            .andExpect(status().isOk())
//            .andExpect(view().name("profile/edit"))
//            .andExpect(model().attributeExists("user"))
//            .andExpect(model().attribute("user", mockUser));
//    }
//
//    @Test
//    @WithMockUser(username = "testuser")
//    public void testUpdateProfileWithSocialMedia() throws Exception {
//        // Setup mock user
//        User mockUser = new User();
//        mockUser.setId(1L);
//        mockUser.setUsername("testuser");
//        mockUser.setEmail("test@example.com");
//
//        when(userService.getUserByUsername("testuser"))
//            .thenReturn(Optional.of(mockUser));
//
//        when(userService.updateUser(anyLong(), anyString(), anyString(), anyString(), anyString()))
//            .thenReturn(mockUser);
//
//        when(userService.updateSocialMediaLinks(anyLong(), anyString(), anyString(), anyString()))
//            .thenReturn(mockUser);
//
//        mockMvc.perform(post("/profile/update")
//                .with(csrf())
//                .param("firstName", "Updated")
//                .param("lastName", "User")
//                .param("email", "updated@example.com")
//                .param("phoneNumber", "09123456789")
//                .param("linkdin", "https://linkedin.com/in/updateduser")
//                .param("facebook", "https://facebook.com/updateduser")
//                .param("instagram", "https://instagram.com/updateduser"))
//            .andExpect(status().is3xxRedirection())
//            .andExpect(redirectedUrl("/profile"));
//    }
//
//    @Test
//    @WithMockUser(username = "testuser")
//    public void testUpdateProfileWithEmptySocialMedia() throws Exception {
//        // Setup mock user
//        User mockUser = new User();
//        mockUser.setId(1L);
//        mockUser.setUsername("testuser");
//        mockUser.setEmail("test@example.com");
//
//        when(userService.getUserByUsername("testuser"))
//            .thenReturn(Optional.of(mockUser));
//
//        when(userService.updateUser(anyLong(), anyString(), anyString(), anyString(), anyString()))
//            .thenReturn(mockUser);
//
//        when(userService.updateSocialMediaLinks(anyLong(), anyString(), anyString(), anyString()))
//            .thenReturn(mockUser);
//
//        mockMvc.perform(post("/profile/update")
//                .with(csrf())
//                .param("firstName", "Test")
//                .param("lastName", "User")
//                .param("email", "test@example.com")
//                .param("phoneNumber", "09123456789")
//                .param("linkdin", "")
//                .param("facebook", "")
//                .param("instagram", ""))
//            .andExpect(status().is3xxRedirection())
//            .andExpect(redirectedUrl("/profile"));
//    }
//}
