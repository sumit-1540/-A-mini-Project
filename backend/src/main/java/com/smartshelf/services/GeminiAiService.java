package com.smartshelf.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartshelf.dto.HandwrittenListParseResponse;
import com.smartshelf.dto.ParsedListItemDTO;
import com.smartshelf.models.Product;
import com.smartshelf.repositories.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class GeminiAiService {

    private static final Logger log = LoggerFactory.getLogger(GeminiAiService.class);

    private final ProductRepository productRepository;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${app.gemini.api-key:${GEMINI_API_KEY:}}")
    private String configuredApiKey;

    @Value("${app.gemini.model:gemini-1.5-flash}")
    private String configuredModel;

    // Comprehensive Hindi, Marathi, Hinglish, and English grocery synonym mapping
    private static final Map<String, List<String>> GROCERY_SYNONYMS = new HashMap<>();

    static {
        // Dairy & Milk
        addSynonym("doodh", "milk", "taaza", "toned");
        addSynonym("dudh", "milk", "taaza", "toned");
        addSynonym("dudha", "milk", "taaza", "toned");
        addSynonym("milk", "doodh", "taaza", "toned");

        // Rice & Grains
        addSynonym("chawal", "rice", "basmati", "biryani");
        addSynonym("tandul", "rice", "basmati", "chawal");
        addSynonym("rice", "chawal", "basmati", "biryani");
        addSynonym("basmati", "rice", "chawal");

        // Atta & Flour
        addSynonym("aata", "atta", "flour", "sharbati");
        addSynonym("atta", "aata", "flour", "sharbati");
        addSynonym("gehu", "atta", "flour", "sharbati");
        addSynonym("flour", "atta", "aata", "sharbati");

        // Salt
        addSynonym("namak", "salt", "mith");
        addSynonym("mith", "salt", "namak");
        addSynonym("salt", "namak", "mith");

        // Sugar
        addSynonym("chini", "sugar", "sakhar", "shakkar");
        addSynonym("shakkar", "sugar", "chini", "sakhar");
        addSynonym("sakhar", "sugar", "chini", "shakkar");
        addSynonym("sugar", "sakhar", "chini", "shakkar");

        // Cooking Oil
        addSynonym("tel", "oil", "sunflower", "refined", "sunlite");
        addSynonym("oil", "tel", "sunflower", "refined", "sunlite");
        addSynonym("sarson", "mustard", "oil", "tel");
        addSynonym("sunflower", "oil", "sunlite", "refined");

        // Tea
        addSynonym("chai", "tea", "chaha", "patti");
        addSynonym("chaha", "tea", "chai", "patti");
        addSynonym("tea", "chai", "chaha", "patti");
        addSynonym("patti", "tea", "chai");

        // Bath Soap
        addSynonym("sabun", "soap", "bathing");
        addSynonym("soap", "sabun", "bathing");

        // Detergent
        addSynonym("surf", "detergent", "powder", "easy wash");
        addSynonym("detergent", "surf", "powder", "easy wash");
        addSynonym("nirma", "detergent", "surf", "powder");
        addSynonym("tide", "detergent", "surf", "powder");
        addSynonym("ariel", "detergent", "surf", "powder");

        // Biscuits & Cookies
        addSynonym("biskut", "biscuit", "biscuits", "cookies");
        addSynonym("biscuit", "biscuits", "biskut", "cookies");
        addSynonym("biscuits", "biscuit", "biskut", "cookies");
        addSynonym("cookie", "cookies", "biscuit");
        addSynonym("cookies", "cookie", "biscuit");

        // Butter
        addSynonym("makkhan", "butter", "pasteurized");
        addSynonym("makhan", "butter", "pasteurized");
        addSynonym("butter", "makhan", "makkhan", "pasteurized");

        // Ghee
        addSynonym("ghee", "desi ghee", "tup");
        addSynonym("tup", "ghee", "desi ghee");

        // Eggs
        addSynonym("ande", "egg", "eggs", "brown eggs");
        addSynonym("anda", "egg", "eggs", "brown eggs");
        addSynonym("egg", "ande", "eggs", "brown eggs");
        addSynonym("eggs", "ande", "egg", "brown eggs");

        // Noodles
        addSynonym("noodle", "noodles", "maggi");
        addSynonym("noodles", "noodle", "maggi");
        addSynonym("maggi", "noodles", "masala noodles");

        // Snacks
        addSynonym("chips", "wafers", "namkeen");

        // Coffee
        addSynonym("coffee", "instant coffee");

        // Toothpaste
        addSynonym("paste", "toothpaste", "dental");
        addSynonym("toothpaste", "paste", "dental");

        // Dishwash
        addSynonym("bartan", "dishwash", "gel");
        addSynonym("dishwash", "bartan", "gel");

        // Pulses / Dal
        addSynonym("dal", "toor", "tuvar", "daal", "pulses", "arhar");
        addSynonym("daal", "toor", "dal", "pulses");
        addSynonym("toor", "dal", "daal", "tuvar");
        addSynonym("tuvar", "toor", "dal", "daal");

        // Spices & Oats
        addSynonym("oats", "wholegrain", "cereal");
        addSynonym("paneer", "malai paneer");
        addSynonym("masala", "garam masala");
    }

    private static void addSynonym(String key, String... values) {
        GROCERY_SYNONYMS.put(key.toLowerCase(), Arrays.asList(values));
    }

    public GeminiAiService(ProductRepository productRepository) {
        this.productRepository = productRepository;
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    public HandwrittenListParseResponse processHandwrittenSlipWithGemini(MultipartFile file, String manualTextInput, String customApiKey, String customModel) {
        String apiKey = (customApiKey != null && !customApiKey.trim().isEmpty()) ? customApiKey.trim() : configuredApiKey;
        String model = (customModel != null && !customModel.trim().isEmpty()) ? customModel.trim() : configuredModel;

        String fileName = (file != null && !file.isEmpty()) ? file.getOriginalFilename() : "Shopping Slip";
        List<Product> catalog = productRepository.findByActiveTrue();

        // 1. If Gemini API Key is configured and valid, call Google Gemini Generative Vision API
        if (apiKey != null && !apiKey.trim().isEmpty() && !apiKey.equalsIgnoreCase("YOUR_GEMINI_API_KEY")) {
            try {
                return callGeminiApi(file, manualTextInput, fileName, catalog, apiKey, model);
            } catch (Exception e) {
                log.error("Gemini API call failed, switching to local intelligent matcher: {}", e.getMessage(), e);
                // Fall back gracefully to local catalog matcher with clear indicator of the error
                return processLocalIntelligentFallback(file, manualTextInput, fileName, catalog, "SmartShelf Fallback Parser (Gemini: " + e.getMessage() + ")");
            }
        }

        // 2. Client/Server Intelligent Local Parser
        return processLocalIntelligentFallback(file, manualTextInput, fileName, catalog, "SmartShelf Intelligent Inventory Matcher");
    }

    private HandwrittenListParseResponse callGeminiApi(MultipartFile file, String manualTextInput, String fileName, List<Product> catalog, String apiKey, String model) throws Exception {
        String cleanModel = model != null ? model.trim() : "gemini-1.5-flash";
        if (cleanModel.startsWith("models/")) {
            cleanModel = cleanModel.substring(7);
        }

        String endpoint = String.format("https://generativelanguage.googleapis.com/v1beta/models/%s:generateContent?key=%s", cleanModel, apiKey);

        // Build Catalog context for Gemini
        StringBuilder catalogContext = new StringBuilder();
        catalogContext.append("STORE INVENTORY CATALOG:\n");
        for (Product p : catalog) {
            catalogContext.append(String.format("- ID: %d | Name: \"%s\" | SKU: %s | Price: ₹%.2f | Stock: %d %s | Category: %s\n",
                    p.getId(), p.getName(), p.getSku(), p.getSellingPrice(), p.getCurrentStock(), p.getUnit(),
                    p.getCategory() != null ? p.getCategory().getName() : "General"));
        }

        String systemPrompt = "You are SmartShelf AI Vision Assistant, an expert OCR and grocery item parsing AI for Indian supermarket stores.\n" +
                "Your task is to transcribe this handwritten shopping list / customer slip, extract each line item (product name, quantity, unit), " +
                "and find the best matching product ID from the STORE INVENTORY CATALOG provided below.\n\n" +
                catalogContext + "\n\n" +
                "STRICT INSTRUCTIONS:\n" +
                "1. If an image is attached, perform optical character recognition (OCR) on all handwritten or printed text.\n" +
                "2. If raw text is provided, parse every line item.\n" +
                "3. Extract the requested quantity (default 1) and unit (e.g. kg, g, L, ml, pkt, pcs, bag, box).\n" +
                "4. Match each item to the closest product from the STORE INVENTORY CATALOG. If no good match, leave matchedProductId null.\n" +
                "5. Return ONLY a valid JSON object matching this schema:\n" +
                "{\n" +
                "  \"rawExtractedText\": \"Full transcription of all text found on the slip\",\n" +
                "  \"items\": [\n" +
                "    {\n" +
                "      \"rawText\": \"original item text line\",\n" +
                "      \"extractedProductName\": \"recognized item name\",\n" +
                "      \"extractedQuantity\": 1,\n" +
                "      \"extractedUnit\": \"kg\",\n" +
                "      \"matchedProductId\": 12,\n" +
                "      \"matchConfidence\": 0.95\n" +
                "    }\n" +
                "  ],\n" +
                "  \"unmatchedLines\": [\"any unrecognized or unavailable items\"]\n" +
                "}";

        Map<String, Object> requestBody = new HashMap<>();
        List<Map<String, Object>> contents = new ArrayList<>();
        Map<String, Object> contentPart = new HashMap<>();
        List<Map<String, Object>> parts = new ArrayList<>();

        // Add prompt part
        Map<String, Object> textPart = new HashMap<>();
        if (manualTextInput != null && !manualTextInput.trim().isEmpty()) {
            textPart.put("text", systemPrompt + "\n\nCUSTOMER SLIP INPUT TEXT:\n" + manualTextInput);
        } else {
            textPart.put("text", systemPrompt + "\n\nPlease OCR and parse the grocery list image attached.");
        }
        parts.add(textPart);

        // Add image part if image file uploaded
        if (file != null && !file.isEmpty()) {
            byte[] imageBytes = file.getBytes();
            String base64Image = Base64.getEncoder().encodeToString(imageBytes);

            String rawMime = file.getContentType();
            String mimeType = "image/jpeg";
            if (rawMime != null && !rawMime.isBlank() && !rawMime.equals("application/octet-stream")) {
                if (rawMime.equalsIgnoreCase("image/jpg") || rawMime.equalsIgnoreCase("image/pjpeg")) {
                    mimeType = "image/jpeg";
                } else if (rawMime.equalsIgnoreCase("image/x-png") || rawMime.equalsIgnoreCase("image/png")) {
                    mimeType = "image/png";
                } else if (rawMime.equalsIgnoreCase("image/webp")) {
                    mimeType = "image/webp";
                } else {
                    mimeType = rawMime;
                }
            } else {
                String orig = file.getOriginalFilename() != null ? file.getOriginalFilename().toLowerCase() : "";
                if (orig.endsWith(".png")) mimeType = "image/png";
                else if (orig.endsWith(".webp")) mimeType = "image/webp";
                else mimeType = "image/jpeg";
            }

            Map<String, Object> inlineData = new HashMap<>();
            inlineData.put("mimeType", mimeType);
            inlineData.put("data", base64Image);

            Map<String, Object> imagePart = new HashMap<>();
            imagePart.put("inlineData", inlineData);
            parts.add(imagePart);
        }

        contentPart.put("parts", parts);
        contents.add(contentPart);
        requestBody.put("contents", contents);

        Map<String, Object> generationConfig = new HashMap<>();
        generationConfig.put("responseMimeType", "application/json");
        generationConfig.put("temperature", 0.2);
        requestBody.put("generationConfig", generationConfig);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(endpoint, entity, String.class);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            JsonNode rootNode = objectMapper.readTree(response.getBody());
            JsonNode candidate = rootNode.path("candidates").path(0).path("content").path("parts").path(0).path("text");
            if (candidate.isMissingNode() || candidate.asText().isBlank()) {
                throw new RuntimeException("Empty response received from Gemini AI model");
            }
            String rawOutput = candidate.asText().trim();

            // Strip Markdown fences e.g. ```json ... ```
            if (rawOutput.startsWith("```json")) {
                rawOutput = rawOutput.substring(7);
            } else if (rawOutput.startsWith("```")) {
                rawOutput = rawOutput.substring(3);
            }
            if (rawOutput.endsWith("```")) {
                rawOutput = rawOutput.substring(0, rawOutput.length() - 3);
            }
            rawOutput = rawOutput.trim();

            // Robust JSON extraction (find first '{' or '[' and matching closing bracket)
            int firstBrace = rawOutput.indexOf('{');
            int lastBrace = rawOutput.lastIndexOf('}');
            int firstBracket = rawOutput.indexOf('[');
            int lastBracket = rawOutput.lastIndexOf(']');

            String jsonText = rawOutput;
            if (firstBrace != -1 && lastBrace > firstBrace && (firstBracket == -1 || firstBrace < firstBracket)) {
                jsonText = rawOutput.substring(firstBrace, lastBrace + 1);
            } else if (firstBracket != -1 && lastBracket > firstBracket) {
                jsonText = rawOutput.substring(firstBracket, lastBracket + 1);
            }
            jsonText = jsonText.trim();

            JsonNode geminiResult = objectMapper.readTree(jsonText);
            String rawExtractedText = geminiResult.has("rawExtractedText")
                    ? geminiResult.path("rawExtractedText").asText()
                    : (manualTextInput != null ? manualTextInput : "");

            List<ParsedListItemDTO> parsedItems = new ArrayList<>();
            BigDecimal estimatedTotal = BigDecimal.ZERO;
            int matchedCount = 0;

            Map<Long, Product> productMap = new HashMap<>();
            for (Product p : catalog) {
                productMap.put(p.getId(), p);
            }

            // Handle both JSON Object with "items" and root JSON Array
            JsonNode itemsNode = geminiResult.isArray() ? geminiResult : geminiResult.path("items");
            if (itemsNode.isArray()) {
                for (JsonNode itemNode : itemsNode) {
                    String rawText = itemNode.path("rawText").asText();
                    String extractedName = itemNode.path("extractedProductName").asText();
                    int qty = itemNode.path("extractedQuantity").asInt(1);
                    if (qty <= 0) qty = 1;
                    String unit = itemNode.path("extractedUnit").asText("pcs");
                    Long matchedId = itemNode.hasNonNull("matchedProductId") ? itemNode.path("matchedProductId").asLong() : null;
                    double confidence = itemNode.path("matchConfidence").asDouble(0.95);

                    Product matchedProduct = matchedId != null ? productMap.get(matchedId) : null;
                    if (matchedProduct == null) {
                        matchedProduct = findBestCatalogProduct(extractedName != null && !extractedName.isBlank() ? extractedName : rawText, catalog);
                    }

                    ParsedListItemDTO itemDTO = ParsedListItemDTO.builder()
                            .rawText(rawText)
                            .extractedProductName(extractedName != null && !extractedName.isBlank() ? extractedName : (matchedProduct != null ? matchedProduct.getName() : rawText))
                            .extractedQuantity(qty)
                            .extractedUnit(unit)
                            .found(matchedProduct != null)
                            .matchConfidence(matchedProduct != null ? confidence : 0.0)
                            .build();

                    if (matchedProduct != null) {
                        matchedCount++;
                        itemDTO.setMatchedProductId(matchedProduct.getId());
                        itemDTO.setMatchedProductName(matchedProduct.getName());
                        itemDTO.setMatchedSku(matchedProduct.getSku());
                        itemDTO.setMatchedPrice(matchedProduct.getSellingPrice());
                        itemDTO.setAvailableStock(matchedProduct.getCurrentStock());

                        BigDecimal lineTotal = matchedProduct.getSellingPrice().multiply(BigDecimal.valueOf(qty));
                        itemDTO.setEstimatedLineTotal(lineTotal);
                        estimatedTotal = estimatedTotal.add(lineTotal);
                    }

                    parsedItems.add(itemDTO);
                }
            }

            if (rawExtractedText.isBlank() && !parsedItems.isEmpty()) {
                StringBuilder sb = new StringBuilder();
                for (ParsedListItemDTO item : parsedItems) {
                    sb.append(item.getRawText() != null && !item.getRawText().isBlank() ? item.getRawText() : item.getExtractedProductName()).append("\n");
                }
                rawExtractedText = sb.toString().trim();
            }

            List<String> unmatchedLines = new ArrayList<>();
            JsonNode unmatchedNode = geminiResult.path("unmatchedLines");
            if (unmatchedNode.isArray()) {
                for (JsonNode u : unmatchedNode) {
                    unmatchedLines.add(u.asText());
                }
            }

            return HandwrittenListParseResponse.builder()
                    .originalFileName(fileName)
                    .rawExtractedText(rawExtractedText)
                    .totalExtractedItems(parsedItems.size())
                    .successfullyMatchedItems(matchedCount)
                    .estimatedTotal(estimatedTotal)
                    .items(parsedItems)
                    .unmatchedLines(unmatchedLines)
                    .aiModelUsed("Google Gemini " + cleanModel + " (Vision AI)")
                    .build();
        }

        throw new RuntimeException("Unexpected response from Gemini API: " + response.getStatusCode());
    }

    public HandwrittenListParseResponse processLocalIntelligentFallback(MultipartFile file, String manualTextInput, String fileName, List<Product> catalog, String modelName) {
        String extractedText = manualTextInput != null ? manualTextInput.trim() : "";

        // If no text was provided at all and no file was uploaded, populate demo items
        if (extractedText.isEmpty() && (file == null || file.isEmpty())) {
            extractedText = "1. Basmati Rice 5kg\n" +
                    "2. Aashirvaad Atta 10kg\n" +
                    "3. Tata Salt 1kg\n" +
                    "4. Amul Butter 500g\n" +
                    "5. Amul Milk 2L\n" +
                    "6. Fortune Sunflower Oil 1L\n" +
                    "7. Red Label Tea 500g\n" +
                    "8. Parle G 1kg\n" +
                    "9. Maggi Noodles 1 pack";
        }

        List<ParsedListItemDTO> parsedItems = new ArrayList<>();
        List<String> rawLinesList = extractIndividualItems(extractedText);
        BigDecimal estimatedTotal = BigDecimal.ZERO;
        int matchedCount = 0;
        List<String> unmatched = new ArrayList<>();

        for (String rawLine : rawLinesList) {
            String line = rawLine.trim();
            if (line.isEmpty()) continue;

            // Remove leading bullet/numbering e.g. "1.", "1)", "1 -", "-", "•"
            String cleanedLine = line.replaceFirst("^(\\d+[.)\\-:]\\s*|[•\\-*–]\\s*)", "").trim();

            // Extract Quantity and Unit
            ExtractedQuantityUnit qu = parseQuantityAndUnit(cleanedLine);
            String productNameQuery = qu.productName;
            int qty = qu.quantity;
            String unit = qu.unit;

            if (productNameQuery == null || productNameQuery.isBlank()) {
                productNameQuery = cleanedLine;
            }

            Product bestMatch = findBestCatalogProduct(productNameQuery, catalog);

            ParsedListItemDTO itemDTO = ParsedListItemDTO.builder()
                    .rawText(rawLine)
                    .extractedProductName(productNameQuery)
                    .extractedQuantity(qty)
                    .extractedUnit(unit)
                    .found(bestMatch != null)
                    .matchConfidence(bestMatch != null ? 0.95 : 0.0)
                    .build();

            if (bestMatch != null) {
                // If product title has pre-packaged weight matching extracted qty (e.g. "Basmati Rice 5kg" matched "India Gate Basmati Rice 5kg")
                String bmName = bestMatch.getName().toLowerCase();
                if ((bmName.contains(qty + "kg") || bmName.contains(qty + " kg") || bmName.contains(qty + "l") || bmName.contains(qty + " l"))
                        && !cleanedLine.toLowerCase().contains("bag") && !cleanedLine.toLowerCase().contains("pkt") && !cleanedLine.toLowerCase().contains("packet")) {
                    qty = 1;
                    itemDTO.setExtractedQuantity(1);
                }

                matchedCount++;
                itemDTO.setMatchedProductId(bestMatch.getId());
                itemDTO.setMatchedProductName(bestMatch.getName());
                itemDTO.setMatchedSku(bestMatch.getSku());
                itemDTO.setMatchedPrice(bestMatch.getSellingPrice());
                itemDTO.setAvailableStock(bestMatch.getCurrentStock());

                BigDecimal lineTotal = bestMatch.getSellingPrice().multiply(BigDecimal.valueOf(qty));
                itemDTO.setEstimatedLineTotal(lineTotal);
                estimatedTotal = estimatedTotal.add(lineTotal);
            } else {
                unmatched.add(rawLine);
            }

            parsedItems.add(itemDTO);
        }

        if (parsedItems.isEmpty() && file != null && !file.isEmpty()) {
            unmatched.add("Slip image received: transcribe using Tesseract OCR or enter items above.");
        }

        return HandwrittenListParseResponse.builder()
                .originalFileName(fileName)
                .rawExtractedText(extractedText)
                .totalExtractedItems(parsedItems.size())
                .successfullyMatchedItems(matchedCount)
                .estimatedTotal(estimatedTotal)
                .items(parsedItems)
                .unmatchedLines(unmatched)
                .aiModelUsed(modelName)
                .build();
    }

    private List<String> extractIndividualItems(String text) {
        List<String> result = new ArrayList<>();
        if (text == null || text.isBlank()) return result;

        String[] lines = text.split("\\r?\\n");
        for (String rawLine : lines) {
            String l = rawLine.trim();
            if (l.isEmpty()) continue;

            // If line contains multiple items separated by numbering e.g. "1. Rice 5kg 2. Sugar 1kg"
            if (l.matches(".*\\b\\d+[.)]\\s+.*\\b\\d+[.)]\\s+.*")) {
                String[] parts = l.split("(?=\\b\\d+[.)]\\s+)");
                for (String p : parts) {
                    if (!p.trim().isEmpty()) result.add(p.trim());
                }
            } else if (l.matches(".*,\\s*\\d+[.)].*")) {
                String[] parts = l.split(",\\s*(?=\\d+[.)])");
                for (String p : parts) {
                    if (!p.trim().isEmpty()) result.add(p.trim());
                }
            } else if (l.contains(",") && !l.toLowerCase().contains("buy") && !l.toLowerCase().contains("pack")) {
                String[] parts = l.split(",");
                if (parts.length > 1) {
                    for (String p : parts) {
                        if (!p.trim().isEmpty()) result.add(p.trim());
                    }
                } else {
                    result.add(l);
                }
            } else {
                result.add(l);
            }
        }
        return result;
    }

    private static class ExtractedQuantityUnit {
        int quantity = 1;
        String unit = "pcs";
        String productName = "";
    }

    private ExtractedQuantityUnit parseQuantityAndUnit(String input) {
        ExtractedQuantityUnit res = new ExtractedQuantityUnit();
        if (input == null) return res;
        String clean = input.replaceAll("[:–—]", " ").trim();

        // 1. Check for suffix: "Rice 5kg", "Rice 1.5 kg", "Tata Salt 2 packets"
        Pattern suffixPattern = Pattern.compile("(.*?)[\\s\\-–]+(\\d+(?:\\.\\d+)?(?:/\\d+)?)\\s*(kg|kgs|kilo|kilos|g|gm|gms|gram|grams|l|ltr|ltrs|litre|litres|ml|pkt|pkts|packet|packets|pack|packs|pcs|pieces|pc|tin|tins|can|cans|jar|jars|bottle|bottles|dozen|darjan)?$", Pattern.CASE_INSENSITIVE);
        Matcher mSuffix = suffixPattern.matcher(clean);
        if (mSuffix.find()) {
            String name = mSuffix.group(1).trim();
            if (!name.isEmpty()) {
                int parsedQty = parseQuantityValue(mSuffix.group(2));
                String rawUnit = mSuffix.group(3);
                String normalizedUnit = rawUnit != null ? normalizeUnit(rawUnit) : "pcs";

                // If suffix is large grams/ml (e.g. 100g, 250g, 500g, 200ml, 500ml), treat as package net weight
                if (("g".equalsIgnoreCase(normalizedUnit) || "ml".equalsIgnoreCase(normalizedUnit)) && parsedQty >= 25) {
                    res.productName = clean; // Keep full name for exact SKU match
                    res.quantity = 1;
                    res.unit = "pcs";
                } else {
                    res.productName = name;
                    res.quantity = parsedQty;
                    res.unit = normalizedUnit;
                }
                return res;
            }
        }

        // 2. Check for prefix: "5kg Rice", "1.5 kg Rice", "2 packets Milk", "1 dozen eggs"
        Pattern prefixPattern = Pattern.compile("^(\\d+(?:\\.\\d+)?(?:/\\d+)?)\\s*(kg|kgs|kilo|kilos|g|gm|gms|gram|grams|l|ltr|ltrs|litre|litres|ml|pkt|pkts|packet|packets|pack|packs|pcs|pieces|pc|tin|tins|can|cans|jar|jars|bottle|bottles|dozen|darjan)?\\s+(?:of\\s+)?(.*?)$", Pattern.CASE_INSENSITIVE);
        Matcher mPrefix = prefixPattern.matcher(clean);
        if (mPrefix.find()) {
            int parsedQty = parseQuantityValue(mPrefix.group(1));
            String rawUnit = mPrefix.group(2);
            String normalizedUnit = rawUnit != null ? normalizeUnit(rawUnit) : "pcs";

            if (("g".equalsIgnoreCase(normalizedUnit) || "ml".equalsIgnoreCase(normalizedUnit)) && parsedQty >= 25) {
                res.productName = clean;
                res.quantity = 1;
                res.unit = "pcs";
            } else {
                res.quantity = parsedQty;
                res.unit = normalizedUnit;
                res.productName = mPrefix.group(3).trim();
            }
            return res;
        }

        // 3. Fallback: general number search
        Pattern generalPattern = Pattern.compile("(\\d+(?:\\.\\d+)?(?:/\\d+)?)\\s*(kg|kgs|g|gm|l|ltr|pkt|pack|pcs)?", Pattern.CASE_INSENSITIVE);
        Matcher mGen = generalPattern.matcher(clean);
        if (mGen.find()) {
            res.quantity = parseQuantityValue(mGen.group(1));
            if (mGen.group(2) != null) {
                res.unit = normalizeUnit(mGen.group(2));
            }
            res.productName = clean.substring(0, mGen.start()) + " " + clean.substring(mGen.end());
            res.productName = res.productName.replaceAll("\\s+", " ").trim();
            return res;
        }

        res.productName = clean;
        return res;
    }

    private int parseQuantityValue(String raw) {
        if (raw == null || raw.isBlank()) return 1;
        try {
            if (raw.contains("/")) {
                String[] parts = raw.split("/");
                double num = Double.parseDouble(parts[0]);
                double den = Double.parseDouble(parts[1]);
                return (int) Math.max(1, Math.round(num / den));
            }
            double val = Double.parseDouble(raw);
            return (int) Math.max(1, Math.round(val));
        } catch (Exception e) {
            return 1;
        }
    }

    private String normalizeUnit(String rawUnit) {
        if (rawUnit == null) return "pcs";
        String u = rawUnit.toLowerCase().trim();
        if (u.startsWith("kg") || u.startsWith("kilo")) return "kg";
        if (u.startsWith("g") || u.startsWith("gram")) return "g";
        if (u.startsWith("l") || u.startsWith("ltr") || u.startsWith("litre")) return "L";
        if (u.startsWith("ml")) return "ml";
        if (u.startsWith("pkt") || u.startsWith("pack")) return "pkt";
        if (u.startsWith("pc")) return "pcs";
        if (u.startsWith("doz") || u.startsWith("darjan")) return "dozen";
        if (u.startsWith("tin")) return "tin";
        if (u.startsWith("can")) return "can";
        if (u.startsWith("jar")) return "jar";
        if (u.startsWith("bot")) return "bottle";
        return "pcs";
    }

    public Product findBestCatalogProduct(String query, List<Product> catalog) {
        if (query == null || query.trim().isEmpty() || catalog == null || catalog.isEmpty()) return null;

        String cleanQuery = query.toLowerCase()
                .replaceAll("[-_•*.,/()\\[\\]:;!?]", " ")
                .replaceAll("\\s+", " ")
                .trim();

        if (cleanQuery.isEmpty()) return null;

        Product bestProduct = null;
        double highestScore = 0.0;

        String[] queryTokens = cleanQuery.split("\\s+");

        for (Product product : catalog) {
            String pName = product.getName().toLowerCase();
            String pSku = product.getSku() != null ? product.getSku().toLowerCase() : "";
            String pBrand = product.getBrand() != null ? product.getBrand().toLowerCase() : "";
            String pCat = product.getCategory() != null ? product.getCategory().getName().toLowerCase() : "";

            // 1. Exact match
            if (pName.equalsIgnoreCase(cleanQuery) || pSku.equalsIgnoreCase(cleanQuery)) {
                return product;
            }

            // 2. Direct substring containment (prioritize when query >= 4 chars)
            if (cleanQuery.length() >= 4 && pName.contains(cleanQuery)) {
                double containmentScore = 1.6 + ((double) cleanQuery.length() / pName.length());
                if (containmentScore > highestScore) {
                    highestScore = containmentScore;
                    bestProduct = product;
                }
            }

            double score = 0.0;

            // 3. Token by token matching with synonym expansion
            for (String token : queryTokens) {
                if (token.length() <= 1) continue;

                boolean tokenMatched = false;

                // Direct match in Name, Brand or Category
                if (pName.contains(token)) {
                    score += 1.0;
                    tokenMatched = true;
                } else if (pBrand.contains(token)) {
                    score += 0.9;
                    tokenMatched = true;
                } else if (pCat.contains(token)) {
                    score += 0.6;
                    tokenMatched = true;
                }

                // Check Synonyms
                if (GROCERY_SYNONYMS.containsKey(token)) {
                    List<String> synonyms = GROCERY_SYNONYMS.get(token);
                    for (int i = 0; i < synonyms.size(); i++) {
                        String syn = synonyms.get(i);
                        double weight = (i == 0) ? 1.5 : 1.0;
                        if (pName.contains(syn)) {
                            score += weight;
                            tokenMatched = true;
                        } else if (pCat.contains(syn)) {
                            score += (weight * 0.7);
                            tokenMatched = true;
                        }
                    }
                }

                // Fuzzy Levenshtein match for typos (e.g. basmti -> basmati, detol -> dettol)
                if (!tokenMatched && token.length() >= 3) {
                    for (String pToken : pName.split("\\s+")) {
                        if (pToken.length() >= 3 && computeSimilarity(token, pToken) >= 0.72) {
                            score += 0.8;
                            tokenMatched = true;
                            break;
                        }
                    }
                }
            }

            // Brand Boost: If query mentions the exact brand
            if (!pBrand.isEmpty() && cleanQuery.contains(pBrand)) {
                score += 0.6;
            }

            double normalizedScore = queryTokens.length > 0 ? (score / queryTokens.length) : 0.0;

            if (normalizedScore > highestScore && normalizedScore >= 0.35) {
                highestScore = normalizedScore;
                bestProduct = product;
            }
        }

        return bestProduct;
    }

    private double computeSimilarity(String s1, String s2) {
        int dist = computeLevenshteinDistance(s1, s2);
        int maxLen = Math.max(s1.length(), s2.length());
        if (maxLen == 0) return 1.0;
        return 1.0 - ((double) dist / maxLen);
    }

    private int computeLevenshteinDistance(String s1, String s2) {
        int[][] dp = new int[s1.length() + 1][s2.length() + 1];

        for (int i = 0; i <= s1.length(); i++) dp[i][0] = i;
        for (int j = 0; j <= s2.length(); j++) dp[0][j] = j;

        for (int i = 1; i <= s1.length(); i++) {
            for (int j = 1; j <= s2.length(); j++) {
                int cost = (s1.charAt(i - 1) == s2.charAt(j - 1)) ? 0 : 1;
                dp[i][j] = Math.min(
                        Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1),
                        dp[i - 1][j - 1] + cost
                );
            }
        }
        return dp[s1.length()][s2.length()];
    }
}
