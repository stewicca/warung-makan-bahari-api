//package com.enigma.wmb_api.service.impl;
//
//import com.enigma.wmb_api.constant.MenuCategory;
//import com.enigma.wmb_api.dto.request.MenuRequest;
//import com.enigma.wmb_api.dto.request.SearchMenuRequest;
//import com.enigma.wmb_api.dto.response.MenuResponse;
//import com.enigma.wmb_api.entity.Menu;
//import com.enigma.wmb_api.repository.MenuRepository;
//import com.enigma.wmb_api.util.ValidationUtil;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageImpl;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.jpa.domain.Specification;
//import org.springframework.web.server.ResponseStatusException;
//
//import java.util.List;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//
//@ExtendWith(MockitoExtension.class)
//class MenuServiceImplTest {
//    @Mock
//    private MenuRepository menuRepository;
//
//    @Mock
//    private ValidationUtil validationUtil;
//
//    @InjectMocks
//    private MenuServiceImpl menuService;
//
//    @Test
//    void shouldReturnMenuResponseWhenCreate() {
//        MenuRequest request = MenuRequest.builder()
//                .name("Pisang Goreng")
//                .price(3000L)
//                .category("Makanan")
//                .build();
//
//        Mockito.doNothing().when(validationUtil).validate(request);
//
//        Menu mockMenu = Menu.builder()
//                .id("menu-1")
//                .name(request.getName())
//                .price(request.getPrice())
//                .category(MenuCategory.FOOD)
//                .build();
//
//        Mockito.when(menuRepository.saveAndFlush(Mockito.any(Menu.class)))
//                .thenReturn(mockMenu);
//
//        MenuResponse menuResponse = menuService.create(request);
//
//        assertEquals(request.getName(), menuResponse.getName());
//        assertEquals(request.getPrice(), menuResponse.getPrice());
//        assertEquals(request.getCategory(), menuResponse.getCategory());
//
//        Mockito.verify(
//                validationUtil,
//                Mockito.times(1)
//        ).validate(request);
//        Mockito.verify(
//                menuRepository,
//                Mockito.times(1)
//        ).saveAndFlush(Mockito.any(Menu.class));
//    }
//
//    @Test
//    void shouldReturnMenuResponseWhenGetById() {
//        String id = "menu-1";
//
//        // seolah olah dapat dari DB
//        Menu menu = Menu.builder()
//                .id("menu-1")
//                .name("Pisang Goreng")
//                .price(3000L)
//                .category(MenuCategory.FOOD)
//                .build();
//
//        Mockito.when(menuRepository.findById(id))
//                .thenReturn(Optional.of(menu));
//
//        MenuResponse menuResponse = menuService.getById(id);
//
//        assertEquals(menu.getId(), menuResponse.getId());
//        assertEquals(menu.getName(), menuResponse.getName());
//        assertEquals(menu.getPrice(), menuResponse.getPrice());
//        assertEquals("Makanan", menuResponse.getCategory());
//
//        Mockito.verify(
//                menuRepository,
//                Mockito.times(1)
//        ).findById(id);
//    }
//
//    @Test
//    void shouldThrowNotFoundWhenGetById() {
//        String id = "menu-not-found";
//
//        Mockito.when(menuRepository.findById(id))
//                .thenReturn(Optional.empty());
//
//        ResponseStatusException exception = assertThrows(
//                ResponseStatusException.class,
//                () -> menuService.getById(id)
//        );
//
//        assertEquals("data tidak ditemukan", exception.getReason());
//
//        Mockito.verify(
//                menuRepository,
//                Mockito.times(1)
//        ).findById(id);
//    }
//
//    @Test
//    void shouldReturnPageOfMenuResponseWhenGetAll() {
//        SearchMenuRequest request = new SearchMenuRequest();
//        request.setPage(1);
//        request.setSize(10);
//        request.setQuery("bajigur");
//        request.setMinPrice(1000L);
//        request.setMaxPrice(3000L);
//
//        // seolah olah data di DB
//        Menu menu1 = new Menu("menu-1", "Bajigur", 2000L, MenuCategory.BEVERAGE);
//        Menu menu2 = new Menu("menu-2", "Bajigur 2", 2000L, MenuCategory.BEVERAGE);
//
//        PageImpl<Menu> menus = new PageImpl<>(List.of(menu1, menu2));
//
//        Mockito.when(menuRepository.findAll(
//                Mockito.any(Specification.class),
//                Mockito.any(Pageable.class)
//        )).thenReturn(menus);
//
//        Page<MenuResponse> menuResponsePage = menuService.getAll(request);
//
//        assertEquals(2, menuResponsePage.getTotalElements());
//        Mockito.verify(
//                menuRepository,
//                Mockito.times(1)
//        ).findAll(
//                Mockito.any(Specification.class),
//                Mockito.any(Pageable.class)
//        );
//    }
//}