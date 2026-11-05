package com.smartshelf.services;

import com.smartshelf.dto.HandwrittenListParseResponse;
import com.smartshelf.dto.ParsedListItemDTO;
import com.smartshelf.models.Category;
import com.smartshelf.models.Product;
import com.smartshelf.repositories.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class GeminiAiServiceTest {

    private ProductRepository productRepository;
    private GeminiAiService geminiAiService;

    @BeforeEach
    void setUp() {
        productRepository = Mockito.mock(ProductRepository.class);
        geminiAiService = new GeminiAiService(productRepository);

        Category grains = Category.builder().name("Grains & Flours").build();
        Category dairy = Category.builder().name("Dairy & Eggs").build();
        Category spices = Category.builder().name("Spices & Masala").build();
        Category oils = Category.builder().name("Cooking Oils & Ghee").build();
        Category cleaning = Category.builder().name("Cleaning & Household").build();

        List<Product> catalog = Arrays.asList(
                Product.builder().id(1L).name("India Gate Basmati Rice 5kg").sku("SKU-RICE-001").sellingPrice(new BigDecimal("510.00")).currentStock(45).unit("bag").category(grains).build(),
                Product.builder().id(2L).name("Aashirvaad Superior MP Sharbati Atta 10kg").sku("SKU-ATTA-002").sellingPrice(new BigDecimal("460.00")).currentStock(30).unit("bag").category(grains).build(),
                Product.builder().id(3L).name("Tata Salt Vacuum Evaporated 1kg").sku("SKU-SALT-003").sellingPrice(new BigDecimal("28.00")).currentStock(120).unit("pkt").category(spices).build(),
                Product.builder().id(4L).name("Amul Butter Pasteurized 500g").sku("SKU-BTR-004").sellingPrice(new BigDecimal("275.00")).currentStock(10).unit("pack").category(dairy).build(),
                Product.builder().id(5L).name("Amul Taaza Homogenised Toned Milk 1L").sku("SKU-MILK-005").sellingPrice(new BigDecimal("74.00")).currentStock(15).unit("tetra").category(dairy).build(),
                Product.builder().id(6L).name("Fortune Sunlite Refined Sunflower Oil 1L").sku("SKU-OIL-006").sellingPrice(new BigDecimal("155.00")).currentStock(50).unit("pouch").category(oils).build(),
                Product.builder().id(7L).name("Surf Excel Easy Wash Detergent Powder 1kg").sku("SKU-SURF-010").sellingPrice(new BigDecimal("145.00")).currentStock(35).unit("bag").category(cleaning).build(),
                Product.builder().id(8L).name("Madhur Pure & Hygienic Sugar 1kg").sku("SKU-SUG-025").sellingPrice(new BigDecimal("52.00")).currentStock(80).unit("pkt").category(grains).build()
        );

        when(productRepository.findByActiveTrue()).thenReturn(catalog);
    }

    @Test
    void testParseDirectSlipTextWithQuantities() {
        String testSlip = "1. Basmati Rice 5kg\n" +
                "2. Aashirvaad Atta 10kg\n" +
                "3. Tata Salt 2 packets\n" +
                "4. Amul Butter 500g\n" +
                "5. Milk 3L\n" +
                "6. Sugar 2kg";

        HandwrittenListParseResponse response = geminiAiService.processHandwrittenSlipWithGemini(null, testSlip, null, null);

        assertNotNull(response);
        assertEquals(6, response.getTotalExtractedItems());
        assertEquals(6, response.getSuccessfullyMatchedItems());

        List<ParsedListItemDTO> items = response.getItems();
        assertEquals(1L, items.get(0).getMatchedProductId()); // Basmati Rice
        assertEquals(5, items.get(0).getExtractedQuantity());

        assertEquals(2L, items.get(1).getMatchedProductId()); // Aashirvaad Atta
        assertEquals(10, items.get(1).getExtractedQuantity());

        assertEquals(3L, items.get(2).getMatchedProductId()); // Tata Salt
        assertEquals(2, items.get(2).getExtractedQuantity());

        assertEquals(4L, items.get(3).getMatchedProductId()); // Amul Butter
        assertEquals(5L, items.get(4).getMatchedProductId()); // Milk
        assertEquals(8L, items.get(5).getMatchedProductId()); // Sugar
    }

    @Test
    void testHindiMarathiSynonyms() {
        String hindiSlip = "1. Doodh 2 packet\n" +
                "2. Sakhar 1kg\n" +
                "3. Tel 2L\n" +
                "4. Chawal 5kg\n" +
                "5. Surf 1kg";

        HandwrittenListParseResponse response = geminiAiService.processHandwrittenSlipWithGemini(null, hindiSlip, null, null);

        assertNotNull(response);
        for (ParsedListItemDTO item : response.getItems()) {
            System.out.println("ITEM: " + item.getRawText() + " -> " + item.getExtractedProductName() + " | FOUND=" + item.isFound() + " | MATCHED=" + item.getMatchedProductName());
        }
        assertEquals(5, response.getTotalExtractedItems());
        assertEquals(5, response.getSuccessfullyMatchedItems());

        List<ParsedListItemDTO> items = response.getItems();
        assertEquals(5L, items.get(0).getMatchedProductId()); // Doodh -> Amul Milk
        assertEquals(8L, items.get(1).getMatchedProductId()); // Sakhar -> Sugar
        assertEquals(6L, items.get(2).getMatchedProductId()); // Tel -> Fortune Oil
        assertEquals(1L, items.get(3).getMatchedProductId()); // Chawal -> Basmati Rice
        assertEquals(7L, items.get(4).getMatchedProductId()); // Surf -> Surf Excel
    }

    @Test
    void testSingleLineCommaOrNumberedList() {
        String singleLine = "1. Rice 5kg 2. Atta 10kg 3. Salt 1kg";

        HandwrittenListParseResponse response = geminiAiService.processHandwrittenSlipWithGemini(null, singleLine, null, null);

        assertNotNull(response);
        assertEquals(3, response.getTotalExtractedItems());
        assertEquals(3, response.getSuccessfullyMatchedItems());
    }

    @Test
    void testDecimalsAndUnits() {
        String decimalSlip = "1. Basmati Rice 1.5kg\n" +
                "2. Sunflower Oil 0.5L\n" +
                "3. Tata Salt 2.5kg";

        HandwrittenListParseResponse response = geminiAiService.processHandwrittenSlipWithGemini(null, decimalSlip, null, null);

        assertNotNull(response);
        assertEquals(3, response.getTotalExtractedItems());
        assertEquals(3, response.getSuccessfullyMatchedItems());

        assertEquals(1L, response.getItems().get(0).getMatchedProductId());
        assertEquals(2, response.getItems().get(0).getExtractedQuantity()); // 1.5 rounded to 2

        assertEquals(6L, response.getItems().get(1).getMatchedProductId());
        assertEquals(1, response.getItems().get(1).getExtractedQuantity()); // 0.5 rounded to 1

        assertEquals(3L, response.getItems().get(2).getMatchedProductId());
        assertEquals(3, response.getItems().get(2).getExtractedQuantity()); // 2.5 rounded to 3
    }
}
