//package com.enigma.wmb_api.controller;
//
//import com.enigma.wmb_api.constant.Constant;
//import com.enigma.wmb_api.dto.request.MenuRequest;
//import com.enigma.wmb_api.dto.response.CommonResponse;
//import com.enigma.wmb_api.dto.response.MenuResponse;
//import com.enigma.wmb_api.service.MenuService;
//import com.fasterxml.jackson.core.type.TypeReference;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import jakarta.validation.ConstraintViolationException;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.data.domain.PageImpl;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import org.springframework.security.test.context.support.WithMockUser;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
//import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
//import org.springframework.web.server.ResponseStatusException;
//
//import java.util.List;
//
//@SpringBootTest
//@AutoConfigureMockMvc
//class MenuControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @MockBean
//    private MenuService menuService;
//
//    @Test
//    @WithMockUser(username = "budi", roles = {"ADMIN"})
//    void shouldReturn201WhenCreateNewMenu() throws Exception {
//        MenuRequest request = new MenuRequest();
//        request.setName("Bajigur");
//        request.setPrice(2000L);
//        request.setCategory("Minuman");
//
//        String requestBodyJSON = objectMapper.writeValueAsString(request);
//
//        MenuResponse expectedResponse = new MenuResponse();
//        expectedResponse.setId("menu-1");
//        expectedResponse.setName(request.getName());
//        expectedResponse.setPrice(request.getPrice());
//        expectedResponse.setCategory(request.getCategory());
//
//        Mockito.when(menuService.create(Mockito.any()))
//                .thenReturn(expectedResponse);
//
//        mockMvc.perform(MockMvcRequestBuilders.post(Constant.MENU_API)
//                        .content(requestBodyJSON)
//                        .contentType(MediaType.APPLICATION_JSON_VALUE))
//                .andExpect(MockMvcResultMatchers.status().isCreated())
//                .andDo(result -> {
//                    String responseJSON = result.getResponse().getContentAsString();
//                    // type reference menyesuaikan tipe data variable yg dibuat agar bisa mendapatkan tipe data generic, contohnya CommonResponse
//                    // dibawah ini tidak bisa, jadi biar flexible pakai TypeReference
//                    // MenuResponse response = objectMapper.readValue(responseJSON, Menu.class);
//                    CommonResponse<MenuResponse> response = objectMapper.readValue(responseJSON, new TypeReference<>() {
//                    });
//                    Assertions.assertEquals(HttpStatus.CREATED.value(), response.getStatus());
//                    Assertions.assertNotNull(response.getData());
//                });
//    }
//
//    @Test
//    @WithMockUser(username = "budi", roles = "CUSTOMER")
//    void shouldReturn200WhenGetAllMenu() throws Exception {
//        List<MenuResponse> menus = List.of(
//                new MenuResponse("1", "Bajigur", 2000L, "Minuman"),
//                new MenuResponse("2", "Burger", 5000L, "Makanan")
//        );
//        PageImpl<MenuResponse> mockResponses = new PageImpl<>(
//                menus,
//                PageRequest.of(1, 10),
//                menus.size()
//        );
//
//        Mockito.when(menuService.getAll(Mockito.any()))
//                .thenReturn(mockResponses);
//
//        mockMvc.perform(
//                        MockMvcRequestBuilders.get(Constant.MENU_API)
//                                .requestAttr("page", "1")
//                                .requestAttr("size", "10")
//                ).andExpect(MockMvcResultMatchers.status().isOk())
//                .andDo(result -> {
//                    String jsonResponse = result.getResponse().getContentAsString();
//                    CommonResponse<List<MenuResponse>> response = objectMapper.readValue(jsonResponse, new TypeReference<>() {
//                    });
//                    Assertions.assertEquals(HttpStatus.OK.value(), response.getStatus());
//                    Assertions.assertEquals(2, response.getData().size());
//                });
//    }
//
//    @Test
//    @WithMockUser(username = "budi", roles = {"CUSTOMER"})
//    void shouldReturn200WhenGetById() throws Exception {
//        String id = "menu-1";
//
//        MenuResponse expected = new MenuResponse();
//        expected.setId(id);
//        expected.setName("Burger");
//        expected.setPrice(5000L);
//        expected.setCategory("Makanan");
//
//        Mockito.when(menuService.getById(id))
//                .thenReturn(expected);
//
//        mockMvc.perform(MockMvcRequestBuilders.get(Constant.MENU_API + "/" + id))
//                .andExpect(MockMvcResultMatchers.status().isOk())
//                .andDo(result -> {
//                    String jsonResponse = result.getResponse().getContentAsString();
//                    CommonResponse<MenuResponse> response = objectMapper.readValue(jsonResponse, new TypeReference<>() {
//                    });
//                    Assertions.assertEquals(HttpStatus.OK.value(), response.getStatus());
//                    Assertions.assertNotNull(response.getData());
//                });
//    }
//
//    @Test
//    @WithMockUser(username = "admin", roles = {"ADMIN"})
//    void shouldReturn200WhenUpdate() throws Exception {
//        MenuRequest request = new MenuRequest("Burger", 5000L, "Makanan");
//        String jsonRequest = objectMapper.writeValueAsString(request);
//
//        MenuResponse expected = new MenuResponse("1", "Burger", 5000L, "Makanan");
//
//        Mockito.when(menuService.update(Mockito.anyString(), Mockito.any()))
//                .thenReturn(expected);
//
//        mockMvc.perform(MockMvcRequestBuilders.put(Constant.MENU_API + "/" + "1")
//                        .content(jsonRequest)
//                        .contentType(MediaType.APPLICATION_JSON_VALUE)
//                ).andExpect(MockMvcResultMatchers.status().isOk())
//                .andDo(result -> {
//                    String jsonResponse = result.getResponse().getContentAsString();
//                    CommonResponse<MenuResponse> response = objectMapper.readValue(jsonResponse, new TypeReference<>() {
//                    });
//                    Assertions.assertEquals(HttpStatus.OK.value(), response.getStatus());
//                    Assertions.assertNotNull(response.getData());
//                });
//    }
//
//    @Test
//    @WithMockUser(username = "admin", roles = {"ADMIN"})
//    void shouldReturn200WhenDeleteById() throws Exception {
//        Mockito.doNothing().when(menuService).deleteById("1");
//        mockMvc.perform(MockMvcRequestBuilders.delete(Constant.MENU_API + "/" + "1")
//                ).andExpect(MockMvcResultMatchers.status().isOk())
//                .andDo(result -> {
//                    String jsonResponse = result.getResponse().getContentAsString();
//                    CommonResponse<MenuResponse> response = objectMapper.readValue(jsonResponse, new TypeReference<>() {
//                    });
//                    Assertions.assertEquals(HttpStatus.OK.value(), response.getStatus());
//                    Assertions.assertEquals(Constant.SUCCESS_DELETE_MENU, response.getMessage());
//                    Assertions.assertNull(response.getData());
//                });
//    }
//
//    @Test
//    @WithMockUser(username = "admin", roles = {"ADMIN"})
//    void shouldReturn400WhenCreateMenu() throws Exception {
//        Mockito.when(menuService.create(Mockito.any())).thenThrow(ConstraintViolationException.class);
//
//        MenuRequest request = new MenuRequest();
//        String jsonReq = objectMapper.writeValueAsString(request);
//
//        mockMvc.perform(
//                MockMvcRequestBuilders
//                        .post(Constant.MENU_API)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(jsonReq)
//        ).andExpect(MockMvcResultMatchers.status().isBadRequest());
//    }
//
//    @Test
//    @WithMockUser(username = "budi", roles = {"CUSTOMER"})
//    void shouldReturn404WhenGetById() throws Exception {
//        Mockito.when(menuService.getById("asal")).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));
//
//        mockMvc.perform(
//                MockMvcRequestBuilders
//                        .get(Constant.MENU_API + "/" + "asal")
//        ).andExpect(MockMvcResultMatchers.status().isNotFound());
//    }
//}